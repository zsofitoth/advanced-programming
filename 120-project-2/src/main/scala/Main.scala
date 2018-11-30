// Advanced Programming. Andrzej Wasowski. IT University
// To execute this example, run "sbt run" or "sbt test" in the root dir of the project
// Spark needs not to be installed (sbt takes care of it)

import org.apache.spark.ml.feature._
import org.apache.spark.sql.{Dataset,DataFrame}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.linalg.{DenseVector, Vectors}


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

  def mapOverallToLabel(d: Double): Double = {
	  d match {
		  case 1.0 => 0.0
		  case 2.0 => 0.0
		  case 3.0 => 1.0
		  case _ => 2.0
	  }
  }  

  def main(args: Array[String]) = {

    val glove  = loadGlove ("trainData/glove/glove.6B.50d.txt")
    val reviews = loadReviews ("trainData/reviews/reviews.json")

	//1. Tokenize all words in the 'text' column for the review variable
	//2. Map all the words using the vector dictionary from the glove variable
	//3. Sum all the internal vector variable for the 'review' and divide it with 
	//   the number of vectors for that review

	val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
	val tokenized = tokenizer.transform(reviews)

	val words = tokenized.select("id","overall","words").withColumn("word",explode(col("words")))

	val gloveWords = words.join(glove, "word")
	
	val average = gloveWords.select("id", "vec")
		.as[(Int, List[Double])]
		.groupByKey(_._1)
		.mapGroups((k, iterator) => {
			val vector = iterator.map(_._2).toList;
			(k, Vectors.dense(vector.transpose.map(_.sum / vector.size).toArray))
		})
		.withColumnRenamed("_1","id")
		.withColumnRenamed("_2","vec")
	
	val data = tokenized.select("id","overall")
			.as[(Int, Double)]
			.groupByKey(_._1)
			.mapGroups((k, iterator) => {
				val label = iterator.map(a => mapOverallToLabel(a._2));
				(k, label)
			})
			.withColumnRenamed("_1","id")
			.withColumnRenamed("_2","overall")
			//.join(average, "id")
			//.withColumnRenamed ("vec", "features" )
			//.withColumnRenamed ("overall", "label" )

	data.show

	// Use the embeddings with known ratings to train a network (a multilayer perceptron classifier)
	// Split the data into train and test
	/*val splits = data.randomSplit(Array(0.9, 0.1), seed = 1234L)
	val train = splits(0)
	val test = splits(1)

	// features : average word embeddings (size of the vector?)
	// specify layers for the neural network:
	// input layer of size 4 (features), two intermediate of size 5 and 4
	// and output of size 3 (classes)

	val layers = Array[Int](30, 5, 4, 3)

	// create the trainer and set its parameters
	val trainer = new MultilayerPerceptronClassifier()
		.setLayers(layers)
		.setBlockSize(128)
		.setSeed(1234L)
		.setMaxIter(100)

	// train the model
	val model = trainer.fit(train)

	
	// Use the perceptron to predict ratings for another set of reviews (validation).
	// compute accuracy on the test set
	val result = model.transform(test)
	val predictionAndLabels = result.select("prediction", "label")
	val evaluator = new MulticlassClassificationEvaluator()
		.setMetricName("accuracy")

	println(s"Test set accuracy = ${evaluator.evaluate(predictionAndLabels)}")*/


	spark.stop
  }

}
