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

  behavior of "01 - headOption"

  // a scenario test:

  it should "return None on an empty Stream" in {
    assert(empty.headOption == None)
  }

  // An example generator of random finite non-empty streams
  def list2stream[A] (la :List[A]): Stream[A] = la.foldRight (empty[A]) (cons[A](_,_))

  // In ScalaTest we use the check method to switch to ScalaCheck's internal DSL
  def genNonEmptyStream[A] (implicit arbA :Arbitrary[A]) :Gen[Stream[A]] =
    for { la <- arbitrary[List[A]] suchThat (_.nonEmpty)}
    yield list2stream (la)

  def genNonEmptyPositiveStream (implicit arb: Arbitrary[Int]): Gen[Stream[Int]] =
    for {
      n <- Gen.choose (1,500)
      g = Gen.listOfN[Int] (n, arb.arbitrary)
      l <- g
    } yield list2stream(l)

  def genNonEmptyStreamN (n: Int)(implicit arb: Arbitrary[Int]): Gen[Stream[Int]] =
    for {
      l <- Gen.listOfN[Int] (n, arb.arbitrary)
    } yield list2stream(l)

  def genStreamSize[A](size: Gen[Int])(implicit arb: Arbitrary[Int]): Gen[Stream[Int]] =  
    for {
      n <- size
      g = Gen.listOfN[Int](n, arb.arbitrary) 
      l <- g
    } yield list2stream(l)

  //MOCK METHODS; to test with these not with the ones being tested 

  def mockMap[A, B](s: Stream[A])(f: A => B): Stream[B] = s match {
    case Cons(h, t) => Stream.cons(f(h()), mockMap(t())(f))
    case _ => Stream.empty
  }

  // a property test:

  it should "return the head of the stream packaged in Some" in check {
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

  behavior of "02 - take(n)"
  
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

  behavior of "03 - drop(n)"

  it should "s.drop(n).drop(m) == s.drop(n+m) for any n, m (additivity)" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])
    // generate positive integers: m is 5, but n is -3 then it will fail since additivity does not apply
    // it will drop 5 on one side and onæy 2 on the other side 
    implicit def arbPositiveInt = Arbitrary[Int] (Gen.choose(1, 5000))

    ("random" |:
      Prop.forAll { (s: Stream[Int], n: Int, m: Int) => s.drop(n).drop(m).toList equals s.drop(n + m).toList  })
  }

  it should "not force any of the dropped elements heads" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])

    ("random" |:
      Prop.forAll { (s: Stream[Int], n: Int) => mockMap(s)(n => n/0).drop(n).isInstanceOf[Stream[Int]]  })
  }

  it should "not force any of the dropped elements heads even if we force some stuff in the tail" in check {
    val size: Int = 20
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStreamN(size))
    implicit def arbPositiveInt = Arbitrary[Int] (Gen.choose(1, 5000))

    ("random" |:
      Prop.forAll { (s: Stream[Int], m: Int) => {
        val ss = Stream(s.take(size/2), (mockMap(s.take(size - size/2))(x => x/0)))
        ss.flatMap(s => s.drop(m)).isInstanceOf[Stream[Int]]
      }  
    })
  }

  behavior of "04 - map"
  it should "evaluate to true: x.map(id) == x (where id is the identity function)" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])
    
    ("random" |:
      Prop.forAll { (s: Stream[Int]) => s.map(n => n).toList equals s.toList  })
  }

  it should "terminate on infinite streams" in check {
    ("random" |:
      Prop.forAll { (n: Int) => Stream.from(n).map(x => x + 1).isInstanceOf[Stream[Int]]  })
  }

  behavior of "05 - append"
  it should "should not force the stream that is being concatenated" in check {
    // stream will be a non-empty, finite stream
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])
    ("random" |:
      Prop.forAll { (s1: Stream[Int], s2: Stream[Int]) => s1.append(mockMap(s2)(n => n/0)).isInstanceOf[Stream[Int]]  })
  }

  it should "the size of s1.append(s2) should equal to s2.append(s1)" in check {
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])
    
    ("random" |:
      Prop.forAll { (s1: Stream[Int], s2: Stream[Int]) => {
          val l1 = s1.append(s2).toList
          val l2 = s2.append(s1).toList 
          l1.size == l2.size
        } 
      })
  }

  it should "s1.append(s2).toList should be equal to (s1.toList).append((s2.toList))" in check {
     //we assume that the append in the List works as expected
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])

    Prop.forAll{(s1 :Stream[Int], s2 :Stream[Int]) => ((s1.toList).++(s2.toList)) == (s1.append(s2)).toList }
  }

  it should "the sum of s1.append(s2) = sum of s2.append(s1)" in check {
    //we assume that the append in the List works as expected
    implicit def arbIntStream = Arbitrary[Stream[Int]] (genNonEmptyStream[Int])
    Prop.forAll{ (s1 :Stream[Int], s2 :Stream[Int]) => {
        val l1 = s1.append(s2).toList 
        val l2 = s2.append(s1).toList

        l1.foldRight(0)(_+_) == l2.foldRight(0)(_+_)
      }
    }
  }    
}
