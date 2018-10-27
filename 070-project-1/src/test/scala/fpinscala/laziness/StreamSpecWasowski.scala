// Advanced Programming
// Andrzej Wasowski, IT University of Copenhagen

package fpinscala.laziness
import scala.language.higherKinds

import org.scalatest.FlatSpec
import org.scalatest.prop.Checkers
import org.scalacheck._
import org.scalacheck.Prop._
import Arbitrary.arbitrary

// If you comment out all the import lines below, then you test the Scala
// Standard Library implementation of Streams. Interestingly, the standard
// library streams are stricter than those from the book, so some laziness tests
// fail on them :)

import stream00._    // uncomment to test the book solution
// import stream01._ // uncomment to test the broken headOption implementation
// import stream02._ // uncomment to test another version that breaks headOption

class StreamSpecWasowski extends FlatSpec with Checkers {

  import Stream._

  behavior of "headOption"

  // a scenario test:

  it should "return None on an empty Stream (01)" in {
    assert(empty.headOption == None)
  }

  // An example generator of random finite non-empty streams
  def list2stream[A] (la :List[A]): Stream[A] = la.foldRight (empty[A]) (cons[A](_,_))

  // In ScalaTest we use the check method to switch to ScalaCheck's internal DSL
  def genNonEmptyStream[A] (implicit arbA :Arbitrary[A]) :Gen[Stream[A]] =
    for { la <- arbitrary[List[A]] suchThat (_.nonEmpty)}
    yield list2stream (la)

  //MOCK METHODS; to test with these not with the ones being tested 

  def mockMap[A, B](s: Stream[A])(f: A => B): Stream[B] = s match {
    case Cons(h, t) => Stream.cons(f(h()), mockMap(t())(f))
    case _ => Stream.empty
  }

  // a property test:

  it should "return the head of the stream packaged in Some (02)" in check {
    // the implict makes the generator available in the context
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])

    ("singleton" |:
      Prop.forAll { (n :Int) => cons (n,empty).headOption == Some (n) } ) &&
    ("random" |:
      Prop.forAll { (s :Stream[Int]) => s.headOption != None } )

  }

  it should "not force the tail of the stream" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])
    
    // n/0 -> should throw arithmetic exception if it is evaluated ("forced")
    
    ("random" |:
      Prop.forAll { (s: Stream[Int]) => Stream.cons(1, mockMap(s)(n => n/0)).headOption == Some(1) })
  }

  behavior of "take(n)"
  
  it should "not force any heads nor any tails of the Stream it manipulates" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])
    
    // n/0 -> should throw arithmetic exception if it is evaluated ("forced")
    // map all elements of the stream, take z elements (can be any number) and check if the result is an Stream[Int] type
    // if take "forces" the evaluation an arithmetic exception will be thrown

    ("random" |:
      Prop.forAll { (s: Stream[Int], z: Int) => mockMap(s)(n => n / 0).take(z).isInstanceOf[Stream[Int]] })
  }

  it should "not force (n+1)st head ever (even if we force all elements of take(n))" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])

    ("random" |:
      Prop.forAll { (s: Stream[Int], z: Int) => Stream.cons(z, Stream.cons(z, Stream.cons(z, Stream.cons(z, mockMap(s)(n => n / 0)))))
        .take(4).toList.isInstanceOf[List[Int]] })
  }

  it should "s.take(n).take(n) == s.take(n) for any Stream s and any n (idempotency)" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])

    ("random" |:
      Prop.forAll { (s: Stream[Int], n: Int) => s.take(n).take(n).toList equals s.take(n).toList  })
  }

  behavior of "drop"
  // s.drop(n).drop(m) == s.drop(n+m) for any n, m (additivity)
  // s.drop(n) does not force any of the dropped elements heads
  // the above should hold even if we force some stuff in the tail

  behavior of "map"
  // x.map(id) == x (where id is the identity function)
  // map terminates on infinite streams

  behavior of "append"

}
