n = input("Enter the value of n: ") // Get the position (n) of the Fibonacci number from the user
a = 0
b = 1
i = 2  /

if n == 0 {
    fib = 0  // The 0th Fibonacci number is 0
} else if n == 1 {
    fib = 1  // The 1st Fibonacci number is 1
} else {
    while (i <= n) {
        fib = a + b  // Compute the next Fibonacci number
        a = b
        b = fib
        i = i + 1    // Move to the next position in the sequence
    }
}
print(fib)  // Output the n-th Fibonacci number
