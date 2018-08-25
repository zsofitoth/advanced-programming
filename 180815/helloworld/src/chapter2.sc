object worksheet1 {

  def findFirst(ss: Array[String], key: String) : Int = {
    @annotation.tailrec
    def loop(n: Int) : Int =
      if (n >= ss.length) -1
      else if (ss(n) == key) n
      else loop(n + 1)

    loop(0)
  }

  def findFirstPol[A](as:Array[A], p: A => Boolean) : Int = {
    @annotation.tailrec
    def loop(n: Int) : Int =
      if (n >= as.length) -1
      else if (p(as(n))) n
      else loop(n + 1)

    loop(0)
  }

  val listOfNames = Array("Zsofi", "Lili", "Kristof", "Dan")
  findFirst(listOfNames, "Dan")

  val listOfNumbers = Array(1, 2, 4, 4)
  val foundNumber = findFirstPol(listOfNumbers,(x: Int) => x == 4)
  println(foundNumber)

  //2.1
  // write a recursive function to get the nth of the Fibonacci number
  // 0, 1, 1, 2, 3, 5
  // Your definition should use a local tail-recursive function.

  // 0 and 1 are the first two numbers in the sequence,
  // so we start the accumulators with those.
  // At every iteration, we add the two numbers to get the next one.

  def fib(n: Int): Int = {
    @annotation.tailrec
    def loop(n: Int, prev: Int, cur: Int): Int =
      if (n == 0) prev
      else loop(n - 1, cur, prev + cur)
    loop(n, 0, 1)
  }

  fib(2)

  //head recursion
  def factorial(n: Int) : Int = {
      if(n==0) 1
      else n*factorial(n-1)
  }

  factorial(3)

  //power
  def power(base: Int, power: Int) : Int = {
    @annotation.tailrec
    def loop(base: Int, power: Int, acc: Int) : Int = {
      if(power == 0) acc
      else loop(base, power-1, base*acc)
    }

    loop(base, power, 1)
  }

  power(3, 3)

  //2.2
  def isSorted[A](as: Array[A], ordered: (A, A) => Boolean) : Boolean = {
    @annotation.tailrec
    def loop(n: Int): Boolean = {
      if (n >= as.length - 1) true
      else if ( !ordered(as(n), as(n+1)) ) false
      else loop(n + 1)
    }

    loop(0)
  }

  def descending(x:Int, y:Int): Boolean = if (x >= y) true else false
  def ascending(x: Int, y: Int): Boolean = if(x <= y) true else false

  val isArraySorted = isSorted(Array(2,3,3), ascending)
  val isArraySortedDesc = isSorted(Array(4,3,2), descending)

  //partial
  def partial[A, B, C](a: A, f: (A, B) => C) : B => C = {
    //(b: B) => f(a, b)
    b => f(a, b)
  }

  //2.3
  def curry[A, B, C](f: (A, B) => C) : A => (B => C) = {
    //(a: A) => ((b: B) => f(a,b))
    a => (b => f(a, b))
  }

  //2.4
  def uncurry[A, B, C](f: A => B => C) : (A, B) => C = {
    //(a: A, b: B) => f(a)(b)
    (a, b) => f(a)(b)
  }

  //2.5
  def compose[A,B,C](f: B => C, g: A => B): A => C = {
    //output of the inner function becomes the input of the outer function
    //g takes A and outputs B, f takes B and outputs C
    //compose takes A and return C
    //(a: A) => f(g(a))
    a => f(g(a))
  }
}