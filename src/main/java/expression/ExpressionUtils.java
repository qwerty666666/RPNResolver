package expression;

import functions.DoublePowExecutor;
import functions.Function;
import functions.FunctionExecutor;
import functions.Pow;
import operands.Operand;
import operators.*;
import operators.DoubleOperator.DoubleAddOperator;
import operators.DoubleOperator.DoubleDivideOperator;
import operators.DoubleOperator.DoubleMultiplyOperator;
import operators.DoubleOperator.DoubleSubtractOperator;

import java.util.HashMap;
import java.util.Map;


public class ExpressionUtils {

    /**
     * Check if the unit is operator
     */
    static boolean isTokenOperator(Object unit) {
        return (unit instanceof Class) && Operator.class.isAssignableFrom((Class)unit);
    }


    /**
     * Check if the unit is operator
     */
    static boolean isTokenOperand(Object unit) {
        return (unit instanceof Operand);
    }
}
