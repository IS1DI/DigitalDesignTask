package com.digdes.school;
public class Token {
    private Object val1;
    private Object val2;
    private Operators operator;
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

