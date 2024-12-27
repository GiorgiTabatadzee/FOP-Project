import java.util.*;
import java.util.regex.*;

public class Main {
    public static void main(String[] args) {

        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        // Print instructions
        System.out.println("Enter your code (press Enter with no text to finish):");

        // Create a string to store the entire input
        String input = "";

        // Read each line from the user until an empty line is entered
        while (true) {
            String line = scanner.nextLine();  // Read one line
            if (line.isEmpty()) {  // If the line is empty, break the loop
                break;
            }
            input += line + "\n";  // Add the line to the input
        }

        // Define the pattern (regular expression) to match different parts of the code
        String regex = "(var|val|for|in|until|[a-zA-Z_][a-zA-Z0-9_]*|[0-9]+|[=+(){},;])";

        // Create a pattern from the regex
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher to find parts of the input that match the pattern
        Matcher matcher = pattern.matcher(input);

        // Loop through all matches and print them
        System.out.println("Tokens:");
        while (matcher.find()) {
            System.out.println(matcher.group());  // Print each token
        }
        scanner.close();
    }
}