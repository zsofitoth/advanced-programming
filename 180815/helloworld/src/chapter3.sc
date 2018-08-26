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

  def tail[A](as: List[A]): List[A] = as match {
    case Nil => as
    case Cons(_, xs) => xs
  }

  def setHead[A](a: A, as: List[A]): List[A] = as match {
    case Nil => as
    case Cons(_, xs) => Cons(a, xs)
  }

  def drop[A](l: List[A], n: Int): List[A] = {
    if(n<=0) l
    else l match {
      case Nil => l
      case Cons(_, xs) => drop(xs, n-1)
    }
  }

  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
    case Cons(h,t) if f(h) => dropWhile(t, f)
    case _ => l
  }

  //copies values until the first list is exhausted
  //runtime and memory is only determined by the length of a1
  def append[A](a1: List[A], a2: List[A]) : List[A] = {
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }
  }

  def init[A](l: List[A]): List[A] = l match {
    case Nil => l
    case Cons(_, Nil) => Nil
    case Cons(x, xs) => Cons(x, init(xs))
  }

  // optimized for tail recursion using a mutable list buffer
  def init2[A](l: List[A]): List[A] = {
    import collection.mutable.ListBuffer
    val buf = new ListBuffer[A]
    @annotation.tailrec
    def go(cur: List[A]): List[A] = cur match {
      case Nil => sys.error("init of empty list")
      case Cons(_,Nil) => List(buf.toList: _*)
      case Cons(h,t) => buf += h; go(t)
    }
    go(l)
  }

  //tail recursive
  @annotation.tailrec
  def foldLeft[A,B](as: List[A], z: B)(f: (A, B) => B) : B = as match {
    case Nil => z
    case Cons(x,xs) => foldLeft(xs, f(x, z))(f)
  }

  def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B) : B = as match{
    case Nil => z
    case Cons(x, xs) => f(x, foldRight(xs, z)(f))
  }

  def sum2(ns: List[Int]): Int =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]): Double =
    foldRight(ns, 1.0)(_*_) //same as ((x,y) => x * y)

  def length[A](l: List[A]): Int =
    foldRight(l, 0)((_, acc) => acc + 1)

  def sum2TailRec(ns: List[Int]): Int =
    foldLeft(ns, 0)((x,y) => x + y)

  def product2TailRec(ns: List[Double]): Double =
    foldLeft(ns, 1.0)(_*_) //same as ((x,y) => x * y)

  def lengthTailRec[A](l: List[A]): Int =
    foldLeft(l, 0)((_, acc) => acc + 1)

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

List.tail(List(1,2,3,4,5))
List.tail(List())

List.setHead(44, List(1,2,3,4,5))
List.setHead(44, List())

List.drop(List(1,2,3,4,5), 4)
List.drop(List(), 4)

def dropWhileCondition (a: Int): Boolean = a == 4
List.dropWhile(List(4,4,3,4,5), dropWhileCondition)

List.dropWhile(List(4,4,3,4,5), (x: Int) => x <= 4)
List.init(List(4,4,3,4,5))

List.foldRight(List(1,2,3), Nil: List[Int])(Cons(_,_))

List.length(List(1,2,3,4,7))






