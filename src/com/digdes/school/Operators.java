package com.digdes.school;

import java.util.regex.Pattern;

public enum Operators {

    CLOSE_BRACKET(")", 3) {
        @Override
        boolean operation(Object val1, Object val2) {
            return false;
        }
    },
    OPEN_BRACKET("(", 3) {
        @Override
        boolean operation(Object val1, Object val2) {
            return false;
        }
    },
    AND("AND", 2) {
        @Override
        boolean operation(Object val1, Object val2) {
            if (val1 instanceof Boolean b1 && val2 instanceof Boolean b2) {
                return b1 && b2;
            }
            throw new IllegalArgumentException("Exception with operator AND");
        }
    },
    OR("OR", 1) {
        @Override
        boolean operation(Object val1, Object val2) {
            if (val1 instanceof Boolean b1 && val2 instanceof Boolean b2) {
                return b1 || b2;
            }
            throw new IllegalArgumentException("Exception with operator OR");
        }
    },
    EQUALS("=", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            return val1.equals(val2);
        }
    },
    NOT_EQUALS("!=", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            return !val1.equals(val2);
        }
    },
    LIKE("LIKE", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            if (val1 instanceof String str1 && val2 instanceof String str2) {
                return like(str1, str2);
            }
            throw new IllegalArgumentException("operator LIKE is working only with Strings");
        }

        private static boolean like(String str1, String str2) {
            return Pattern.compile(str2.replaceAll("%", ".*")).matcher(str1).matches();
        }
    },
    ILIKE("ILIKE", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            if (val1 instanceof String str1 && val2 instanceof String str2) {
                return iLike(str1, str2);
            }
            throw new IllegalArgumentException("operator ILIKE is working only with Strings");
        }

        private static boolean iLike(String str1, String str2) {
            return Pattern.compile(str2.replaceAll("%", ".*"), Pattern.CASE_INSENSITIVE).matcher(str1).matches();
        }
    },
    HIGHER_OR_EQUALS(">=", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            return (val1 instanceof Double d1 && ((val2 instanceof Double d2 && d1 >= d2) || (val2 instanceof Long l2 && d1 >= l2)) ||
                    (val1 instanceof Long l1 && ((val2 instanceof Double d2 && l1 >= d2) || (val2 instanceof Long l2 && l1 >= l2))));
        }
    },
    LESS_OR_EQUALS("<=", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            return (val1 instanceof Double d1 && ((val2 instanceof Double d2 && d1 <= d2) || (val2 instanceof Long l2 && d1 <= l2)) ||
                    (val1 instanceof Long l1 && ((val2 instanceof Double d2 && l1 <= d2) || (val2 instanceof Long l2 && l1 <= l2))));
        }
    },
    HIGHER(">", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            return (val1 instanceof Double d1 && ((val2 instanceof Double d2 && d1 > d2) || (val2 instanceof Long l2 && d1 > l2)) ||
                    (val1 instanceof Long l1 && ((val2 instanceof Double d2 && l1 > d2) || (val2 instanceof Long l2 && l1 > l2))));
        }
    },
    LESS("<", 0) {
        @Override
        boolean operation(Object val1, Object val2) {
            return (val1 instanceof Double d1 && ((val2 instanceof Double d2 && d1 < d2) || (val2 instanceof Long l2 && d1 < l2)) ||
                    (val1 instanceof Long l1 && ((val2 instanceof Double d2 && l1 < d2) || (val2 instanceof Long l2 && l1 < l2))));
        }
    };

    private final String operator;
    private final int type;

    Operators(String operator, int type) {
        this.operator = operator;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static Operators getByOperator(String str) {
        for (Operators i : Operators.values()) {
            if (i.getNameOperator().equals(str.toUpperCase())) {
                return i;
            }
        }
        throw new IllegalArgumentException("no constant");
    }

    public String getNameOperator() {
        return operator;
    }

    abstract boolean operation(Object val1, Object val2);

}
