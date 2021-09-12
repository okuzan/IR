import java.util.*;

public class BoolSearch {
//        private TreeMap<String, Map<Integer, Integer>> map;
    private static HashMap<String, Map<Integer, ArrayList<Integer>>> coordMap;
    private static HashMap<String, Map<Integer, ArrayList<Integer>>> biwordMap;

    BoolSearch(HashMap<String, Map<Integer, ArrayList<Integer>>> map1, HashMap<String, Map<Integer, ArrayList<Integer>>> map2) {
        coordMap = map1;
        biwordMap = map2;
    }

    public Set<Integer> processBoolRequest(String request) {
        String[] parts = request.trim().split("\\s+");
        return fromRPN(toRPN(parts));
    }

    static String toRPN(String[] tokens) {
        StringBuilder expression = new StringBuilder();
        Stack<String> operators = new Stack<String>();
        try {
            for (String token : tokens) {

                if (isOperator(token)) {
                    if (token.equals(")")) {
                        while (!operators.peek().equals("(")) {
                            expression.append(operators.pop()).append(" ");
                        }
                        operators.pop();
                    } else {
                        if (!operators.empty() && !operators.peek().equals("(") && !token.equals("(")) {
//                            && !(token.equals("||") && !operators.peek().equals("||")))

                            expression.append(operators.pop() + " ");
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
        } catch (InputMismatchException e) {
            System.out.println("Not proper query tried to proceed!");
        }
        return String.valueOf(expression);
    }


    private Set<Integer> fromRPN(String expression) {
        String[] parts = expression.split("\\s+");
        Stack<Set<Integer>> operands = new Stack<Set<Integer>>();
        for (String part : parts) {
            if (!isOperator(part)) {
                if (part.contains("/")) {
                    String[] pieces = part.split("/");
                    operands.push(proximity(pieces[0], pieces[2], Integer.parseInt(pieces[1])));
                } else {
                    if (!coordMap.containsKey(part)) try {
                        throw new Exception();
                    } catch (Exception e) {
                        System.out.println("There is no such word! " + part);
                    }
                    operands.push(coordMap.get(part).keySet());
                }
            } else {
                Set<Integer> b = operands.pop();
                Set<Integer> a = operands.pop();
                operands.push(operate(a, b, part));
            }
        }
        return operands.pop();
    }

    //using biword indexing
    public Set<Integer> phrasalSearchBiword(String query) {
        Set<Integer> result = null;
        try {
            String[] parts = query.trim().toLowerCase().split("\\s+");
            Stack<Set<Integer>> biwords = new Stack<Set<Integer>>();
            for (int i = 0; i < parts.length - 1; i++) {
                String biword = parts[i] + " " + parts[i + 1];
                biwords.add(biwordMap.get(biword).keySet());
            }
            result = biwords.pop();
            while (!biwords.isEmpty()) {
                result = operate(result, biwords.pop(), "&");
            }
        } catch (InputMismatchException e) {
            System.out.println("Wrong input!");
            e.printStackTrace();
        }
        return result;
    }

    //using coordinate indexing (beta)
    public Set<Integer> phrasalSearchCoord(String query) {
        String[] parts = query.trim().toLowerCase().split("\\s+");
        Set<Integer> docs = coordMap.get(parts[0]).keySet();
        Set<Integer> finalSet = new HashSet<Integer>();
        ArrayList<Integer> result;

        for (int i = 0; i < parts.length - 1; i++) {
            docs = operate(docs, coordMap.get(parts[i + 1]).keySet(), "&");
            if (docs.isEmpty()) return null;
        }

        for (int i : docs) {
            ArrayList[] coords = new ArrayList[parts.length];
            coords[0] = coordMap.get(parts[0]).get(i);

            //equalizing coords of consecutive words
            for (int j = 1; j < parts.length; j++) {
                ArrayList<Integer> coord = coordMap.get(parts[j]).get(i);
                for (int k = 0; k < coord.size(); k++)
                    coord.set(k, coord.get(k) - j);
                coords[j] = coord;
            }
            result = coords[0];

            //finding same coords
            for (int j = 1; j < coords.length; j++) {
                result.retainAll(coords[j]);
                if (result.isEmpty()) break;
            }

            if (!result.isEmpty()) finalSet.add(i);
        }
        return finalSet.isEmpty() ? null : finalSet;
    }

    private Set<Integer> operate(Set setA, Set setB, String operator) throws NullPointerException {

        if (operator.equals("&")) {
            setA.retainAll(setB);
        }
        if (operator.equals("!")) {
            setA.removeAll(setB);
        }
        if (operator.equals("||")) {
            Set<Integer> mySet = new HashSet<Integer>();
            mySet.addAll(setA);
            mySet.addAll(setB);
            return mySet;
        }
        return setA;
    }

    //util function for range search
    Set<Integer> proximity(String word1, String word2, int dist) {

        Map<Integer, ArrayList<Integer>> map1 = coordMap.get(word1);
        Map<Integer, ArrayList<Integer>> map2 = coordMap.get(word2);

        Set<Integer> common = operate(map1.keySet(), map2.keySet(), "&");
        Set<Integer> set = new HashSet<Integer>();
        ArrayList<Integer> al1, al2;
        for (int i : common) {
            al1 = map1.get(i);
            al2 = map2.get(i);
            if (checkAL(al1, al2, dist)) set.add(i);
        }
        return set;
    }

    //find common usages by
    private boolean checkAL(ArrayList<Integer> al1, ArrayList<Integer> al2, int dist) {
        for (int i = -dist; i < dist; i++) {
            if (i == 0) continue;
            ArrayList<Integer> alc1 = new ArrayList<Integer>(al1);
            ArrayList<Integer> alc2 = new ArrayList<Integer>(al2);
            for (int k = 0; k < alc2.size(); k++)
                alc2.set(k, al2.get(k) + i);
            alc1.retainAll(alc2);
            if (!alc1.isEmpty()) return true;
        }
        return false;
    }

    private static boolean isOperator(String token) {
        return token.equals("!")
                || token.equals("&")
                || token.equals("||")
                || token.equals("(")
                || token.equals(")");
    }

    public static void main(String[] args) {
        //all characters must be separate, except proximity slach "me\2\you", where is 2 is range
        String f = " she & ( ( mom ! dad ) & ( sister || me ) ! he )";
        toRPN(f.trim().split("\\s+"));
    }
}