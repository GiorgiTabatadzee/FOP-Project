import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class KotlinToJavaInterpreter {

    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, Boolean> booleanVariables = new HashMap<>();
    private final Stack<Boolean> executionStack = new Stack<>();
    private boolean breakFlag = false;



        public void interpret (String code){          //acts as compiler, handles some syntax errors,
                                                      // calls appropriate methods depended on input code
            String[] lines = code.split("\n");
            try {
                checkSyntax(lines);
            } catch (RuntimeException e) {
                System.out.println("Syntax Error: " + e.getMessage());
                return;
            }

            int lineIndex = 0;

            while (lineIndex < lines.length) {
                String line = lines[lineIndex].trim();

                if (line.startsWith("if")) {
                    lineIndex = handleNestedIfInIf(lines, lineIndex);
                } else if (line.startsWith("else")) {
                    if (executionStack.isEmpty() || executionStack.pop()) {
                        lineIndex = skipBlock(lines, lineIndex);
                    } else {
                        lineIndex = executeBlock(lines, lineIndex + 1);
                    }
                } else if (line.startsWith("while")) {
                    lineIndex = handleNestedIfInWhile(lines, lineIndex);
                } else {
                    interpretLine(line);
                    lineIndex++;
                }
            }
        }


    private int skipBlock(String[] lines, int lineIndex) {  //if needed this method helps us to skip block

        int openBraces = 1;
        lineIndex++;
        while (lineIndex < lines.length && openBraces > 0) {
            String line = lines[lineIndex].trim();
            if (line.equals("{")) {
                openBraces++;
            } else if (line.equals("}")) {
                openBraces--;
            }
            lineIndex++;
        }
        return lineIndex;
    }

    private int findEndOfBlock(String[] lines, int lineIndex) {

        int openBraces = 1;
        lineIndex++;
        while (lineIndex < lines.length && openBraces > 0) {
            String line = lines[lineIndex].trim();
            if (line.equals("{")) {
                openBraces++;
            } else if (line.equals("}")) {
                openBraces--;
            }
            lineIndex++;
        }
        return lineIndex - 1;
    }

    private int executeBlock(String[] lines, int start) {

        int lineIndex = start;
        while (lineIndex < lines.length && !lines[lineIndex].trim().equals("}")) {
            interpretLine(lines[lineIndex].trim());
            if (breakFlag) {
                breakFlag = false;
                break;
            }
            lineIndex++;
        }
        return lineIndex + 1;
    }


    private int handleNestedIfInIf(String[] lines, int lineIndex) {   //handles if statement in while
        String line = lines[lineIndex].trim();
        String condition = extractCondition(line);
        boolean conditionResult = evaluateBooleanExpression(condition);

        if (conditionResult) {
            lineIndex = executeBlock(lines, lineIndex + 1);
            executionStack.push(true);
        } else {
            lineIndex = skipBlock(lines, lineIndex);
            executionStack.push(false);
        }

        while (lineIndex < lines.length) {
            line = lines[lineIndex].trim();
            if (line.startsWith("else if")) {
                condition = extractCondition(line);
                conditionResult = evaluateBooleanExpression(condition);
                if (conditionResult) {
                    lineIndex = executeBlock(lines, lineIndex + 1);
                    executionStack.push(true);
                } else {
                    lineIndex = skipBlock(lines, lineIndex);
                    executionStack.push(false);
                }
            } else if (line.startsWith("else")) {
                if (executionStack.isEmpty() || executionStack.pop()) {
                    lineIndex = skipBlock(lines, lineIndex);
                } else {
                    lineIndex = executeBlock(lines, lineIndex + 1);
                }
                break;
            } else {
                break;
            }
        }
        return lineIndex;
    }

    private void interpretLine(String line) {   //recoginises essential things in line


            if (line.startsWith("var ") || line.startsWith("val ")) {
                handleVariableDeclaration(line);
            } else if (line.startsWith("boolean ")) {
                handleBooleanDeclaration(line);
            } else if (line.contains("+=") || line.contains("-=") || line.contains("*=") || line.contains("/=") || line.contains("=")) {
                handleVariableAssignment(line);
            } else if (line.startsWith("println")) {
                handlePrint(line);
            } else if (line.contains("++")) {
                handleIncrement(line);
            } else if (line.contains("--")) {
                handleDecrement(line);
            } else if (line.equals("break")) {
                handleBreak();
            }

    }

    private void handleVariableDeclaration(String line) {
        line = line.replace("var ", "").replace("val ", "").trim();
        String[] parts = line.split("=");
        String variableName = parts[0].trim();
        String expression = parts[1].trim();

        // Check if the expression is a boolean value
        if (expression.equals("true") || expression.equals("false")) {
            boolean value = Boolean.parseBoolean(expression);
            booleanVariables.put(variableName, value);
        } else {
            int value = evaluateExpression(expression);
            variables.put(variableName, value);
        }
    }

    private void handleBooleanDeclaration(String line) {
        line = line.replace("boolean ", "").trim();
        String[] parts = line.split("=");
        String variableName = parts[0].trim();
        String expression = parts[1].trim();

        boolean value = evaluateBooleanExpression(expression);
        booleanVariables.put(variableName, value);
    }

    private void handleVariableAssignment(String line) {   //doing arithmetic operations
        String[] parts;
        String variableName;
        String expression;

        if (line.contains("+=")) {
            parts = line.split("\\+=");
            variableName = parts[0].trim();
            expression = parts[1].trim();
            if (variables.containsKey(variableName)) {
                int value = evaluateExpression(expression);
                variables.put(variableName, variables.get(variableName) + value);
            }
        } else if (line.contains("-=")) {
            parts = line.split("-=");
            variableName = parts[0].trim();
            expression = parts[1].trim();
            if (variables.containsKey(variableName)) {
                int value = evaluateExpression(expression);
                variables.put(variableName, variables.get(variableName) - value);
            }
        } else if (line.contains("*=")) {
            parts = line.split("\\*=");
            variableName = parts[0].trim();
            expression = parts[1].trim();
            if (variables.containsKey(variableName)) {
                int value = evaluateExpression(expression);
                variables.put(variableName, variables.get(variableName) * value);
            }
        } else if (line.contains("/=")) {
            parts = line.split("/=");
            variableName = parts[0].trim();
            expression = parts[1].trim();
            if (variables.containsKey(variableName)) {
                int value = evaluateExpression(expression);
                if (value == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                variables.put(variableName, variables.get(variableName) / value);
            }
        } else {
            parts = line.split("=");
            variableName = parts[0].trim();
            expression = parts[1].trim();
            if (variables.containsKey(variableName)) {
                int value = evaluateExpression(expression);
                variables.put(variableName, value);
            } else if (booleanVariables.containsKey(variableName)) {
                boolean value = evaluateBooleanExpression(expression);
                booleanVariables.put(variableName, value);
            }
        }
    }
    private void handlePrint(String line) {
        int start = line.indexOf("(") + 1;
        int end = line.lastIndexOf(")");
        String expression = line.substring(start, end).trim();

        if (expression.startsWith("\"") && expression.endsWith("\"")) {

            String stringValue = expression.substring(1, expression.length() - 1);
            System.out.println(stringValue);
        } else if (booleanVariables.containsKey(expression)) {

            System.out.println(booleanVariables.get(expression));
        } else if (variables.containsKey(expression)) {

            System.out.println(variables.get(expression));
        } else {

            int value = evaluateExpression(expression);
            System.out.println(value); // Print the value
        }
    }
     private String extractCondition(String line) {
        int start = line.indexOf("(") + 1;
        int end = line.indexOf(")");
        return line.substring(start, end).trim();
    }
    private void handleIncrement(String line) {
        String variableName = line.replace("++", "").trim();
        if (variables.containsKey(variableName)) {
            variables.put(variableName, variables.get(variableName) + 1);
        }
    }

    private void handleDecrement(String line) {
        String variableName = line.replace("--", "").trim();
        if (variables.containsKey(variableName)) {
            variables.put(variableName, variables.get(variableName) - 1);
        }
    }

    private void handleBreak() {
        breakFlag = true; // setting the break flag to true
    }

    private boolean evaluateBooleanExpression(String expression) {

        if (expression.contains("||")) {
            String[] parts = expression.split("\\|\\|");
            for (String part : parts) {
                if (evaluateBooleanExpression(part.trim())) {
                    return true;
                }
            }
            return false;
        }

        if (expression.contains("&&")) {
            String[] parts = expression.split("&&");
            for (String part : parts) {
                if (!evaluateBooleanExpression(part.trim())) {
                    return false;
                }
            }
            return true;
        }

        if (expression.startsWith("!")) {
            return !evaluateBooleanExpression(expression.substring(1).trim());
        }

        if (expression.contains("==")) {
            String[] parts = expression.split("==");
            return evaluateExpression(parts[0].trim()) == evaluateExpression(parts[1].trim());
        }

        if (expression.contains("!=")) {
            String[] parts = expression.split("!=");
            return evaluateExpression(parts[0].trim()) != evaluateExpression(parts[1].trim());
        }

        if (expression.contains(">=")) {
            String[] parts = expression.split(">=");
            return evaluateExpression(parts[0].trim()) >= evaluateExpression(parts[1].trim());
        }

        if (expression.contains("<=")) {
            String[] parts = expression.split("<=");
            return evaluateExpression(parts[0].trim()) <= evaluateExpression(parts[1].trim());
        }

        if (expression.contains(">")) {
            String[] parts = expression.split(">");
            return evaluateExpression(parts[0].trim()) > evaluateExpression(parts[1].trim());
        }

        if (expression.contains("<")) {
            String[] parts = expression.split("<");
            return evaluateExpression(parts[0].trim()) < evaluateExpression(parts[1].trim());
        }

        return evaluateExpression(expression) != 0;
    }

    private int evaluateExpression(String expression) {
        expression = substituteVariables(expression); // replacing variables with values

        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c)) {
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                i--;
                values.push(num);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Remove '('
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.isEmpty() ? 0 : values.pop();
    }
    private String substituteVariables(String expression) {

        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            expression = expression.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        for (Map.Entry<String, Boolean> entry : booleanVariables.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue() ? "1" : "0");
        }
        return expression;
    }
