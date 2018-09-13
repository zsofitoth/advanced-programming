// Advanced Programming
// Andrzej Wąsowski, IT University of Copenhagen

package adpro
import Stream._


//See "Main" as a Java-like "main" method. 
object Main extends App {
    
    println("Welcome to Streams, the ADPRO 030 class!!")

    val l1 :Stream[Int] = empty
    val l2 :Stream[Int]= cons(1, cons(2, cons (3, empty)))

    println (l1.headOption)
    println (l2.headOption)
    println(l2.toList)

    println(naturals.drop(5).take(10).toList)
    println(naturals.take(1000000000).drop(41).take(10).toList)
    println(naturals.takeWhile(_ < 20).toList)
}
