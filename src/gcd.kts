fun main() {
    var a = 48
    var b = 18
    while (b != 0) {
        val temp = b
        b = a % b
        a = temp
    }
    println("GCD: \$a")
}
