package com.digdes.school;
public class Token {
    private Object val1;
    private Object val2;
    private Operators operator;
    Token(Operators operator){
        this.operator = operator;
    }
    Token(Object val1, Operators operator, Object val2){
        this.val1 = val1;
        this.operator = operator;
        this.val2 = val2;
    }
    Token(){}
    public static Object getValueOrNullIfEqualsTypes(Object val1, Object val2){
        return ((val1 instanceof Boolean && val2 instanceof Boolean)||(val1 instanceof String && val2 instanceof String str2 && !str2.equalsIgnoreCase("null"))||
                ((val1 instanceof Long || val1 instanceof Double) && (val2 instanceof Long || val2 instanceof Double)))?val1:null;
    }

    public Object getVal1() {
        return val1;
    }

    public void setVal1(Object val1) {
        this.val1 = val1;
    }

    public Object getVal2() {
        return val2;
    }

    public void setVal2(Object val2) {
        this.val2 = val2;
    }

    public Operators getOperator() {
        return operator;
    }

    public void setOperator(Operators operator) {
        this.operator = operator;
    }
}

enum Type{
    SELECT("SELECT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    INSERT("INSERT"),
    WHERE("WHERE"),
    EQUALS("="),
    NOT_EQUALS("!="),
    LIKE("LIKE"),
    ILIKE("ILIKE"),
    HIGHER_OR_EQUALS(">="),
    LESS_OR_EQUALS("<="),
    HIGHER(">"),
    LESS("<"),
    AND("AND"),
    OR("OR");
    private String val;
    Type(String val){
        this.val = val;
    }



}
