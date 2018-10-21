package adpro
import java.util.concurrent._
import scala.language.implicitConversions

// Work through the file top-down, following the exercises from the week's
// sheet.  Uncomment and complete code fragments.
// AUTHOR1: Sadaf Zahid <saza@itu.dk>
// TIME1: 10 hours <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//
// AUTHOR2: Niels Viberg Sønderbæk <nivs@itu.dk>
// TIME2: 10 hours <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//
// AUTHOR3: Zsófia Tóth <zsto@itu.dk>
// TIME1: 10 hours <- how much time have you used on solving this exercise set
// (excluding reading the book, fetching pizza, and going out for a smoke)
//

object Par {

  type Par[A] = ExecutorService => Future[A]

  /*
    Fully evaluates a given Par, spawning parallel computations 
    as requested by fork and extracting the resulting value.
    Extracts the value from a Par by actually performing the operation
  */
  //responsibility of creating threads and submitting execution tasks
  def run[A] (s: ExecutorService) (a: Par[A]) : Future[A] = a(s)


  case class UnitFuture[A] (get: A) extends Future[A] {
    def isDone = true
    def get (timeout: Long, units: TimeUnit) = get //obtain a value from a Future
    def isCancelled = false
    def cancel (evenIfRunning: Boolean) : Boolean = false
  }

  //creates a computation that immediately results in the value 'a'
  def unit[A] (a: A) :Par[A] = (es: ExecutorService) => UnitFuture(a)

  /*
    Combines the results of two parallel computations with a binary function.
  */
  def map2[A,B,C] (a: Par[A], b: Par[B]) (f: (A,B) => C) : Par[C] =
    (es: ExecutorService) => {
      val af = a (es)
      val bf = b (es)
      UnitFuture (f(af.get, bf.get))
    }

  /*
    Marks a computation for concurrent evaluation by run.
    The evaluation won't actually occur until forced by run.
  */
  //its arguement gets evaluated in a seperate logical thread
  def fork[A] (a: => Par[A]) : Par[A] = 
    (es: ExecutorService) => {
      es.submit(new Callable[A] { def call = a(es).get })    
    }
  
  //wraps the expression 'a' for concurrent evaluation by run
  def lazyUnit[A] (a: => A) : Par[A] = fork(unit(a))

  // Exercise 1 (CB7.4)

  // converts A => B to A => Par[B] 
  def asyncF[A,B] (f: A => B) : (A => Par[B]) = 
    (a: A) => lazyUnit(f(a))

  // map is shown in the book

  def map[A,B] (pa: Par[A]) (f: A => B) : Par[B] =
    map2 (pa,unit (())) ((a,_) => f(a))

  // Exercise 2 (CB7.5)

  def sequence[A] (ps: List[Par[A]]): Par[List[A]] = 
    ps.foldRight(unit(Nil: List[A]))((uh, ut) => map2(uh,ut)( (h, t) => h::t ))

  // Exercise 3 (CB7.6)

  // this is shown in the book:

  def parMap[A,B](ps: List[A])(f: A => B): Par[List[B]] = fork {
     val fbs: List[Par[B]] = ps.map(asyncF(f))
     sequence(fbs)
  }

  def parFilter[A](as: List[A])(f: A => Boolean): Par[List[A]] = {
    val filtered: List[Par[List[A]]] =
      as.map(asyncF((a: A) => if(f(a)) List(a) else Nil: List[A]))
      map(sequence(filtered))(_.flatten)
  }

  //Could you explain if foldRight can be used for parFilter? (compiler errors)

  // Exercise 4: implement map3 using map2

  def map3[A,B,C,D] (pa :Par[A], pb: Par[B], pc: Par[C]) (f: (A,B,C) => D) :Par[D]  ={
    //partially apply function
    def partialCurry(a: A, b: B)(c: C): D = f(a, b, c)
    
    def applyFully(f: C => D, c: C): D = f(c)
    map2(map2(pa, pb)((a, b) => partialCurry(a, b)(_)), pc)((ab, c) => applyFully(ab, c))
  }

  // shown in the book

  def equal[A](e: ExecutorService)(p: Par[A], p2: Par[A]): Boolean = p(e).get == p2(e).get

  // Exercise 5 (CB7.11)
  //FROM THE BOOK
  def choice1[A] (cond: Par[Boolean]) (t: Par[A], f: Par[A]) : Par[A] =
    (es: ExecutorService) => {
      if(run(es)(cond).get()) t(es)
      else f(es)
    }

  def choiceN[A] (n: Par[Int]) (choices: List[Par[A]]) :Par[A] = 
    (es: ExecutorService) => {
      //runs n
      val i = run(es)(n).get

      //uses it to select a parallel computation from choiches 
      run(es)(choices(i))
    }

  def choice[A] (cond: Par[Boolean]) (t: Par[A], f: Par[A]) : Par[A] =
    //if the condition evaluates to true then n will be 0 and choice N will run t (the 0th index of the list), else it'll run the 1st index
    choiceN(map(cond)(x => if(x) 0 else 1))(List(t, f))

  // Exercise 6 (CB7.13)

  def chooser[A,B] (pa: Par[A]) (choices: A => Par[B]): Par[B] = 
    (es: ExecutorService) => {
      val x = run(es)(pa).get
      run(es)(choices(x))
    }

  def choiceNviaChooser[A] (n: Par[Int]) (choices: List[Par[A]]) :Par[A] = 
    chooser(n)(choices)

  def choiceViaChooser[A] (cond: Par[Boolean]) (t: Par[A], f: Par[A]) : Par[A] =
    chooser(map(cond)(x => if(x) 0 else 1))(List(t, f))

  // Exercise 7 (CB7.14)

  def join[A](a: Par[Par[A]]): Par[A] = 
    (es: ExecutorService) => run(es)(run(es)(a).get())

  def flatMapViaJoin[A,B](p: Par[A])(f: A => Par[B]): Par[B] = 
    join(map(p)(f))

  class ParOps[A](p: Par[A]) {

  }

  implicit def toParOps[A](p: Par[A]): ParOps[A] = new ParOps(p)
}
