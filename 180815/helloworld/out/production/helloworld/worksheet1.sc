object worksheet1 {
  println("Hello World!")
  var x = 5
  val str = "This is a string"
  var odds = List(1, 3, 5, 7, 9)
  2 to 10

  var y = true
  val pi = 3.14159

  def add(a : Int, b : Int) : Int = a + b
  add(4,9)

  val num = 5
  val decimal = 5.5
  var z = 1 + 2 * 3.5
  List(1, 2, 4.5)
  List(1, true)
  List(1, true, "Zsofi")

  def addOne(x:Int) = x + 1

  //loops
  def findFirst(ss: Array[String], key: String) : Int = {
    @annotation.tailrec
    def loop(n: Int) : Int =
      if (n >= ss.length) -1
      else if (ss(n) == key) n
      else loop(n + 1)

    loop(0)
  }

  val listOfNames = Array("Zsofi", "Lili", "Kristof", "Dan")
  findFirst(listOfNames, "Dan")
}