//make state updates EXPLICIT
trait RNG {
  //return random number and new state, leaving the old state unmodified
  def nextInt: (Int, RNG)
}

case class SimpleRNG(seed: Long) extends RNG {
  def nextInt: (Int, RNG) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRNG = SimpleRNG(newSeed)
    val n = (newSeed >>> 16).toInt
    (n, nextRNG)
  }
}

import State._

case class State[S, +A](run: S => (A, S)) {

  // Exercise 9 (6.10)

  def map[B](f: A => B): State[S, B] = State {
    (s: S) =>
      val (a, s2) = run(s)
      (f(a), s2)
  }

  def map2[B,C](sb: State[S, B])(f: (A, B) => C): State[S, C] =
    for {
      b <- sb
      a <- this
    } yield f(a,b)

  def flatMap[B](f: A => State[S, B]): State[S, B] = State {
    (s: S) =>
      val (a, s2) = run(s)
      f(a).run(s2)
  }
}

object State {
  type Rand[A] = State[RNG, A]

  def unit[S, A](a: A): State[S, A] =
    State(s => (a, s))

  // Exercise 9 (6.10) continued

  def sequence[S,A](sas: List[State[S, A]]): State[S, List[A]] =
    sas.foldRight(unit[S, List[A]](Nil: List[A]))((h,t) => h.map2(t)((h,t) => (h::t)))

  // This is given in the book:

  def modify[S](f: S => S): State[S, Unit] = for {
    s <- get // Gets the current state and assigns it to `s`.
    _ <- set(f(s)) // Sets the new state to `f` applied to `s`.
  } yield ()

  def get[S]: State[S, S] = State(s => (s, s))

  def set[S](s: S): State[S, Unit] = State(_ => ((), s))

  def random_int :Rand[Int] =  State (_.nextInt)

  // Exercise 10

  def state2stream[S,A] (s :State[S,A]) (seed :S) :Stream[A] =
    s.run(seed) match {
      case (a, s1) => a#::state2stream(s)(s1)
    }


  // Exercise 11 (lazy is added so that the class does not crash at load time
  // before you provide an implementation).

  lazy val random_integers = state2stream(random_int)(new SimpleRNG(42))

}

random_integers.take(10).toList