// Advanced Programming, A. Wąsowski, IT University of Copenhagen
//
// Group number: 28
//
// AUTHOR1: Sadaf Zahid <saza@itu.dk>
// TIME1: _____ <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//
// AUTHOR2: Niels Viberg Sønderbæk <nivs@itu.dk>
// TIME2: 10 hours <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//
// AUTHOR3: Zsófia Tóth <zsto@itu.dk>
// TIME1: 10 hours <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//
// You should work with the file by following the associated exercise sheet
// (available in PDF from the course website).
//
// This file is compiled with 'sbt compile' and tested with 'sbt test'.
//
// The file shall always compile and run after you are done with each exercise
// (if you do them in order).  Please compile and test frequently. Of course,
// some tests will be failing until you finish. Only hand in a solution that
// compiles and where tests pass for all parts that you finished.    The tests
// will fail for unfnished parts.  Comment such out.

package adpro

// Exercise  1

/* We create OrderedPoint as a trait instead of a class, so we can mix it into
 * Points (this allows to use java.awt.Point constructors without
 * reimplementing them). As constructors are not inherited, We would have to
 * reimplement them in the subclass, if classes not traits are used.  This is
 * not a problem if I mix in a trait construction time. */

trait OrderedPoint extends scala.math.Ordered[java.awt.Point] {

  this: java.awt.Point =>

  override def compare (that: java.awt.Point): Int =  {
    if(this.x < that.x || ((this.x==that.x) && this.y < that.y)) -1
    else 1
    }
  }

// Try the following (and similar) tests in the repl (sbt console):
// val p = new java.awt.Point(0,1) with OrderedPoint
// val q = new java.awt.Point(0,2) with OrderedPoint
// assert(p < q)

// Chapter 3


sealed trait Tree[+A]
case class Leaf[A] (value: A) extends Tree[A]
case class Branch[A] (left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {

  def size[A] (t :Tree[A]): Int = {
    t match {
      case Leaf(_) => 1
      case Branch(l,r) => 1 + size(l) + size(r)
    }
  }

  // Exercise 3 (3.26)

  def maximum (t: Tree[Int]): Int = {
    t match{
      case Leaf(a) => a
      case Branch(l,r) => maximum(l) max maximum(r)
    }
  }

  // Exercise 4 (3.28)

  def map[A,B] (t: Tree[A]) (f: A => B): Tree[B] = {
    t match {
      case Leaf(a) => Leaf(f(a))
      case Branch(l,r) => Branch(map(l)(f), map(r)(f))
    }
  }

  // Exercise 5 (3.29)

  def fold[A,B] (t: Tree[A]) (f: (B,B) => B) (g: A => B): B = {
    t match {
      case Leaf(a) => g(a)
      case Branch(l,r) => f(fold(l)(f)(g), fold(r)(f)(g))
    }
  }

  def size1[A] (t: Tree[A]): Int = {
     fold[A,Int](t)(1 + _ + _)(a => 1)
  }

  def maximum1[A] (t: Tree[Int]): Int = {
    fold[Int,Int](t)(_ max _)(a => a)
  }

  def map1[A,B] (t: Tree[A]) (f: A=>B): Tree[B] = {
    fold[A,Tree[B]](t)(Branch(_,_))(a => Leaf(f(a)))
  }

}

sealed trait Option[+A] {

  // Exercise 6 (4.1)

  def map[B] (f: A=>B): Option[B] = {
    this match {
      case Some(a) => Some(f(a))
      case _ => None
    }
  }

  // You may Ignore the arrow in default's type below for the time being.
  // (it should work (almost) as if it was not there)
  // It prevents the argument "default" from being evaluated until it is needed.
  // So it is not evaluated in case of Some (the term is 'call-by-name' and we
  // should talk about this soon).

  def getOrElse[B >: A] (default: => B): B = {
    this match {
      case Some(a) => a
      case _ => default
    }
  }

  def flatMap[B] (f: A=>Option[B]): Option[B] = {
    this match {
      case Some(a) => f(a)
      case _ => None
    }
  }

  def filter (p: A => Boolean): Option[A] = {
    this match {
      case Some(a) => if(p(a)) Some(a) else None
      case _ => None
    }
  }
}

case class Some[+A] (get: A) extends Option[A]
case object None extends Option[Nothing]

object ExercisesOption {

  // Remember that mean is implemented in Chapter 4 of the text book

  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)

  // Exercise 7 (4.2)


  // 1. Calculate the mean
  // 2. Calculate the mean squared diffence (math.pow(x - m, 2))
  def variance (xs: Seq[Double]): Option[Double] = {
    for {
      m <- mean(xs)
      res <- mean(xs.map(x => math.pow((x - m),2)))
    } yield res
    // mean(xs) flatMap(x => mean(xs.map(x => math.pow((x - m),2))))
  }

  // Exercise 8 (4.3)

  //ExercisesOption.map2 (Some(n), Some(m)) (_-_) should equal (Some(n-m))
  def map2[A,B,C] (ao: Option[A], bo: Option[B]) (f: (A,B) => C): Option[C] = {
    for{
      a <- ao
      res <- bo.map(b => f(a,b))
    } yield res
  }

  // Exercise 9 (4.4)

  // ExercisesOption.sequence (Nil) should equal (Some(Nil))
  // ExercisesOption.sequence (List(None)) should equal (None)
  // ExercisesOption.sequence (List(Some(42))) should equal (Some(List(42)))
  // ExercisesOption.sequence (List(Some(1), Some(2), Some(42))) should equal (Some(List(1,2,42)))
  // ExercisesOption.sequence (List(None,    Some(2), Some(42))) should equal (None)
  // ExercisesOption.sequence (List(Some(1), None,    Some(42))) should equal (None)
  // ExercisesOption.sequence (List(Some(1), Some(2), None    )) should equal (None)

  //Using map2 to create our list by appending elements from the list
  // should it hit a value of None, map2 will return None as required
  def sequence[A] (aos: List[Option[A]]): Option[List[A]] = {
    aos.foldRight[Option[List[A]]](Some(Nil))((a,b) => map2(a,b)(_ :: _))
  }

  // Exercise 10 (4.5)

  def traverse[A,B] (as: List[A]) (f :A => Option[B]): Option[List[B]] = {
    as.foldRight[Option[List[B]]](Some(Nil))((h,to) => map2(f(h), to)(_::_))
  }

}
