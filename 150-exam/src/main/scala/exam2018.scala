// Name: Zsófia Tóth
// ITU email: zsto@itu.dk
package adpro.exam2018

import fpinscala.monoids.Monoid
import fpinscala.monads.Monad
import fpinscala.monads.Functor
import fpinscala.laziness.{Stream,Empty,Cons}
import fpinscala.laziness.Stream._
import fpinscala.parallelism.Par._
import scala.language.higherKinds
import adpro.data._
import adpro.data.FingerTree._
import monocle.Lens

object Q1 { 
  
  //solution from teachers
  private def f[K,V] (tail: List[(K,List[V])], head: (K,V)): List[(K,List[V])] = {
    val l1 = tail map { el => if (el._1 == head._1) el._1->(head._2::el._2) else el }
    if (l1 == tail) (head._1->List(head._2))::tail else l1
  }

  def groupByKey2[K,V] (l :List[(K,V)]) :List[(K,List[V])] =
     l.foldRight[List[(K,List[V])]] (Nil) ((h, t) => f(t,h))

  def printTest: Unit = {
      val l: List[(Int, Int)] = List( (1,1), (1,1), (1,2), (2,3), (3, 4), (3, 5) )
      //Zsofia's solution corrected with the teacher's solution
      val result =  
      l.foldRight(Nil: List[(Int, List[Int])]) ((head, tail) => {
        val l1 = tail.map( el => {
          if(el._1 == head._1) (head._1, head._2::el._2)
          else el
        })
        //this step is essential
        if (l1 == tail) (head._1, head._2::Nil)::tail else l1
      })

      //println(result)
      //println(groupByKey2(l))
    
  }

  // Zsofia's solution
  // (K, V) -> key, value tuple
  // groupByKey (List(1->1, 1->1, 1->2, 2->3)) should produce List(1->List(1,1,2), 2->List(3))
  def groupByKey[K,V] (l: List[(K,V)]): List[(K,List[V])] = {
    l.foldRight(Nil: List[(K, List[V])]) ((head, tail) =>
        if(tail.length == 0) { 
          List(head._1 -> (head._2::Nil))
        }
        else {
          val newTail: List[(K, List[V])] = tail.map( el => {
            if(el._1 == head._1) (head._1, head._2::el._2)
            else (head._1, head._2::Nil)//::tail DOES NOT COMPILE
          })

          newTail
        }
      )
  }
}


object Q2 { 

  def printTest: Unit = {
    val l: List[Either[String,Int]] = List(Left("FileNotFound"), Right(12), Left("IndexOutOfBound"), Right(44))
    println(f(l))
    
  }
  
  //ZSÒFIA´s solution (I hope I understood the question correctly)
  def f[A,B] (results: List[Either[A,B]]): Either[List[A],List[B]] = {
    //val (lefts, rights) = results.partition(_.isLeft)
    val lefts = results.filter(l => l.isLeft)
    val rights = results.filter(r => r.isRight)
    if(lefts.length != 0) Left(lefts.map(_.left.get))
    else Right(rights.map(_.right.get))
  }

}


object Q3 {

  type T[B] = Either[String,B]
  implicit val eitherStringIsMonad :Monad[T] = ???



  implicit def eitherIsMonad[A] = {
    type T[B] = Either[A,B]
    ??? 
  }

} // Q3


object Q4 {

   // Write the answers in English below.
   
   // A. ...
   
   // B. ...
   
}


object Q5 { 

  def parForall[A] (as: List[A]) (p: A => Boolean): Par[Boolean] = ???

}


object Q6 {

  def apply[F[_],A,B](fab: F[A => B])(fa: F[A]): F[B] = ???
  def unit[F[_],A](a: => A): F[A] = ???

  val f: (Int,Int) => Int = _ + _
  def a :List[Int] = ???

  // Answer below in a comment:

  // ...

} // Q6


object Q7 {

  def map2[A,B,C] (a :List[A], b: List[B]) (f: (A,B) => C): List[C] = ???


  def map3[A,B,C,D] (a :List[A], b: List[B], c: List[C]) (f: (A,B,C) => D) :List[D] = ???


  // def map3monad ...

} // Q7


object Q8 {

  def filter[A] (t: FingerTree[A]) (p: A => Boolean): FingerTree[A] = ???

}


object Q9 {

  def eitherOption[A,B] (default: => A): Lens[Either[A,B],Option[B]] = ???


  // Answer the questions below:

  // A. ...

  // B. ...

  // C. ...

} // Q9

object Main extends App { 
  Q1.printTest
  Q2.printTest
}

