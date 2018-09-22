//rng has some internal state,  that gets updated after each invocation
val rng = new scala.util.Random

rng.nextDouble
rng.nextDouble
rng.nextInt
rng.nextInt(10)

//not referentialy transparent

def rolldie: Int = {
  val rng = new scala.util.Random
  rng.nextInt(6)
}

rolldie + rolldie //results are not the same on each run

def rolldie2(rng: scala.util.Random): Int = rng.nextInt(6)
rolldie2(rng)
//same generator has to be created with the same seed and also be in the same state
//every time we call next int the previous state of the random number generator gets destroyed

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

val rng_state = SimpleRNG(42)
val (n1, rng_state2) = rng_state.nextInt

val (n2, rng_state4) = rng_state2.nextInt

val minIntValue = Int.MinValue
val maxIntValue = Int.MaxValue

def nonNegativeInt(rng: RNG): (Int, RNG) = {
  val (i, rng2) = rng.nextInt
  val naturalNumber = if(i < 0) -(i + 1) else i
  (naturalNumber, rng2)
}
nonNegativeInt(new SimpleRNG(3))

def double(rng: RNG): (Double, RNG) = {
  val (i, rng2) = nonNegativeInt(rng)
  val d = i / (Int.MaxValue.toDouble + 1)
  (d, rng2)
}

double(new SimpleRNG(3))

Int.MaxValue
Int.MaxValue.toDouble
//Int.MaxValue + 1 //will give int min value
//Int.MaxValue + 2 //will give int min value + 1
//Int.MaxValue + Int.MaxValue

val nonNeg = nonNegativeInt(new SimpleRNG(3))._1
Int.MaxValue

Int.MaxValue/(Int.MaxValue.toDouble + 1)

def intDouble(rng: RNG): ((Int, Double), RNG) = {
  val (i,rng2) = nonNegativeInt(rng)
  val (d, rng3) = nonNegativeInt(rng2)
  ((i,d), rng2)
}

def doubleInt(rng: RNG): ((Double, Int), RNG) = {
  val ((i,d), rng2) =   intDouble(rng)
  ((d, i), rng2)
}

def double3(rng: RNG): ((Double, Double, Double), RNG) = {
  val (d1, rng2) = double(rng)
  val (d2, rng3) = double(rng2)
  val (d3, rng4) = double(rng3)
  ((d1, d2, d3), rng4)
}

def f[A] : RNG =>(A,RNG) = ???

def ints(count: Int)(rng: RNG):(List[Int], RNG) = {
  @annotation.tailrec
  def loop(n: Int, rng: RNG, l: List[Int]):(List[Int], RNG) = {
    if (n == 0) (l, rng)
    else{
      val (i, newRng) = rng.nextInt
      loop(n-1, newRng, i::l)
    }
  }

  loop(count, rng, Nil: List[Int])
}

ints(4)(new SimpleRNG(42))

//f[A] : RNG =>(A,RNG)
type Rand[+A] = RNG => (A, RNG)

val int: Rand[Int] = _.nextInt
val int2: Rand[Int] = rng => rng.nextInt

int(new SimpleRNG(42))

def unit[A](a: A): Rand[A] = rng => (a, rng)

unit(Nil: List[Int])(rng_state)

def map[A,B](s: Rand[A])(f: A => B): Rand[B] = rng => {
  val (a, rng2) = s(rng)
  (f(a), rng2)
}

//transforms odd numbers into even numbers by subtracting either 1 or 0 from a number
def nonNegativeEven: Rand[Int] =
  map(nonNegativeInt)(i => i - i % 2)

val (i, r) = nonNegativeEven(rng_state)
nonNegativeEven(r)

lazy val _double: Rand[Double] =
  map(nonNegativeInt)(i => i / (Int.MaxValue.toDouble + 1))

_double(new SimpleRNG(42))

/*def double(rng: RNG): (Double, RNG) = {
  val (i, rng2) = nonNegativeInt(rng)
  val d = i / (Int.MaxValue.toDouble + 1)
  (d, rng2)
}*/

def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = rng => {
  val (a, rng2) = ra(rng)
  val (b, rng3) = rb(rng2)
  (f(a,b), rng3)
}


/*
  def intDouble(rng: RNG): ((Int, Double), RNG) = {
    val (i,rng2) = nonNegativeInt(rng)
    val (d, rng3) = nonNegativeInt(rng2)
    ((i,d), rng2)
  }
*/

def both[A,B](ra: Rand[A], rb: Rand[B]): Rand[(A, B)] =
  map2(ra, rb)((_,_))

val randIntDouble: Rand[(Int, Double)] =
  both(int, double)

val randDoubleInt: Rand[(Double, Int)] =
  both(double, int)

randDoubleInt(rng_state)

def sequence[A](fs: List[Rand[A]]): Rand[List[A]] =
  fs.foldRight(unit(Nil:List[A]))((h, t) => map2(h, t)((h,t) => (h::t)))


List(unit(1)(rng_state), unit(2)(rng_state), unit(3)(rng_state))
unit(List(1,2,3))(rng_state)

def _ints(count: Int): Rand[List[Int]] =
  sequence(List.fill(count)(int))

def flatMap[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = rng => {
  val (a, rng2) = f(rng)
  g(a)(rng2)
}

def nonNegativeLessThan(n: Int): Rand[Int] =
  flatMap(nonNegativeInt){
    i =>
      val mod = i % n
      if (i + (n-1) - mod >= 0) unit(mod) else nonNegativeLessThan(n)
  }

def mapViaFlatMap[A,B](f: Rand[A])(g: A => B): Rand[B] =
  flatMap(f){
    i =>
      unit(g(i))
  }

def map2ViaFlatMap[A, B, C](ra: Rand[A])(rb: Rand[B])(f: (A, B) => C): Rand[C] =
  flatMap(ra){
    a =>
      map(rb){
        b => f(a,b)
      }
  }
  /*for{
    a <- ra
    b <- rb
  } f(a,b)*/

