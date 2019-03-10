package expression;

import operands.Operand;
import operators.Operator;


public class ExpressionUtils {

    /**
     * Check if the unit is operator
     */
    public static boolean isTokenOperator(Object unit) {
        return (unit instanceof Class) && Operator.class.isAssignableFrom((Class)unit);
    }


    /**
     * Check if the unit is operator
     */
    public static boolean isTokenOperand(Object unit) {
        return (unit instanceof Operand);
    }
}
