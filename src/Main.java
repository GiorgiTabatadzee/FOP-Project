import java.util.*;
import java.util.regex.*;

public class Main {
    public static void main(String[] args) {
public static void main(String[] args) {
        private final Map<String, Integer> variables = new HashMap<>();
        private final Map<String, Boolean> booleanVariables = new HashMap<>();
        private final Stack<Boolean> executionStack = new Stack<>();
        private boolean breakFlag = false;

        public void interpret(String code) {
            String[] lines = code.split("\n");
            int lineIndex = 0;

            while (lineIndex < lines.length) {
                String line = lines[lineIndex].trim();

                if (line.startsWith("if")) {

                    String condition = extractCondition(line);
                    boolean conditionResult = evaluateBooleanExpression(condition);

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
                } else if (line.startsWith("while")) {

                    String condition = extractCondition(line);
                    int startOfLoop = lineIndex + 1;
                    int endOfLoop = findEndOfBlock(lines, lineIndex);


                    while (evaluateBooleanExpression(condition)) {
                        executeBlock(lines, startOfLoop, endOfLoop);

                        if (breakFlag) {
                            breakFlag = false; // Reset the break flag
                            break;
                        }


                        condition = extractCondition(line);
                    }

                    lineIndex = endOfLoop;
                } else {
                    interpretLine(line);
                    lineIndex++;
                }
            }
        }

        private int skipBlock(String[] lines, int lineIndex) {

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

        private void executeBlock(String[] lines, int start, int end) {
            // Execute lines within a block
            for (int i = start; i < end; i++) {
                interpretLine(lines[i].trim());
                if (breakFlag) {
                    breakFlag = false;
                    break;
                }
            }
        }

        private void interpretLine(String line) {

            if (line.startsWith("var ")) {
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
            line = line.replace("var ", "").trim();
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

        private void handleVariableAssignment(String line) {
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
            breakFlag = true; // Set the break flag to true
        }
    }
    }
}
