// Advanced Programming, Exercises by A. Wąsowski, IT University of Copenhagen
//
// AUTHOR1: zsto@itu.dk
// AUTHOR2:
//
// Write ITU email addresses of both group members that contributed to
// the solution of the exercise (in lexicographic order).
//
// You should work with the file by following the associated exercise sheet
// (available in PDF from the course website).
//
// The file is meant to be compiled inside sbt, using the 'compile' command.
// To run the compiled file use the 'run' or 'runMain' command.
// To load the file int the REPL use the 'console' command.
// Now you can interactively experiment with your code.
//
// Continue solving exercises in the order presented in the PDF file. The file
// shall always compile, run, and pass tests, after you are done with each
// exercise (if you do them in order).  Please compile and test frequently.

// The extension of App allows writing the main method statements at the top
// level (the so called default constructor). For App objects they will be
// executed as if they were placed in the main method in Java.
package fpinscala

object Exercises extends App with ExercisesInterface {

  import fpinscala.List._

  // Exercise 3

  def fib (n: Int) : Int = {
    @annotation.tailrec
    def loop(n: Int, cur: Int, prev: Int) : Int = {
      if (n <= 1) prev
      else loop(n - 1, cur + prev, cur)
    }

    loop(n, 1, 0)
  }

  // Exercise 4

  def isSorted[A] (as: Array[A], ordered: (A,A) =>  Boolean) : Boolean = {
    @annotation.tailrec
    def loop(as: Array[A], n:Int, ordered: (A,A) =>  Boolean) : Boolean = {
      if (n == as.length-1) true
      else if (ordered(as(n), as(n+1)) == false) false
      else loop(as, n + 1, ordered)
    }

    loop(as, 0, ordered)
  }

  // Exercise 5

  def curry[A,B,C] (f: (A,B) => C): A => (B => C) = {
    (a: A) => (b: B) => f(a,b)
    //a => (b => f(a,b))
  }

  // Exercise 6

  def uncurry[A,B,C] (f: A => B => C): (A,B) => C = {
    (a: A, b: B) => f(a)(b)

    //f: A=>B=>C ~ f(a)(b)
  }

  // Exercise 7

  def compose[A,B,C] (f: B => C, g: A => B) : A => C = {
    (a: A) => f(g(a))
  }

  // Exercise 8 requires no programming
  // Match error at runtime
  // Match error at runtime
  // 3
  // 15
  // 101

  // Exercise 9

  def tail[A] (as: List[A]) :List[A] = as match {
    case Nil => sys.error("tail of empty list")
    case Cons(_, t) => t
  }

  // Exercise 10

  @annotation.tailrec
  def drop[A] (l: List[A], n: Int) : List[A] = {
    if(n==0) l
    else l match {
      case Nil => sys.error("empty list")
      case Cons(_, t) => drop(t, n-1)
    }
  }

  // Exercise 11

  @annotation.tailrec
  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
    case Nil => l
    case Cons(h, t) if f(h) => dropWhile(t, f)
    case _ => l
  }

  // Exercise 12

  def init[A](l: List[A]): List[A] = l match {
    //last element of the list is "anyting" and that points to Nil
    //hence the break condition is the list matches that and we "earase" the last element before the Nil by just simply giving back Nil
    case Nil => sys.error("empty list")
    case Cons(_, Nil) => Nil
    //we call init recursively to "create" the new list
    case Cons(h, t) => Cons(h, init(t))
  }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B) : B = as match {
    case Nil => z
    case Cons(h, t) => f(h, foldRight(t, z)(f)) 
  }

  // Exercise 13

  def length[A] (as: List[A]): Int = 
    foldRight(as, 0)((_, x) => x + 1)

  // Exercise 14

  //z is the initial value
  @annotation.tailrec
  def foldLeft[A,B] (as: List[A], z: B) (f: (B, A) => B): B = as match {
    case Nil => z
    case Cons(h, t) => foldLeft(t, f(z, h))(f)

    //1. List(2, 3, 4), f(z, 1) = acc = new z
    //2. List(3, 4), f(acc, 2)
    //3. List(4), f(acc, 3)
    //4. Nil, f(acc, 4)
  }

  // Exercise 15

  def product (as: List[Int]): Int = 
    foldLeft(as, 1)( _ * _ )

  def length1 (as: List[Int]): Int = 
    foldLeft(as, 0)((x, _) => x + 1)

  // Exercise 16

  def reverse[A] (as: List[A]): List[A] = 
    foldLeft(as, Nil: List[A])((z, h) => Cons(h, z)) //z is the accumulated value

  // Exercise 17

  def foldRight1[A,B] (as: List[A], z: B) (f: (A, B) => B): B = 
    foldLeft(reverse(as), z)((b, a) => f(a, b))

  // Exercise 18

  def foldLeft1[A,B] (as: List[A], z: B) (f: (B,A) => B): B = ???

  // Exercise 19

  def append[A](a1: List[A], a2: List[A]): List[A] = a1 match {
    case Nil => a2
    case Cons(h,t) => Cons(h, append(t, a2))
  }

  def concat[A] (as: List[List[A]]): List[A] = ???

  // Exercise 20

  def filter[A] (as: List[A]) (f: A => Boolean): List[A] = 
    foldRight(as, Nil: List[A])((h, t) => if(f(h)) Cons(h, t) else t )

  // Exercise 21

  def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] = ???

  // Exercise 22

  def filter1[A] (l: List[A]) (p: A => Boolean) :List[A] = ???

  // Exercise 23

  def add (l: List[Int]) (r: List[Int]): List[Int] = (l, r) match {
      case (_, Nil) => Nil
      case (Nil, _) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(h1 + h2, add(t1)(t2))
  }

  // Exercise 24

  def zipWith[A,B,C] (f: (A,B)=>C) (l: List[A], r: List[B]): List[C] = (l, r) match {
      case (_, Nil) => Nil
      case (Nil, _) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(f(h1, h2), zipWith(f)(t1, t2))
  }

  // Exercise 25

  def hasSubsequence[A] (sup: List[A], sub: List[A]): Boolean = ???

  // Exercise 26

  def pascal (n: Int): List[Int] = ???

}
