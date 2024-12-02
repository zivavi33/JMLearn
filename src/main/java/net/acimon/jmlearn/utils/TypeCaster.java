package net.acimon.jmlearn.utils;

import java.util.Arrays;
import java.util.List;

public class TypeCaster {


    private static final List<String> ALLOWED_TYPES = Arrays.asList(
        "String", "char", "int", "long", "double"
    );

    // Cast methods for specific types
    public String castToString(Object input) {
        return castToStringInternal(input);
    }

    public char castToChar(Object input) {
        return (char) castToCharInternal(input);
    }

    public int castToInt(Object input) {
        return castToIntInternal(input);
    }

    public long castToLong(Object input) {
        return castToLongInternal(input);
    }

    public double castToDouble(Object input) {
        return castToDoubleInternal(input);
    }

    // Internal helper methods
    private String castToStringInternal(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        String inputType = getInputType(input);

        if (!ALLOWED_TYPES.contains(inputType)) {
            throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }

        if (input instanceof String) {
            return (String) input;
        }
        return input.toString();
    }

    private char castToCharInternal(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        String inputType = getInputType(input);
        if (!ALLOWED_TYPES.contains(inputType)) {
            throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }

        if (input instanceof String && ((String) input).length() == 1) {
            return ((String) input).charAt(0);
        }
        throw new IllegalArgumentException("Cannot cast to char: " + input);
    }

    private int castToIntInternal(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        String inputType = getInputType(input);
        if (!ALLOWED_TYPES.contains(inputType)) {
            throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }
    
        if (input instanceof Number) {
            return ((Number) input).intValue();
        }
        if (input instanceof String) {
            String inputStr = (String) input;
            try {
                // Try parsing as an integer directly
                return Integer.parseInt(inputStr);
            } catch (NumberFormatException e) {
                // If it's not an integer, try parsing it as a double and truncate the decimal part
                try {
                    double value = Double.parseDouble(inputStr);
                    return (int) value; // Truncate decimal part by casting to int
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Cannot cast to int: " + inputStr, ex);
                }
            }
        }
        throw new IllegalArgumentException("Cannot cast to int: " + input);
    }

    private long castToLongInternal(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        String inputType = getInputType(input);
        if (!ALLOWED_TYPES.contains(inputType)) {
            throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }

        if (input instanceof Number) {
            return ((Number) input).longValue();
        }
        if (input instanceof String) {
            return Long.parseLong((String) input);
        }
        throw new IllegalArgumentException("Cannot cast to long: " + input);
    }

    private double castToDoubleInternal(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }
        String inputType = getInputType(input);
        if (!ALLOWED_TYPES.contains(inputType)) {
            throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }

        if (input instanceof Number) {
            return ((Number) input).doubleValue();
        }
        if (input instanceof String) {
            return Double.parseDouble((String) input);
        }
        throw new IllegalArgumentException("Cannot cast to double: " + input);
    }

    private String getInputType(Object input) {
        if (input instanceof String) return "String";
        if (input instanceof Character) return "char";
        if (input instanceof Integer) return "int";
        if (input instanceof Long) return "long";
        if (input instanceof Double) return "double";
        throw new IllegalArgumentException("Unsupported input type: " + input.getClass().getName());
    }
}
