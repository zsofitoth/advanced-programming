package adpro

// Exercise  1

/* We create OrderedPoint as a trait instead of a class, so we can mix it into
 * Points (this allows to use java.awt.Point constructors without
 * reimplementing them). As constructors are not inherited, We would have to
 * reimplement them in the subclass, if classes not traits are used.  This is
 * not a problem if I mix in a trait construction time. */

trait OrderedPoint extends scala.math.Ordered[java.awt.Point] {

  this: java.awt.Point =>

  override def compare (that: java.awt.Point): Int = (that, this) match {
    case (p, q) => if (p.x < q.x || p.x == q.x && p.y < q.y)  0 else -1
  }
}
/*
trait OrderedPoint extends scala.math.Ordered[java.awt.Point] {
  this: java.awt.Point =>
  override def compare (that: java.awt.Point): Int = {
    if ((this.x < that.x) ||(this.x == that.x && this.y < that.y) ) -1
    else 0
  }
} */

// Try the following (and similar) tests in the repl (sbt console):
// val p = new java.awt.Point(0,1) with OrderedPoint
// val q = new java.awt.Point(0,2) with OrderedPoint
// assert(p < q)

// Chapter 3


sealed trait Tree[+A]
case class Leaf[A] (value: A) extends Tree[A]
case class Branch[A] (left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {

  def size[A] (t :Tree[A]): Int = t match {
    case Leaf(_) =>1
    case Branch(left,right) => 1 + size(left) + size(right) 
  }

  // Exercise 3 (3.26)

  def maximum (t: Tree[Int]): Int = t match {
    case Leaf(a) => println(a); a
    case Branch(left, right) => maximum(left) max maximum(right)  
  }

  // Exercise 4 (3.28)

  def map[A,B] (t: Tree[A]) (f: A => B): Tree[B] = t match {
    case Leaf(a) => println(f(a));Leaf(f(a))
    case Branch(l, r) => Branch(map(l)(f), map(r)(f))
  }

  // Exercise 5 (3.29)

  def fold[A,B] (t: Tree[A]) (f: (B,B) => B) (g: A => B): B = t match {
    case Leaf(a) => g(a)
    case Branch(l, r) => f(fold(l)(f)(g), fold(r)(f)(g))
  }

  def size1[A] (t: Tree[A]): Int = {
    //def f(l: Int, r: Int) = l + r
    //def g(l: Int, r: Int) = 1 + f(l, r)
    // 1 + _ + _ is the same as 1 + f(_, _) 
    fold[A, Int](t)(1 + _ + _)(a => 1)  
  }


  def maximum1[A] (t: Tree[Int]): Int = {
    //def f(l: Int, r: Int): Int = l max r
    fold[Int,Int] (t)(_ max _)(a => a)
  }

  def map1[A,B] (t: Tree[A]) (f: A => B): Tree[B] = 
    fold[A, Tree[B]](t)((l, r) => Branch(l, r))(a => Leaf(f(a)))

}


sealed trait Option[+A] {

  // Exercise 6 (4.1)

  def map[B] (f: A=>B): Option[B] = this match {
    case None => None
    case Some(a) => Some(f(a))
  }

  def getOrElse[B >: A] (default: => B): B = this match {
    case None => default
    case Some(a) => a
  }

  def flatMap[B] (f: A=>Option[B]): Option[B] = this match {
    case None => None
    case Some(a) => if (f(a) == None) None else f(a)
  }

  def filter (p: A => Boolean): Option[A] = this match {
    case None => None
    case Some(a) => if(p(a)) Some(a) else None
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

  def variance (xs: Seq[Double]): Option[Double] = {
    for {
      m <- mean(xs)
      v <- mean(xs.map(x => math.pow(x-m, 2)))
    } yield v
  }
  //OR: 
  //mean(xs).flatMap(m => mean(xs).map(x => math.pow(x-m, 2)))

  // Exercise 8 (4.3)

  def map2[A,B,C] (ao: Option[A], bo: Option[B]) (f: (A,B) => C): Option[C] = {
    for {
      b <- bo
      a <- ao
    } yield f(a, b)
    
    /*
      I don't know if this is correct or necessary, I solved it like this before I saw the correct solution in the book (yellow sidebar),
      but within the for yield each line is applying a flatMap anyways if I understood correctly
      
      for {
        b <- ao.flatMap(_ => bo)
        c <- ao.map(a => f(a, b))
      } yield c
    
    */
  }
  
  //Solution with flatMap and map
  /*
    ao.flatMap(a => 
      bo.map(b => f(a, b)))
  */
  
  //Solution with pattern matching
  /*(ao, bo) match {
      case (_, None) => None
      case (None, _) => None
      case (Some(a), Some(b)) => Some(f(a, b)) 
  }*/

  // Exercise 9 (4.4)

  def sequence[A] (aos: List[Option[A]]): Option[List[A]] = 
    aos.foldRight[Option[List[A]]](Some(Nil))((ho, to) => map2(ho,to)((ho,to) =>ho::to))
    //aos.foldRight[Option[List[A]]](Some(Nil))((ho,to) => ho.flatMap(h => to.map(t => h::t)))

  //no pattern matching is allowed, but could this work as a recursive solution?
  def sequence2[A] (aos: List[Option[A]]): Option[List[A]] = aos match {
    case Nil => Some(Nil)
    case ho::to => 
      if(ho == None) None
      else ho.flatMap(h => sequence2(to).map(h::_))
  }

  // Exercise 10 (4.5)

  def traverse[A,B] (as: List[A]) (f: A => Option[B]): Option[List[B]] = 
    as.foldRight[Option[List[B]]](Some(Nil))((h,to) => map2(f(h), to)((ho,to) => ho::to))
    //Traverses through the list twice
    //sequence(as.map(a => f(a)))

}

//TO SEE IT ON THE CONSOLE: sbt run

object Main extends App {
    val t: Tree[Int] = Branch( Branch(Leaf(1), Leaf(2)), Branch(Leaf(-1), Leaf(2)) )
    println(t)

    Tree.maximum(t)
    Tree.map(t)(a => a * 2)

    val t2 = Tree.fold(t)((a: Int, b: Int) => a + b)(x => if (x > 0)  x else 0)
    println("addition " + t2)

    val o: Option[String] = Some("h");
    println(o)

    val res: String = o.getOrElse("error");
    println(res)
}
