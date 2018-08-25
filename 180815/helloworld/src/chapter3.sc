sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {
  def sum(ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }

  def product(ds: List[Double]) : Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
}

//List(1,2,3) match {case _ => 42}
//List(1,2,3) match {case Cons(h, _) => h}
//List(1,2,3) match {case Cons(_, t) => t}
//List(1,2,3) match {case Nil => 42}

//3.1
val x = List(1,2,3,4,5) match {
    //case Cons(x, Cons(2, Cons(4, _))) => x
    //case Cons(1, Cons(2, x)) => x
    //case Nil => 42
    //case _ => 101
    //case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + List.sum(t)
}



