object MyModule {
    def abs(n: Int) : Int = {
        if (n < 0) -n
        else n
    }

    //tail recursion
    //acc: accumulator
    def factorial(n: Int) : Int = {
        def go(n: Int, acc: Int) : Int =
            if(n <= 0) acc
            else go(n-1, n*acc)
        go(n, 1)
    }

    private def formatResult(name: String, n: Int, f: Int => Int) = {
        val msg = "The %s of %d is %d."
        msg.format(name, n, f(n))
    }

    def main(args: Array[String]) : Unit = {
        println(formatResult("absolute value", -42, abs))
        println(formatResult("factorial", 3, factorial))
    }
}
