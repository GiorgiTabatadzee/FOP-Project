public class GCD {
    public static void main(String[] args) {

        String code = """
         var a = 56
                 var b = 98
                 while (b != 0) {
                 var temp = b
                 b = a % b
                 a = temp
                 }
                 println(a)
                
                
                 
        """;

         KotlinToJavaInterpreterMain interpreter = new KotlinToJavaInterpreterMain();
        interpreter.interpret(code);
    }
}
