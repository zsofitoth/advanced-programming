// Advanced Programming
// Andrzej Wąsowski, IT University of Copenhagen
//
// meant to be compiled, for example: fsc Stream.scala

package adpro

sealed trait Stream[+A] {
  import Stream._

  def headOption () :Option[A] = this match {
      case Empty => None
      case Cons(h,t) => Some(h())
  }

  def tail :Stream[A] = this match {
      case Empty => Empty
      case Cons(h,t) => t()
  }

  def foldRight[B] (z : =>B) (f :(A, =>B) => B) :B = this match {
      case Empty => z
      case Cons (h,t) => f (h(), t().foldRight (z) (f))
      // Note 1. f can return without forcing the tail
      // Note 2. this is not tail recursive (stack-safe) It uses a lot of stack
      // if f requires to go deeply into the stream. So folds sometimes may be
      // less useful than in the strict case
  }

  // Note 1. eager; cannot be used to work with infinite streams. So foldRight
  // is more useful with streams (somewhat opposite to strict lists)
  def foldLeft[B] (z : =>B) (f :(A, =>B) =>B) :B = this match {
      case Empty => z
      case Cons (h,t) => t().foldLeft (f (h(),z)) (f)
      // Note 2. even if f does not force z, foldLeft will continue to recurse
  }

  def exists (p : A => Boolean) :Boolean = this match {
      case Empty => false
      case Cons (h,t) => p(h()) || t().exists (p)
      // Note 1. lazy; tail is never forced if satisfying element found this is
      // because || is non-strict
      // Note 2. this is also tail recursive (because of the special semantics
      // of ||)
  }

  //Exercise 2
  def toList: List[A] = this match {
    case Empty => Nil
    case Cons(h, t) => h()::t().toList
  }

  def toList2: List[A] = {
    @annotation.tailrec
    def loop(s: Stream[A], acc: List[A]): List[A] = s match {
      case Empty => acc
      case Cons(h, t) => loop(t(), h()::acc)
    }

    loop(this, Nil: List[A]).reverse
  }

  //Exercise 3
  def take(n: Int): Stream[A] = this match {
    case Empty => Empty
    case Cons(h, t) => if (n > 0) cons(h(), t().take(n-1)) else Empty
  }

  def drop(n: Int): Stream[A] = this match {
    case Empty => Empty
    case Cons(h, t) => if(n > 0) t().drop(n-1) else this
  }

  //naturals.take(1000000000).drop(41).take(10).toList
  /*
    Will not terminate with memory exception, because it is evaluated lazily.
  */
  
  //Exercise 4
  def takeWhile(p: A => Boolean): Stream[A] = this match {
    case Empty => Empty
    case Cons(h, t) => if(p(h())) cons(h(), t().takeWhile(p)) else Empty
  }

  //naturals.takeWhile.(_<1000000000).drop(100).take(50).toList
  /*
    It terminates fast, because lazy evaluation applies. It will not filter all 1000000000
    numbers, because the rest of the "chain" only needs to work on 50 elements before evaluation. 
  */

  //Exercise 5
  def forAll(p: A => Boolean): Boolean = 
    foldRight(true)((a, b) => p(a) && b)
  /*this match {
    case Empty => true
    case Cons(h, t) => if(p(h())) true else false
  }*/

  //This should succeed: naturals.forAll (_ < 0)
  /*
    It will succeed because it will return false on the first element and will not evaluate the rest
  */
  //This should crash: naturals.forAll (_ >=0) . Explain why.
  /*
    This will crash because it is true for all elements and will evaluate "infinitely"
  */

  //both exists and forAll are fine to use for finite streams, because ...
  //Exercise 6
  def takeWhile2(p: A => Boolean): Stream[A] = 
    foldRight(Empty: Stream[A])((h,t) => if(p(h)) cons(h, t) else Empty )

  //Exercise 7
  def headOption2 () :Option[A] = 
    foldRight(None: Option[A])((h,t) => Some(h))

  //Exercise 8 The types of these functions are omitted as they are a part of the exercises
  def map[B](f: A => B): Stream[B] = 
    foldRight(Empty: Stream[B])((h,t) => cons(f(h), t))

  def filter(p: A => Boolean): Stream[A] = 
    foldRight(Empty: Stream[A])((h,t) => if(p(h)) cons(h,t) else t)

  def append = ??? 

  def flatMap[B](f: A => Stream[B]): Stream[B] = 
    foldRight(Empty: Stream[B])((h,t) => Stream.append(f(h))(t))

  //Exercise 09
  def find (p :A => Boolean) :Option[A]= this.filter(p).headOption
  //Put your answer here:

  //Exercise 10
  //Put your answer here:
  def fib: Stream[Int] = {
    def loop(prev: Int, cur: Int): Stream[Int] = {
      cons(prev, loop(cur, cur + prev))
    }
    loop(0,1)
  }

  //Exercise 11
  def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] = 
    f(z).map(x => cons(x._1, unfold(x._2)(f))).getOrElse(Empty)
  /*f(z) match {
    case Some((h,t)) => cons(h, unfold(t)(f))
    case None => Empty
  }*/

  //PRETTIFIED
  def unfold2[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] = 
    f(z).map(x => cons(x._1, unfold(x._2)(f))).getOrElse(Empty)

  //Exercise 12
  //def fib2: Stream[Int] = unfold((0, 1))(x => Some((x._1), (x._2, x._1 + x._2) ))
  //PRETTIFIED
  def fib2: Stream[Int] = unfold((0,1)){
    case (prev, cur) => Some(prev, (cur, prev+cur))
  }

  //def from2(n: Int): Stream[Int] = unfold(n)(n => Some(n, n + 1))
  def from2(n: Int): Stream[Int] = unfold(0){
    case n => Some(n, n+1)
  }

  //Exercise 13
  def map2[B](f: A => B): Stream[B] = unfold(this){
    case Cons(h, t) => Some((f(h()), t()))
    case Empty => None
  }

  def take2(n: Int): Stream[A] = unfold((this, n))(x => x._1 match {
    case Empty => None
    case Cons(h, t) => if (x._2 > 0) Some((h(), (t(), x._2 - 1))) else None
  })

  def takeWhileViaUnfold(p: A => Boolean) = unfold(this) {
    case Empty => None
    case Cons(h, t) => if (p(h())) Some((h(), t())) else None
  }

  def zipWith[B,C](s2: Stream[B])(f: (A, B) => C): Stream[C] = unfold((this, s2)) {
    case (Empty, _) => None
    case (_, Empty) => None
    case (Cons(h1, t1), Cons(h2, t2)) => Some((f(h1(), h2()), (t1(), t2())))
  }
}


case object Empty extends Stream[Nothing]
case class Cons[+A](h: ()=>A, t: ()=>Stream[A]) extends Stream[A]

object Stream {

  def empty[A]: Stream[A] = Empty

  def cons[A] (hd: => A, tl: => Stream[A]) :Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    Cons(() => head, () => tail)
  }

  def apply[A] (as: A*) :Stream[A] =
    if (as.isEmpty) empty
    else cons(as.head, apply(as.tail: _*))
    // Note 1: ":_*" tells Scala to treat a list as multiple params
    // Note 2: pattern matching with :: does not seem to work with Seq, so we
    //         use a generic function API of Seq


  //TODO: move this to the trait
  def append[A](s1: Stream[A])(s2: Stream[A]): Stream[A] =  
    s1.foldRight(s2)((h,t) => cons(h, t))
  
  //Exercise 1
  //going towards positive infinity
  def from(n:Int): Stream[Int] = cons(n,from(n+1))
  //going negative infinity
  def to(n:Int): Stream[Int] = cons(n,to(n-1))

  val naturals: Stream[Int] = from(0)

}

