import java.util.Scanner;
import java.util.ArrayList;

public class Lexer {
    public void tokenizeCode() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your code (press Enter with no text to finish):");


        String input = "";


        while (true) {  //reading until empty line
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            input += line + "\n";  // Add the line to the input
        }


        String[] words = input.split("[\\s,;(){}+=]");  // Split by spaces, commas, semicolons, etc.


        ArrayList<String> tokens = new ArrayList<>(); //list for tokens


        for (String word : words) { //adding nonempty strings to token array
            if (!word.isEmpty()) {
                tokens.add(word);
            }
        }

        System.out.println("Tokens:");
        for (String token : tokens) {
            System.out.println(token);  // printing each token
        }

        scanner.close();
    }
}
