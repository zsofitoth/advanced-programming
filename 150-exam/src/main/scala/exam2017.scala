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

  //Generalize these types to use any generic collection C[_] instead of List[_]
  trait TreeOfCollections[C[+_], +A]
  case class LeafOfCollections[C[+_]] () extends TreeOfCollections[C, Nothing]
  case class BranchOfCollections[C[+_], +A](
    data: C[A],
    left: TreeOfCollections[C, A],
    right:TreeOfCollections[C, A]
  ) extends TreeOfCollections[C, A]

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

  def step (i: Input) (s: MachineState) : MachineState = (i, s) match {
    // Ignore all the input if it contains no coffee beans (no state change)
    case (_, MachineState(_, 0, _)) => s
    // Inserting a coin into a machine causes it to become busy. It also increases the number of coins accumulated
    case(Coin, MachineState(r, cb, c)) if(r && cb > 0) => MachineState(false, cb, c + 1)
    // Pressing Brew on a busy machine will cause it to deliver a cup of coffee (which changes the state by taking one coffee portion away) and return to the ready state
    case(Brew, MachineState(r, cb, c)) if(!r && cb > 0) => MachineState(true, cb - 1, c ) 
    // Pressing Brew on a machine which is not busy (a ready machine) has no effect
    case(Brew, MachineState(r, _, _)) if(r) => s
    // Inserting two or more coins in a row is possible, but you will only get one coffee anyway. The machine
    // is a bit simplistic: the new coin is just gladly consumed
    //case(i: Coin, MachineState(r, cb, c)) if(i > 1 && r && cb > 0 ) => MachineState(false, cb, c + 1) <- does not compile
    case _ => s
  }

  //should execute the machine based on the list of inputs and return the number of coffee bean portions and coins accumulated at the end.
  def simulateMachine (initial: MachineState) (inputs: List[Input]): (Int,Int) =  inputs match {
    case Nil => (initial.coffee, initial.coins)
    case h::t => simulateMachine(step(h)(initial))(t)
  }

  //STATE[MachineState, (Int, Int)]
  //OFFICIAL SOLUTION
  def simulateMachine2(initial: MachineState) (inputs: List[Input]): (Int,Int) = {
    val ms: List[State[MachineState,Unit]] = inputs map (i => State.modify (step (i)))
    val m: State[MachineState,Unit] = State sequence (ms) map (_ => Unit)
    val MachineState(ready,coffee,coins) = (m run initial)._2
    (coffee,coins)
  }

} // Q4


object Q5 {

  // Implement a function flatten that converts a Stream[List[A]] to a Stream[A] in the obvious way (as if the
  // lists were `concatenated into a single stream`). Do so carefully, to avoid forcing the next list in the stream if
  // not necessary. It is fine to force the head of the stream always (as often done earlier in the course), but do not
  // force deeper elements
  
  def flatten[A] (s: => Stream[List[A]]) :Stream[A] = 
    s.flatMap(as => as.foldRight(Empty: Stream[A])((h,t) => cons(h,t)))

  // TRICK IS:
  // convert List to stream

  def printTest: Unit = {
    
    val sas: Stream[List[Int]] = Stream(List(1,2), List(2,3), List(1,5), List(2,3), List(3, 5), List(4, 2))
    //Convert list to stream! List(1,2).toStream
    val r: Stream[Int] = sas.flatMap(as => Stream(as: _*))
    val r2: Stream[Int] = sas.flatMap(as => as.foldRight(Empty: Stream[Int])((h,t) => cons(h,t)))

    println(r2.toList)

  }

} // Q5


object Q6 {

  def parExists[A] (as: List[A]) (p: A => Boolean): Par[Boolean] = {
    val pas: Par[List[A]] = parFilter(as)(a => p(a))
    val isTrue: Par[Boolean] = map(pas)(l => l.length > 0)
    isTrue
  }

} // Q6


object Q7 {

  //  def reduceL[A,Z] (opl: (Z,A) => Z) (z: Z, t: FingerTree[A]) :Z = ??? // assume that this is implemented
  //  def reduceR[A,Z] (opr: (A,Z) => Z) (t: FingerTree[A], z: Z) :Z = ??? // assume that this is implemented

  //  trait FingerTree[+A] {
  //  def addL[B >:A] (b: B) :FingerTree[B] = ??? // assume that this is implemented as in the paper
  //  def addR[B >:A] (b: B) :FingerTree[B] = ??? // assume that this is implemented as in the paper
  // }

  // Implement this:
  def concatenate[A, B >: A] (left: FingerTree[A]) (right: FingerTree[B]) :FingerTree[B] = {
    Digit.toTree(left.toList ::: right.toList)
  }
    

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
  Q5.printTest
}

