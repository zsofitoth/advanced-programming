**Zsófia Tóth.** *December, 2018*
# AP Cheat Sheet
## Basics
- useful list operations
  - ```List.range(n, m)``` 
    - where $n$ is start and $m$ is end
  - ```List.fill(n)("foo")``` 
    - where $n$ is the number of times it needs to be filled
- referential transparency (RT)
- higher order functions
- recursion, tail recursion
- compose, curried functions
- parametric polymorphism
- pattern matching
- covariant
    - ```class Foo[+A]```
        - For some class ```List[+A]```, making ```A``` contravariant implies that for two types ```A``` and ```B``` where ```A``` is a subtype of ```B```, then ```List[A]``` is a subtype of ```List[B]```   
- contravariant
    - ```class Bar[-A]```
        -  For some class ```Writer[-A]```, making ```A``` contravariant implies that for two types ```A``` and ```B``` where ```A``` is a subtype of ```B```, ```Writer[B]``` is a subtype of ```Writer[A]``` 
- invariant
    - ```class Baz[A]```
    - neither covariant nor contravariant
- construction
    - ```::``` = List
    - ```#::``` = Stream  
- ```map```
    - ```def map[B](f: A => B): List[B]```
    - ```foldRight``` + construction
         - ```foldRight(Nil: Empty[B])((h,t) => f(h)::(t))```
- ```append```
    - ```:::``` = List
    - ```#:::``` = Stream
- ```flatMap```
    - ```def flatMap[B] (f: A => List[B]) : List[B]```
    - ```foldRight``` + appending
        - ```foldRight(Nil: Empty[B])((h,t) => f(h):::(t))```  
- ```filter```
    - ```def filter(f: A => Boolean): List[A]```
        - ```foldRight```   
- ```zipWith```
    - apply a binary $op$ to each element from both lists at the *same* position 
    - ```def zipWith[A,B,C] (f: (A,B)=>C) (l: List[A], r: List[B]): List[C]```   
### Folding
- fold "is the new" pattern matching with recursion
- $z$: *initial element*
- $op$: ```f: (A, B) => B```
- foldLeft
  - ```def foldLeft[A,B] (z: B) (f: (B, A) => B): B ```
  - starts from the ledt side of the list/stream/...
  - ```List(1, 2, 3)``` = ```Cons(1, Cons(2, Cons(3, Nil)))```
  - ```f(3, f(2, f(z, 1)))```
    - $op$ is $-$, $z$ is $0$
      - $((0 - 1) - 2) - 3 = -6$
- foldRight
  - ```def foldRight[A,B] (z: B) (f: (A, B) => B): B ```
  - starts from the right side of the list/stream/...
  - ```List(1, 2, 3)``` = ```Cons(1, Cons(2, Cons(3, Nil)))```
  - ``` f(1, f(2, f(3, z))) ```
    - $op$ is $-$, $z$ is $0$
    - $1 - (2 - (3 - 0)) = 2$
### Algebraic Data Types
#### Introduction
##### How many values do they have ...
- ```Nothing```
    - $0$
- ```Unit```
    - $1$ 
- ```Boolean```
    - $2$ (*true, false*)
- ```Byte```
    - $256$ 
- ```String```
    - $many$
- ```(Byte, Boolean)```
    - $2 × 256 = 512$ 
- ```(Byte, Unit)```
    - $1 x 256 = 256$
- ```(Byte, Byte)```
    - $256 × 256 = 65536$   
- ```(Byte, Boolean, Boolean)```
    - $256 × 2 × 2 = 1024$
- ```(Boolean, String, Nothing)```
    - $2 × many × 0 = 0$
- ```Byte``` or ```Boolean```
    - $2 + 256 = 258$
- ```Boolean``` or ```Unit```
    - $2 + 1 = 3$
- ```Boolean``` or ```(String, Nothing)```
    - $2 + many × 0 = 2$
##### Classes
- Scala
```Scala
class ScalaPerson(val name: String, val age: Int)
```
- Java
```Java
class JavaPerson {
  final String name;
  final Int age;
}
```
#### Sum Types
- ADT = **SUM TYPE**
- Sum type
    - This **or** That
```Scala
sealed trait Pet
case class Cat(name: String) extends Pet
case class Fish(name: String, color: Color) extends Pet
case class Squid(name: String, age: Int) extends Pet

val bob: Pet = Cat("Bob")
```
- Destructed by pattern matching
```Scala
def sayHi(p: Pet): String = 
  p match {
    case Cat(n) => "Meow " + n + "!"
    case Fish(n, _) => "Hello fishy " + n + "."    
    case Squid(n, _) => "Hi " + n + "."    
  }
```
#### Computations that may return many answers
##### List
```Scala
sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]
```
##### Tree
```Scala
sealed trait Tree[+A]
case class Leaf[A] (value: A) extends Tree[A]
case class Branch[A] (left: Tree[A], right: Tree[A]) extends Tree[A]
```
##### Stream
```Scala
sealed trait Stream[+A]
case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]
```
#### Computations that may fail to return a value
##### Option
```Scala
sealed trait Option[+A] 
case object None extends Option[Nothing]
case class  Some[A](a: A) extends Option[A]
```
#### Computation that either returns this or that
##### Either
```Scala
sealed trait Either[+A, +B] 
case class Left[A](a: A)  extends Either[A, Nothing]
case class Right[B](b: B) extends Either[Nothing, B]
```
#### Computations that may fail with an exception
##### Try
```Scala
sealed trait Try[+A]
case class Success[A](a: A) extends Try[A]
case class Failure[A](t: Throwable) extends Try[A]
```
## Options
- ```Some("value")```and ```None```
- for comprehensions
    - ```for { } yield ()```
- ```map2```
- ```sequence```
- ```Either[+A, +B]```
    - ```Left```; error
    - ```Right```; result
## Streams
- infinite streams
    - ```from```
    - ```to```
    - ```fibonacci```
- finite streams
- ```unfold```
    - ```def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A]```
- lazily evaluated
- ```append```
    - ```def append[B >: A](that: => Stream[B]): Stream[B]```
    -  lower type bound
    -  $B$ is constrained to be a supertype of $A$
    -  allowed to append element of type $B$ to a stream containing elements of type $A$
       -  the result is a stream of $B$ elements
    -  ```[B <: A]``` is an upper type bound
        - $B$ is constrained to be a subtype of $A$
- library function implementations with ```foldRight``` and $pattern$ $matching$ 
## State
- **RNG**
    - ```SimpleRNG```
        - class within ```RNG``` trait
        - takes a seed
        - ```nextInt```
          - $(Int, RNG)$
    - ```(A, RNG)```
      - $(value, rng)$
- **RAND**
    - ```type RAND[A] = RNG => (A, RNG)```
    - ```RAND``` type alias for statw
      - ```type RAND[A] = State[RNG, A]```   
    - ```unit```
        - ```def unit[A](a: A): Rand[A]```
        - produces: ```rng => (value, next)```
    - ```map```
    - ```map2```
    - ```flatMap```
        - bit tricky because of ```f: A => RAND[B]```
        ```Scala
        rng => {
            val (value, rng2) = ra(rng)
            // f(value) will be a RAND, so pass rng2 to get next value and next state
            val (value2, rng3) = f(value)(rng2)
            (value2, rng3)
        }
        ```
    - ```sequence```
      - ```foldRight```
        - $z$
          - ```unit(Nil: List[A])```
        - $op$
          - ```map2```
    - ```List.fill(n)(int)```
        - create a list of size $n$ with ```RAND[Int]```
        - ```val int: Rand[Int] = _.nextInt```
        - same as ```rng => rng.nextInt```
- **STATE**
  - make any API purely functional
  - state updates are not RT
    - return the new state along with the value generated
  - this is a more general implementation of ```RAND```
    - ```type Rand[A] = State[RNG, A]```
  - ```case class State[S, +A](run: S => (A, S))```
  - ```type State[S, +A] = S => (A, S)```
  - general-purpose functions for capturing states 
  - unfold and apply state
    ```Scala
    State {
        (s: S) => {
            //must return State[S, B]
        }
    }
    ```
  - ```state2stream```
    - ```def state2stream[S,A] (state: State[S,A]) (seed: S) :Stream[A]```; signature
    - implementation:
        ```Scala
        state.run(seed) match {
            case(a, s2) => a#::state2stream(state)(s2)
        }
      ```
      - construct a stream from the next value (a)
      - new "seed" to run is the "next" on the same state 
## Algebraic Design
- the following 3 chapters have been introduced as part of AD
    - the process of designing purely functional libraries
- parser combinators is heavily focused on AD and contains exercises related to designing a library
- API can be described by and algebra and obey specific $laws$
### Par
- purely functional library for parallel and asynchronous computations
- seperate the concern from *describing* a computation to actually *running* it
- *describe*
  - ```def unit[A](a: => A): Par[A]```
    - take an unevaluated $A$ 
    - return a computation that might evaluate in a seperate *thread*
        - ```(es: ExecutorService) => UnitFuture(a)```
            - wrap it in a future task (something that will get evaluated eventually)
- *extract*
    - ```def get[A](a: Par[A]): A```
        - get resulting value from a parallel computation
- combine asynchronous computations without waiting for them to finish
    - avoid combining ```unit``` and ```get```
- ```type Par[A] = ExecutorService => Future[A]```
    - takes an executor service and wraps A in a future
-  ```def run[A] (es: ExecutorService) (a: Par[A]) : Future[A] = a(es)```
- $unit$
    - creates computation that immediately results in the value $a$
- $fork$
  - marks a computation for concurrent evaluation
  - evaluation won't occur until forces by run
- $lazyUnit$
    - wraps it's unevaluated arguement in a $Par$ and marks it for concurrent evaluation
    - ```fork(unit(a))```
- $run$
    - fully evaluates given $Par$ 
    - spawns parallel computations as requested by $fork$
    - extracts the resulting value
- $map2$
    - combines the result of 2 parallel computations with a $binary$ function
    - does not evaluate the call to $f$ in a seperate logical thread
    - $fork$ is controlling all parallelism
    - ```fork(map2(a, b)(f))```
        - if we want the evaluation of $f$ to occur in a seperate thread   
- $parMap$
    - combines $N$ parallel computations
    - apply a function $f$ to every element in a collection $simultaneously$
- $parFilter$
### Property Testing
#### Generators
#### Laws
### Parser Combinators
## Functional Design
### Monoids
- foldables
### Functors
- map
### Monads
- flatMap
## Lenses
## Finger Trees
