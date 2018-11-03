// Advanced Programming 2015
// Andrzej Wasowski, IT University of Copenhagen
package fpinscala.monoids
import scala.language.higherKinds

trait Monoid[A] {

  def op(a1: A, a2: A): A
  def zero: A

}

object Monoid {

  val stringMonoid = new Monoid[String] {
    def op(a1: String, a2: String) = a1 + a2
    val zero = ""
  }

  // I guess such objects could be useful if we combine them with implicits

  def listMonoid[A] = new Monoid[List[A]] {
    def op (a1: List[A], a2: List[A]) = a1 ++ a2
    val zero = Nil
  }

  // Exercise 1

  val intAddition = new Monoid[Int] {
    def op (a1: Int, a2: Int) = a1 + a2
    val zero = 0
  }
  
  val intMultiplication = new Monoid[Int] {
    def op (a1: Int, a2: Int) = a1 * a2
    val zero = 1
  }
  
  val booleanOr = new Monoid[Boolean] {
    def op (a1: Boolean, a2: Boolean) = a1 || a2
    val zero = false
  }
  
  val booleanAnd = new Monoid[Boolean] {
    def op (a1: Boolean, a2: Boolean) = a1 && a2
    val zero = true
  }

  // Exercise 2

  def optionMonoid[A] : Monoid[Option[A]] = new Monoid[Option[A]] {
    def op (a1: Option[A], a2: Option[A]) = a1.orElse(a2)
    val zero = None
  }

  def dual[A] (m :Monoid[A]) = new Monoid[A] {
    def op (a1: A, a2: A) = m.op(a2,a1)
    val zero = m.zero
  }

  // Exercise 3
  def endoMonoid[A] : Monoid[A => A] = new Monoid[A => A] {
    def op(f: A => A, g: A => A) = (a: A) => f(g(a))
    val zero = (a: A) => a
  }

  // Exercise 4 is solved in MonoidSpec.scala

  def concatenate[A] (as: List[A], m: Monoid[A]): A =
    as.foldLeft (m.zero) (m.op)

  // Exercise 7
  //
  // Implement a productMonoid that builds a monoid out of two monoids. Test it
  // with scala check for instance by composing an Option[Int] monoid with a
  // List[String] monoid and running through our monoid laws.

  // def productMonoid[A,B] (ma: Monoid[A]) (mb: Monoid[B]) =

}


trait Foldable[F[_]] {

  def foldRight[A,B] (as: F[A]) (z: B) (f: (A,B) => B): B
  def foldLeft[A,B] (as: F[A]) (z: B) (f: (B,A) => B): B
  def foldMap[A,B] (as: F[A]) (f: A => B) (mb: Monoid[B]): B
  def concatenate[A](as: F[A])(m: Monoid[A]): A =
    foldLeft(as)(m.zero)(m.op)

  // Exercise 9 (CB 10.15)

  // def toList[A] (fa: F[A]) :List[A]
}

// Exercise 8 (CB 10.12 We just do Foldable[List])

object Foldable extends Foldable[List] {

  def foldRight[A,B] (as: List[A]) (b: B) (f: (A,B) => B): B = ???

  def foldLeft[A,B] (as: List[A]) (b: B) (f: (B,A) => B): B = ???

  def foldMap[A,B] (as: List[A]) (f: A => B) (mb: Monoid[B]): B = ???
}

// vim:cc=80:tw=80
