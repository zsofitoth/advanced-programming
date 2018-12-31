// Your name and ITU email: ____
package adpro.exam2017

import scala.language.higherKinds
import fpinscala.monoids.Monoid
import fpinscala.monads.Functor
import fpinscala.state.State
import fpinscala.laziness.{Stream,Empty,Cons}
import fpinscala.laziness.Stream._
import fpinscala.parallelism.Par._
import adpro.data._
import adpro.data.FingerTree._
import monocle.Lens

object Q1 { 

  //Given an association list l and a key k, 
  //it should return true iff there exist an element with that key on the list
  def hasKey[K,V] (l: List[(K,V)]) (k: K) :Boolean = l match {
    case Nil => false
    case h::t => if(h._1 == k) true else hasKey(t)(k)
  }

  //combining values with the same key component using the reduction operator ope
  def reduceByKey[K,V] (l :List[(K,V)]) (ope: (V,V) => V) :List[(K,V)] = {
    l.foldRight(Nil: List[(K, V)])((h, t) => {
      if(!hasKey(t)(h._1)){
        h::t 
      } else {
        t.map(el => {
          if(el._1 == h._1) (el._1, ope(el._2, h._2))
          else el
        })
      }
    })
  }

  def separate (l :List[(Int,List[String])]) :List[(Int,String)] =
    l flatMap { idws => idws._2 map { w => (idws._1,w) } }

  def separateViaFor (l :List[(Int,List[String])]) :List[(Int,String)] = 
    for {
      idws <- l
      w <- idws._2
    } yield((idws._1, w))

  def printTest: Unit = {
      val l: List[(Int, Int)] = List((1,2), (2,3), (1,5), (2,3), (3, 5), (4, 2))
      println(hasKey(l)(5))
      println(hasKey(l)(1))
      println(reduceByKey(l)(_+_))

    val l2: List[(Int, List[String])] = List(
      (1,List("Hello", "How", "are", "you")), 
      (2,List("w1", "w3")), 
      (1,List("a1", "b3")), 
      (2,List("t3")), 
      (3,List("y1", "y3", "grsa")), 
      (4,List("d1", "d3"))
    )

    println(separate(l2))
    println(separateViaFor(l2))

  }

} // Q1


object Q2 {

  trait TreeOfLists[+A]
  case object LeafOfLists  extends TreeOfLists[Nothing]
  case class BranchOfLists[+A] (
    data: List[A],
    left: TreeOfLists[A],
    right: TreeOfLists[A]
  ) extends TreeOfLists[A]

  // trait TreeOfCollections[...]
  // case class LeafOfCollections ...
  // case class BranchOfCollections ...

  def map[A,B] (t: TreeOfLists[A]) (f: A => B) :TreeOfLists[B] = t match {
    case LeafOfLists => LeafOfLists
    case BranchOfLists (data,left,right) =>
        BranchOfLists (data map f, map (left) (f), map (right) (f))
  }

  // def map[...] (t: TreeOfCollections[...]) (f: A => B) ...

} // Q2

object Q3 {

  def p (n: Int): Int = { println (n.toString); n }

  def f (a: Int, b: Int): Int = if (a > 10) a else b

  // Answer the questions in comments here

  // A. ...

  // B. ...

  // C. ...

} // Q3


object Q4 {

  sealed trait Input
  case object Coin extends Input
  case object Brew extends Input

  case class MachineState (ready: Boolean, coffee: Int, coins: Int)

  def step (i: Input) (s: MachineState) :MachineState =  ???

  def simulateMachine (initial: MachineState) (inputs: List[Input]) :(Int,Int) =  ???

} // Q4


object Q5 {

  def flatten[A] (s: =>Stream[List[A]]) :Stream[A] = ???

} // Q5


object Q6 {

  def parExists[A] (as: List[A]) (p: A => Boolean): Par[Boolean] = ???

} // Q6


object Q7 {

  //  def reduceL[A,Z] (opl: (Z,A) => Z) (z: Z, t: FingerTree[A]) :Z = ??? // assume that this is implemented
  //  def reduceR[A,Z] (opr: (A,Z) => Z) (t: FingerTree[A], z: Z) :Z = ??? // assume that this is implemented

  //  trait FingerTree[+A] {
  //  def addL[B >:A] (b: B) :FingerTree[B] = ??? // assume that this is implemented as in the paper
  //  def addR[B >:A] (b: B) :FingerTree[B] = ??? // assume that this is implemented as in the paper
  // }

  // Implement this:

  def concatenate[A, B >: A] (left: FingerTree[A]) (right: FingerTree[B]) :FingerTree[B] = ???

} // Q7


object Q8 {

  // def nullOption[T] = Lens[...]

  // Answer the questions below:

  // A. ...

  // B. ...

  // C. ...

} // Q8

object Main2017 extends App { 
  Q1.printTest
  //Q2.printTest
  //Q3.printTest
  //Q5.printTest
}

