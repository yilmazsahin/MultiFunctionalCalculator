package EndOfTermAssignment.ProfessionalCalculatorNew;

import java.util.*;

public class AdvancedCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String resetColor = "\u001B[0m";
        String blue = "\u001B[34m";
        String purple = "\u001B[35m";
        String red = "\u001B[31m";
        String green = "\u001B[32m";

        System.out.println("******" + purple + " Welcome to the advanced calculator. " + resetColor + "********");
        //  System.out.println("Addition: + | Subtraction: - | Multiplication: *| Division: / ");
        System.out.println(blue + "Addition: " + green + "+" + resetColor + " | " + blue + " Subtraction: " + green + "-" + resetColor +
                " | " + blue + " Multiplication: " + green + "*" + resetColor + " | " + blue + " Division: " + green + "/" + resetColor);
        System.out.println(blue + "Sin : " + green + " sin(x)" + resetColor + " | " + blue + "Cos : " + green + "cos(x)" + resetColor + " | " +
                blue + "Tan : " + green + " tan(x)" + resetColor + " | " + blue + "Cot : " + green + "cot(x)" + resetColor);
        // System.out.println("Square Root Extraction: sqrt(x) | Exponentiation : pow(x,y)|Sin: sin(x)|Cos: cos(x)|Tan: tan(x)|Cot: cot(x)");
        System.out.println("Write " + red + "Exit" + resetColor + " to close the calculator.");

        while (true) {
            System.out.print( "Please enter the operation you want to calculate: " );
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Calculator is being closed.. ");
                break;
            }

            try {
                double result = evaluateExpression(input);
                System.out.println(purple + "Result: " + green + result + resetColor);
            } catch (IllegalArgumentException e) {
                System.out.println(red + "Error: " + e.getMessage() + resetColor);
            }
        }
    }

    public static double evaluateExpression(String input) {
        input = input.replaceAll(" ", "");
        return evaluate(input);
    }

    public static double evaluate(String expression) {
        return new Object() {
            int position = -1, ch;

            void nextChar() {
                ch = (++position < expression.length()) ? expression.charAt(position) : -1;
            }

            boolean eat(int catchTheCaracter) {
                while (ch == ' ') nextChar();
                if (ch == catchTheCaracter) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (position < expression.length()) {
                    throw new IllegalArgumentException("Unexpected Character: " + (char) ch);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) {
                    return -parseFactor();
                }

                double x;
                int startPos = this.position;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.position));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String function = expression.substring(startPos, this.position);
                    if (function.equals("pow")) {
                        eat('(');
                        double base = parseExpression();
                        eat(',');
                        double exponent = parseExpression();
                        eat(')');
                        x = Math.pow(base, exponent);
                    } else {
                        x = parseFactor();
                        switch (function) {
                            case "sqrt" -> x = Math.sqrt(x);
                            case "sin" -> x = Math.sin(Math.toRadians(x));
                            case "cos" -> x = Math.cos(Math.toRadians(x));
                            case "tan" -> {
                                if (x == 90 || x == 270) {
                                    throw new IllegalArgumentException("tan(" + x + ") tanımsızdır");
                                }
                                x = Math.tan(Math.toRadians(x));
                            }
                            case "cot" -> {
                                if (x % 180 == 0) {
                                    throw new IllegalArgumentException("cot(" + x + ") tanımsızdır");
                                }
                                x = 1.0 / Math.tan(Math.toRadians(x));
                            }
                            default -> throw new IllegalArgumentException("Invalid Function: " + function);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Invalid Character: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());
                return x;
            }
        }.parse();
    }
}

