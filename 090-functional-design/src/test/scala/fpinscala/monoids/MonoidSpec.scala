// Advanced Programming 2015
// Andrzej Wasowski, IT University of Copenhagen
// Example solution for scala exercises using scalacheck
// Scalacheck's user guide:
// https://github.com/rickynils/scalacheck/wiki/User-Guide

package fpinscala.monoids
import org.scalacheck._
import org.scalacheck.Prop._
import Arbitrary.arbitrary

object MonoidSpec extends Properties("Monoids..") {

  import Monoid._

  // Exercise 4 (intro to the exercise)

  def associative[A :Arbitrary] (m: Monoid[A]) :Prop =
    forAll { (a1: A, a2: A, a3: A) =>
      m.op(m.op(a1,a2), a3) == m.op(a1,m.op(a2,a3)) } :| "associativity"

  def unit[A :Arbitrary] (m :Monoid[A]) =
    forAll { (a :A) => m.op(a, m.zero) == a } :| "right unit" &&
    forAll { (a :A) => m.op(m.zero, a) == a } :| "left unit"

  def monoid[A :Arbitrary] (m :Monoid[A]) :Prop = associative (m) && unit (m)

  property ("stringMonoid is a monoid") = monoid (stringMonoid)

  // Exercise 4: test intAddition, intMultiplication, booleanOr,
  // booleanAnd and optionMonoid.

  property("intAddition is monoid") = monoid(intAddition)
  property("intMultiplication is monoid") = monoid(intMultiplication)
  property("booleanOr is monoid") = monoid(booleanOr)
  property("booleanAnd is monoid") = monoid(booleanAnd)
  property("optionMonioid is monoid") = monoid[Option[Int]](optionMonoid)

  // Exercise 5

  def homomorphism[A :Arbitrary,B :Arbitrary]
    (ma: Monoid[A]) (f: A => B) (mb: Monoid[B]) =
      forAll { (x: A, y: A) => mb.op(f(x), f(y)) == f(ma.op(x, y)) }

  //f andThen g and g andThen f are identity

  def isomorphism[A :Arbitrary, B :Arbitrary]
    (ma: Monoid[A]) (f: A => B) (mb: Monoid[B]) (g: B => A) = 
      homomorphism(ma)(f)(mb) == homomorphism(mb)(g)(ma)

  // A string can be translated to a list of characters using the toList method.
  // The List.mkString method with default arguments (no arguments) does the opposite conversion.
  property("stringMonoid and listMonoid[Char] are isomorphic") = 
    isomorphism(stringMonoid)((s: String) => s.toList)(listMonoid)((as: List[Char]) => as.mkString)

  // Exercise 6

  property("booleanOr and booleanAnd are homomorphic via !") = 
    homomorphism(booleanOr)((a: Boolean) => !a)(booleanAnd)
  
  property("booleanAnd and booleanOr are homomorphic via !") = 
    homomorphism(booleanAnd)((a: Boolean) => !a)(booleanOr)
  
  property("booleanAnd and booleanOr are isomorphic via !") = 
    isomorphism(booleanAnd)((a: Boolean) => !a)(booleanOr)((a: Boolean) => !a)

  // Exercise 7 (the testing part)
  property ("productMonoid is a monoid") = 
    monoid[(Option[Int], List[String])](productMonoid[Option[Int], List[String]](optionMonoid)(listMonoid))
}
