// Advanced Programming. Andrzej Wasowski. IT University
// To execute this example, run "sbt run" or "sbt test" in the root dir of the project
// Spark needs not to be installed (sbt takes care of it)

import org.apache.spark.ml.feature._
import org.apache.spark.sql.{Dataset,DataFrame}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object Main {

	type Embedding       = (String, List[Double])
	type ParsedReview    = (Integer, String, Double)

	org.apache.log4j.Logger getLogger "org"  setLevel (org.apache.log4j.Level.WARN)
	org.apache.log4j.Logger getLogger "akka" setLevel (org.apache.log4j.Level.WARN)
	val spark =  SparkSession.builder
		.appName ("Sentiment")
		.master  ("local[9]")
		.getOrCreate

  import spark.implicits._

	val reviewSchema = StructType(Array(
			StructField ("reviewText", StringType, nullable=false),
			StructField ("overall",    DoubleType, nullable=false),
			StructField ("summary",    StringType, nullable=false)))

	// Read file and merge the text abd summary into a single text column

	def loadReviews (path: String): Dataset[ParsedReview] =
		spark
			.read
			.schema (reviewSchema)
			.json (path)
			.rdd
			.zipWithUniqueId
			.map[(Integer,String,Double)] { case (row,id) => (id.toInt, s"${row getString 2} ${row getString 0}", row getDouble 1) }
			.toDS
			.withColumnRenamed ("_1", "id" )
			.withColumnRenamed ("_2", "text")
			.withColumnRenamed ("_3", "overall")
			.as[ParsedReview]

  // Load the GLoVe embeddings file

  def loadGlove (path: String): Dataset[Embedding] =
		spark
			.read
			.text (path)
      .map  { _ getString 0 split " " }
      .map  (r => (r.head, r.tail.toList.map (_.toDouble))) // yuck!
			.withColumnRenamed ("_1", "word" )
			.withColumnRenamed ("_2", "vec")
			.as[Embedding]

  def main(args: Array[String]) = {

    val glove  = loadGlove ("trainData/glove.6B.50d.txt")
    val reviews = loadReviews ("trainData/reviews.json")

	val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
	val tokenized = tokenizer.transform(reviews)

	val words = tokenized.select("id","overall","words").withColumn("word",explode(col("words")))

	val foo = words.join(glove, "word")
	
	val baz = foo.select("id", "vec").as[(Int, Array[Double])].groupByKey(k => k._1).mapGroups((k, vec) => (k, vec.map(_._2).toList.transpose.map(_.sum))).withColumnRenamed("_1","id").withColumnRenamed("_2","vec")

	// size of words

	val size = baz.select("id","vec").as[(Int, Array[Double])].groupByKey(k => k._1).mapGroups((k, group) => {val grp = group.toList; (k, grp.map(_._2.map(_ => grp.size)))}).withColumnRenamed("_1","id").withColumnRenamed("_2","vec")
	
	size.show

	// divide all elements of 

	//val bar = baz.select("id","vec").as[(Int, Array[Double])].map((k, vec) => (k,
	//vec.map(_._2).toList.transpose.map(_.sum)))











	// val baz = foo.select("id","word","vec").map(x).
	
	//groupByKey(k => k._1)

	//baz.show

	//foo.where("id","357").show

	//foo.select("word","vec").show

	//foo.select("vec").withColumn("sum", col("vec").flatmap(x => ))


	//words.select("word").withColumn("vector",  glove.filter(x => col(x["word"]) == col("word")))

	//foo.show

    // replace the following with the project code
    //glove.show
    //reviews.show

	//1. Tokenize all words in the 'text' column for the review variable
	//2. Map all the words using the vector dictionary from the glove variable
	//3. Sum all the internal vector variable for the 'review' and divide it with 
	//   the number of vectors for that review



		spark.stop
  }

}
