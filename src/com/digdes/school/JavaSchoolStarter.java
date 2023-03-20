package com.digdes.school;

import java.util.*;
import java.util.regex.Pattern;

public class JavaSchoolStarter {
    private List<Map<String, Object>> table;

    private static final Pattern patternBoolean = Pattern.compile("^(true|false)$");
    private static final Pattern patternAllObj = Pattern.compile("(=|!=|>=|<=|>|<|\\b(SELECT|UPDATE|INSERT|DELETE|VALUES|WHERE|LIKE|ILIKE|AND|OR|true|false)\\b|\\d+\\.?\\d*|'[\\wА-я]+'|'%?[\\wА-я]+%?')", Pattern.CASE_INSENSITIVE);


    private Integer index;

    private List<Object> listOfObjects;

    JavaSchoolStarter() {
        table = new ArrayList<>();
    }


    public List<Map<String, Object>> execute(String query) {
        listOfObjects = patternAllObj.matcher(query).results().map(x -> convertToObj(x.group())).toList();
        index = 0;
        List<Map<String, Object>> result = null;
        while (index < listOfObjects.size() && listOfObjects.get(index) instanceof String str && (
                str.equalsIgnoreCase("SELECT") ||
                        str.equalsIgnoreCase("UPDATE") ||
                        str.equalsIgnoreCase("INSERT") ||
                        str.equalsIgnoreCase("DELETE") ||
                        str.equalsIgnoreCase("WHERE") ||
                        str.equalsIgnoreCase("VALUES"))) {
            try {
                index++;
                return calcResult(Method.getByType(str));
            } catch (Exception e) {
                throw new IllegalArgumentException("method " + str.toUpperCase() + " is not supported");
            }
        }
        return null;
    }

    enum Method {
        DELETE("DELETE"),
        INSERT("INSERT"),
        SELECT("SELECT"),
        UPDATE("UPDATE");

        Method(String type) {
            this.type = type;
        }

        private final String type;

        public String getType() {
            return type;
        }

        public static Method getByType(String str) {
            for (Method i : Method.values()) {
                if (i.getType().equalsIgnoreCase(str)) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Method " + str + " not supported");
        }
    }

    private Map<String, List<Token>> getMapOfValuesWhere() {
        Map<String, List<Token>> mapOf = new HashMap<>();
        while (index < listOfObjects.size()) {
            if (listOfObjects.get(index) instanceof String str) {
                if (str.equalsIgnoreCase("VALUES") || str.equalsIgnoreCase("WHERE")) {
                    index++;
                    mapOf.put(str.toUpperCase(), parse(str.toUpperCase()));
                }
            }
        }
        return mapOf;
    }

    private List<Map<String, Object>> calcResult(Method method) {
        Map<String, List<Token>> mapOfValuesWhere = getMapOfValuesWhere();
        List<Token> listWhere = new ArrayList<>();
        List<Token> listValues = new ArrayList<>();
        List<Map<String, Object>> outputTable = new ArrayList<>();
        switch (method) {
            case SELECT -> {
                if (mapOfValuesWhere.containsKey("WHERE")) {
                    listWhere = mapOfValuesWhere.get("WHERE");
                    for (Map<String, Object> stringObjectMap : table) {
                        if (calcWhere(stringObjectMap, listWhere)) {
                            outputTable.add(stringObjectMap);
                        }
                    }
                    return outputTable;
                } else {
                    return table;
                }
            }
            case DELETE -> {
                if (mapOfValuesWhere.containsKey("WHERE")) {
                    listWhere = mapOfValuesWhere.get("WHERE");
                } else {
                    List<Map<String, Object>> list;
                    list = table;
                    table = new ArrayList<>();
                    return list;
                }
                Iterator<Map<String, Object>> iterator = table.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> row = iterator.next();
                    if (calcWhere(row, listWhere)) {
                        iterator.remove();
                    }
                }
                return table;
            }
            case UPDATE -> {
                if (mapOfValuesWhere.containsKey("WHERE")) {
                    listWhere = mapOfValuesWhere.get("WHERE");
                }
                if (mapOfValuesWhere.containsKey("VALUES")) {
                    listValues = mapOfValuesWhere.get("VALUES");
                }
                for (Map<String, Object> row : table) {
                    if (!listWhere.isEmpty()) {
                        if (calcWhere(row, listWhere)) {
                            if (calcValue(row, listValues))
                                outputTable.add(row);
                        }
                    } else {
                        if (calcValue(row, listValues))
                            outputTable.add(row);
                    }
                }
                return outputTable;
            }
            case INSERT -> {
                if (mapOfValuesWhere.containsKey("VALUES")) {
                    listValues = mapOfValuesWhere.get("VALUES");
                    Map<String, Object> row = new HashMap<>();
                    if (calcValue(row, listValues)) {
                        table.add(row);
                    }
                    outputTable.add(row);
                    return outputTable;
                } else {
                    throw new NoSuchElementException("operator VALUES is not exist in query");
                }
            }
            default -> throw new IllegalArgumentException("operator is not supported");
        }

    }

    private boolean calcValue(Map<String, Object> row, List<Token> value) {
        for (Token token : value) {
            if (token.getVal1() instanceof String str) {
                if (token.getOperator().equals(OperatorsWhere.EQUALS)) {

                    if (row.containsKey(str)) {
                        Object t = Token.getValueOrNullIfEqualsTypes(row.get(str), token.getVal2());
                        if (t == null) {
                            if (row.get(str) instanceof String && token.getVal2() instanceof String) {
                                row.put(str, null);
                            } else {
                                throw new IllegalArgumentException("types are not equals");
                            }
                        } else {
                            row.put(str, token.getVal2());
                        }
                    } else {
                        row.put(str, token.getVal2());
                    }
                } else {
                    throw new IllegalArgumentException("operator is not supported");
                }
            } else {
                throw new IllegalArgumentException("name of column must be String");
            }
        }
        return true;
    }

    private boolean calcWhere(Map<String, Object> row, List<Token> where) {
        Stack<Boolean> stack = new Stack<>();
        for (Token token : where) {
            if (token.getVal1() == null) {
                if (token.getOperator() != null) {
                    stack.push(token.getOperator().operation(stack.pop(), stack.pop()));
                }
            } else if (token.getVal1() instanceof String str) {
                if (row.containsKey(str)) {
                    stack.push(token.getOperator().operation(row.get(str), token.getVal2()));
                } else {
                    //throw new Exception("column is not exist")
                    return false;
                }
            } else {
                stack.push(token.getOperator().operation(token.getVal1(), token.getVal2()));
            }
        }
        if (stack.size() != 1) {
            throw new NoSuchElementException("no such operator");
        } else {
            return stack.pop();
        }
    }

    private List<Token> parse(String type) {
        switch (type) {
            case "VALUES" -> {
                return parseValues();
            }
            case "WHERE" -> {
                List<Token> listOfToken = parseWhere();
                Stack<Token> stack = new Stack<>();
                List<Token> result = new ArrayList<>();
                for (int i = 0; i < listOfToken.size(); i++) {
                    if (listOfToken.get(i).getOperator().getType() == 0) {
                        result.add(listOfToken.get(i));
                    } else if (listOfToken.get(i).getOperator().getType() < 3) {
                        if (stack.peek().getOperator().getType() <= listOfToken.get(i).getOperator().getType()) {
                            stack.push(listOfToken.get(i));
                        } else {
                            do {
                                result.add(stack.pop());
                            } while (!stack.isEmpty());
                            stack.push(listOfToken.get(i));
                        }
                    } else {
                        i = parseBrackets(result, listOfToken, ++i);
                    }
                }
                while (!stack.isEmpty()) {
                    result.add(stack.pop());
                }
                return result;
            }
            default -> throw new IllegalArgumentException("operator is not supported");
        }
    }

    private int parseBrackets(List<Token> result, List<Token> listOfToken, int startIndex) {
        Stack<Token> stack = new Stack<>();
        List<Token> currentResult = new ArrayList<>();
        int i;
        for (i = startIndex; i < listOfToken.size(); i++) {
            if (listOfToken.get(i).getOperator().getType() == 0) {
                currentResult.add(listOfToken.get(i));
            } else if (listOfToken.get(i).getOperator().getType() < 3) {
                if (stack.peek().getOperator().getType() <= listOfToken.get(i).getOperator().getType()) {
                    stack.push(listOfToken.get(i));
                } else {
                    do {
                        result.add(stack.pop());
                    } while (!stack.isEmpty());
                }
            } else {
                i = parseBrackets(currentResult, listOfToken, i);
            }
        }
        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("not enough close brackets");
        } else {
            return index = i;
        }
    }

    private List<Token> parseValues() {
        List<Token> allTokens = new LinkedList<>();
        Token curToken = new Token();
        while (index < listOfObjects.size() && !(listOfObjects.get(index) instanceof String str && str.equalsIgnoreCase("WHERE"))) {
            if (listOfObjects.get(index) instanceof String str && str.equalsIgnoreCase("VALUES")) {
                throw new IllegalArgumentException("too many VALUES");
            } else if (listOfObjects.get(index) instanceof OperatorsWhere ow) {
                if (curToken.getVal1() != null) {
                    if (curToken.getOperator() != null) {
                        throw new IllegalArgumentException("illegal format");
                    } else {
                        curToken.setOperator(ow);
                    }
                } else {
                    throw new IllegalArgumentException("illegal format");
                }
            } else {
                if (curToken.getVal1() == null) {
                    curToken.setVal1(listOfObjects.get(index));
                } else {
                    curToken.setVal2(listOfObjects.get(index));
                    allTokens.add(curToken);
                    curToken = new Token();
                }
            }
            index++;
        }
        return allTokens;
    }

    private List<Token> parseWhere() {
        List<Token> allTokens = new ArrayList<>();
        Token curToken = new Token();
        while (index < listOfObjects.size() && !(listOfObjects.get(index) instanceof String str && (str.equalsIgnoreCase("VALUES") || str.equalsIgnoreCase("WHERE")))) {
            if (listOfObjects.get(index) instanceof OperatorsWhere ow) {
                if (curToken.getVal1() == null) {
                    if (ow.getType() == 0) {
                        throw new IllegalArgumentException("two operators");
                    } else {
                        curToken.setOperator(ow);
                        allTokens.add(curToken);
                        curToken = new Token();
                    }
                } else {
                    curToken.setOperator(ow);
                }
            } else {
                if (curToken.getVal1() == null) {
                    curToken.setVal1(listOfObjects.get(index));
                } else {
                    curToken.setVal2(listOfObjects.get(index));
                    allTokens.add(curToken);
                    curToken = new Token();
                }
            }
            index++;

        }
        return allTokens;
    }

    private static Object convertToObj(String obj) {
        if (patternBoolean.matcher(obj).matches()) {
            return Boolean.valueOf(obj);
        } else {
            try {
                return Long.valueOf(obj);
            } catch (Exception ex) {
                try {
                    return Double.valueOf(obj);
                } catch (Exception exx) {
                    try {
                        return OperatorsWhere.getByOperator(obj.toUpperCase());
                    } catch (Exception exxx) {
                        return obj;
                    }
                }
            }
        }
    }

}
