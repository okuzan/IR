import java.util.*;

public class Search {
    private static Map<String, Map> map;

    public Search(HashMap hashMap) {
        map = hashMap;
    }

    public Set<Integer> processRequest(String request) {
        String[] parts = request.trim().split("\\s+");
        return fromRPN(toRPN(parts));
    }

    static String toRPN(String[] tokens) {
        StringBuilder expression = new StringBuilder();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {

            if (isOperand(token)) {
                if (token.equals(")")) {
                    while (!operators.peek().equals("(")) {
                        expression.append(operators.pop()).append(" ");
                    }
                    operators.pop();
                } else {
                    if (!operators.empty() && !operators.peek().equals("(") && !token.equals("(")) {
                        expression.append(operators.pop()).append(" ");
                    }
                    operators.push(token);
                }
            } else {
                expression.append(token).append(" ");
            }
        }

        while (!operators.empty()) {
            expression.append(operators.pop()).append(" ");
        }

        System.out.println(expression);
        return String.valueOf(expression);
    }

    private static Set<Integer> fromRPN(String expression) {
        String[] parts = expression.split("\\s+");
        Stack<Set<Integer>> operands = new Stack<>();
        for (String part : parts) {
            if (!isOperand(part)) {
                if (!map.containsKey(part)) return null;
                operands.push(map.get(part).keySet());
            } else {
                Set<Integer> b = operands.pop();
                Set<Integer> a = operands.pop();
                operands.push(operate(a, b, part));
            }
        }
        return operands.pop();
    }

    private static Set<Integer> operate(Set<Integer> setA, Set<Integer> setB,
                                        String operator) throws NullPointerException {

        if (operator.equals("&")) {
            setA.retainAll(setB);
        }
        if (operator.equals("!")) {
            setA.removeAll(setB);
        }
        if (operator.equals("|")) {
            Set<Integer> mySet = new HashSet<>();
            mySet.addAll(setA);
            mySet.addAll(setB);
            return mySet;
        }
        return setA;
    }

    private static boolean isOperand(String token) {
        return token.equals("!") || token.equals("&") || token.equals("|") || token.equals("(") || token.equals(")");
    }

    public static void main(String[] args) {
        String f = " she & ( ( mom ! dad ) & ( sister | me ) ! he )";
        toRPN(f.trim().split("\\s+"));
    }
}
