sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {
  def apply[A](as: A*): List[A] = {
    if(as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
  }

  def tail[A](as: List[A]): List[A] = as match {
    case Nil => sys.error("empty list")
    case Cons(_, t) => t
  }

  // Generalize tail remove the first n elements of a lisy
  def drop[A](l: List[A], n: Int): List[A] = 
  if(n == 0) l
  else l match {
      case Nil => sys.error("empty list")
      case Cons(_, t) => drop(t, n-1)
  }

  // Remove elements as long as they satisfy the predicate
  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
      case Nil => sys.error("empty list")
      case Cons(h, t) if(f(h)) => dropWhile(t, f)
      case _ => l
  }

  def dropWhile2[A](l: List[A])(f: A => Boolean): List[A] = l match {
      case Nil => sys.error("empty list")
      case Cons(h, t) if(f(h)) => dropWhile(t, f)
      case _ => l
  }

  def setHead[A](as: List[A], z: A): List[A] = as match {
    case Nil => sys.error("empty list")
    case Cons(_, t) => Cons(z, t)
  }

  def append[A](a1: List[A], a2: List[A]): List[A] = a1 match {
      case Nil => a2
      case Cons(h, t) => Cons(h, append(t, a2))
  }

  //a1 is prepended to a2 in reverse order (folding from left)
  @annotation.tailrec
  def append1[A](a1: List[A], a2: List[A]): List[A] = a1 match {
      case Nil => a2
      case Cons(h, t) => append1(t, Cons(h, a2))
  }

  //TODO: append in the right order
  def append2[A](a1: List[A], a2: List[A]): List[A] = ???

  def init[A](l: List[A]): List[A] = l match {
      case Nil => sys.error("empty list")
      case Cons(_, Nil) => Nil
      case Cons(h, t) => Cons(h, init(t))
  }
  
  //does it in reverse order (folding from left)
  def init1[A](l: List[A]): List[A] = {
      @annotation.tailrec
      def loop(l: List[A], z: List[A]): List[A] = l match {
          case Nil => sys.error("empty list")
          case Cons(_, Nil) => z
          case Cons(h, t) => loop(t, Cons(h, z)) 
      }
      loop(l, Nil: List[A])
  }

  //TODO: append from the right order
  def init2[A](l: List[A]): List[A] = ???
  
  // processes operation/function on the list from right (last value) to left order  
  def foldRight[A,B](as: List[A], z: B)(f: (A,B) => B): B = as match {
      case Nil => z
      case Cons(h, t) => f(h, foldRight(t, z)(f))
      /*
       if List(1, 2, 3, 4) 
        => f(1, f(2, f(3, f(4, z)))) 
            so will start to evaluate 4 with z first where z is the initial value
      */
  }

  def length[A](as: List[A]): Int = 
    foldRight(as, 0)((_, count) => count + 1)
    //counting starts from 0 (z = 0) and from the last element

  def product(as: List[Int]): Int = 
    foldRight(as, 1)( _ * _ ) //order doesn't matter
  
  def sum(as: List[Int]): Int = 
    foldRight(as, 0)( _ + _ ) //order doesn't matter

  // processes operation/function on the list from left (first element) to right order
  /*
     That is, at the start of execution, the function will invoke the operation f 
      on the given intial value (z) and the first item of the list
  */
  @annotation.tailrec
  def foldLeft[A,B](as: List[A], z: B)(f: (B, A) => B): B = as match {
      case Nil => z
      case Cons(h, t) => foldLeft(t, f(z, h))(f)
      /*if List(1, 2, 3, 4) => 
         f( f( f( f(z, 1), 2), 3), 4) 
         so the function will evaluate with the first element, 1 and the initial element first*/
  }

  def length2[A](as: List[A]): Int = 
    foldLeft(as, 0)((count, _) => count + 1)
    //counting starts from 0 (z = 0) and from the first element

  def reverseViaFoldLeft[A](as: List[A]): List[A] = 
    foldLeft(as, Nil: List[A])((z, h) => Cons(h, z))

  def reverseViaFoldRight[A](as: List[A]): List[A] = ???
    /* foldRight(as, Nil: List[A])((h, z) => Cons(z, h)) 
        <= this won't work because Nil: List[A] is not of type B so it cannot be passed to Cons' first arguement (head)
        hence it'll throw a type mismatch error
        we can only reverse from left to right? 
    */

  def foldRightViaFoldLeft[A](as: List[A], z: B)(f: (A, B) => B): B = 
    foldLeft(reverseViaFoldLeft(as), z)((b, a) => f(a, b))

  def transform(as: List[Int]) : List[Int] = 
    foldRight(as, Nil: List[Int])((h, z) => Cons(h + 1, z) )

  def transformToString(as: List[Double]): List[String] = 
    foldRight(as, Nil: List[String])((h, z) => Cons(h.toString, z))

  def map[A,B](as: List[A])(f: A => B): List[B] = 
    foldRight(as, Nil: List[B])((h, z) => Cons(f(h), z))

  def filter[A](as: List[A])(f: A => Boolean): List[A] = 
    foldRight(as, Nil: List[A])((h, z) => if(f(h)) z else Cons(h, z))

  def concat[A](as: List[List[A]]): List[A] = 
    foldRight(as, Nil: List[A])((h, z) => append(h, z))
  /*
     List(List(1,2), List(3,4))
     => Cons(Cons(1,Cons(2,Nil)),Cons(Cons(3,Cons(4,Nil)),Nil))
     1. h: Cons(3,Cons(4,Nil)), z: Nil => append(h,z) => Cons(3,Cons(4,Nil)) <= new z
     2. h: Cons(1,Cons(2,Nil)), z: Cons(3,Cons(4,Nil)) => Cons(1, Cons(2, Cons(3, Cons(4, Nil))))

     A. append(List(3,4), Nil) => List(3, 4)
     B. append(List(1, 2), List(3, 4)) => List(1, 2, 3, 4)
  */

  def flatMap[A, B](as: List[A])(f: A => List[B]): List[B] = 
    foldRight(map(as)(f), Nil: List[B])((h, z) => append(h, z))
    //concat(map(as)(f))

  def filterViaFlatMap[A] (l: List[A]) (p: A => Boolean) :List[A] = 
    flatMap(l)(a => if (p(a)) List(a) else Nil)

  //List(1,2,3,4) and List(3,4,6) will produce List(4,6,9)
  def add (l: List[Int]) (r: List[Int]): List[Int] = (l, r) match {
      case (_, Nil) => Nil
      case (Nil, _) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(h1 + h2, add(t1)(t2))
  }

  def zipWith[A,B,C] (f: (A,B)=>C) (l: List[A], r: List[B]): List[C] = (l, r) match {
      case (_, Nil) => Nil
      case (Nil, _) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(f(h1, h2), zipWith(f)(t1, t2))
  }

  def calculateLists(l: List[Int]) (lt: List[Int]): List[Int] = (l, lt) match {
    case (_, Nil) => l
    case (Nil, _) => Nil
    case (Cons(h1, t1), Cons(h2, t2)) => Cons(h1 + h2, calculateLists(t1) (t2))
  }

  def pascal (n: Int): List[Int] = {
      
      def loop(l: List[Int], n: Int): List[Int] = {
        if (n==1) l
        else loop(Cons(1, calculateLists(l)(List.tail(l))), n-1)
      }

      loop(List(1), n)
  }

}

object ListMain extends App {
  //original list
  val as = List(1,2,3,4)
  val asCopy = List(1,2,3,4)

  val asDouble = List(1.0, 1.1, 2.1)
  val asToBeFiltered = List(1, 1, 1, 2, 1, 2)

  //copies made by transforming list as
  val tailResult = List.tail(as)
  val setHeadResult = List.setHead(as, 6)
  val dropResult = List.drop(as, 3)

  /* def operation(a: Int) : Boolean = a < 3 */
  //Type inference is not possible
  val dropWhileResult = List.dropWhile(as, (a: Int) => a < 3)

  //Currying so type inference is possible
  val dropWhileResult2 = List.dropWhile2(as)(a => a < 3)

  val appendResult = List.append(as, asCopy)
  val initResult = List.init(as)

  /*We get back the original list! Why is that? 
   As we mentioned earlier, one way of thinking about what `foldRight` "does" is it replaces the `Nil` constructor of the list with the `z` argument 
   and it replaces the `Cons` constructor with the given function, `f`. 
   If we just supply `Nil` for `z` and `Cons` for `f`, then we get back the input list.
  */
  
  //original list
  val foldRightResult = List.foldRight(as, Nil: List[Int])(Cons(_,_))
  //reverse of list
  val foldLeftResult = List.foldLeft(as, Nil: List[Int])((t, h) => Cons(h,t))

  val appendedListFromRight = List.foldRight(as, asCopy)(Cons(_,_))

  //symmetrical 
  val appendedListFromLeft = List.foldLeft(as, asCopy)((t, h) => Cons(h,t))

  val reverseViaFoldLeftResult = List.reverseViaFoldLeft(as)

  //println(appendedListFromRight)
  //println(appendedListFromLeft)

  val listOfLists = List(1,2,3, List(1,2,3))
  //println(listOfLists)

  val transformedList = List.transform(as)
  //println(transformedList)

  val transformedListToString = List.transformToString(asDouble)
  //println(transformedListToString)
  
  val filterList = List.filter(asToBeFiltered)(a => a%2 == 1)
  //println(filterList)

  //println(List(List(1, 2), List(3, 4)))

  //val appendList = List.append(List(1), Nil: List[Int])
  //println(appendList)

  def g(v: Int) = List(v-1, v, v+1)
  val flattenedMappedList = List.flatMap(as)(g)
  //println(flattenedMappedList)

  val pascalList = List.pascal(5)
  println(pascalList)

}
