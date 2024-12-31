public class MultiplicationTable {
    public static void main(String[] args) {

        String code = """
         var number = 5
         var i = 1
         while (i <= 10) {
         var result = number * i
         println(result)
         i++
                            }
                
                 
        """;

        KotlinToJavaInterpreter interpreter = new KotlinToJavaInterpreter();
        interpreter.interpret(code);
    }
}
