// Advanced Programming
// Andrzej Wąsowski, IT University of Copenhagen

package adpro
import Stream._


//See "Main" as a Java-like "main" method. 
object Main extends App {
    
    println("Welcome to Streams, the ADPRO 030 class!!")

    val l1: Stream[Int] = empty
    val l2: Stream[Int] = cons(1, cons(2, cons (3, empty)))
    val l3: Stream[Int] = cons(4, cons(5, cons (6, empty)))
    val l4: Stream[Stream[Int]] = Stream(Stream(1,2,3), Stream(4,5,6)) 
    val l5: Stream[Int] = Stream(1,2,3,4)
    val l6: Stream[Int] = Stream(5,6,7)

    println ("headOption: " + l1.headOption)
    println ("headOption: " + l2.headOption)
    println("toList: " + l2.toList)
    println("take: " + l3.take(4).toList)

    println(naturals.drop(5).take(10).toList)
    println(naturals.take(1000000000).drop(41).take(10).toList)
    println(naturals.takeWhile(_ < 20).toList)
    println(naturals.forAll(_ > 20))
    println(naturals.takeWhile2(_ < 20).toList)
    println(naturals.headOption2())
    println("map " + naturals.map(_ + 2).take(5).toList)
    println("filter: " + naturals.drop(42).filter (_%2 ==0).take (30).toList)
    println("append: " + l2.append(l3).append(l3).take(10).toList)
    println("append2: " + l5.append(l6).toList)
    println("toList2: " + naturals.take(4).toList2)
    println("flatMap: " + l4.flatMap(x => x).toList)
    println("fibonacci: " + l1.fib.take(10).toList)

    println("unfold: " + naturals.unfold(0)(x => Some( x, x + 1 )).take(5).toList )
    println("unfold2: " + naturals.from2(10).take2(5).toList )
}
