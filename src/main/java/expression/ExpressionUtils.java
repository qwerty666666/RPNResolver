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


    public static HashMap<Class<? extends Operator>, Operator<Double>> DOUBLE_OPERATORS_MAP = new HashMap<>();
    static {
        DOUBLE_OPERATORS_MAP.put(AddOperator.class, new DoubleAddOperator());
        DOUBLE_OPERATORS_MAP.put(SubtractOperator.class, new DoubleSubtractOperator());
        DOUBLE_OPERATORS_MAP.put(MultiplyOperator.class, new DoubleMultiplyOperator());
        DOUBLE_OPERATORS_MAP.put(DivideOperator.class, new DoubleDivideOperator());
    }


    public static HashMap<Class<? extends Function>, FunctionExecutor<Double, ? extends Function<Double>>> DOUBLE_FUNCTIONS_MAP = new HashMap<>();
    static {
        DOUBLE_FUNCTIONS_MAP.put(Pow.class, new DoublePowExecutor());
    }
}
