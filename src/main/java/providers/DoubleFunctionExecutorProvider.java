package providers;

import functions.Function;
import functions.FunctionExecutor;
import functions.Pow;
import operators.*;
import operators.DoubleOperator.DoubleAddOperator;
import operators.DoubleOperator.DoubleDivideOperator;
import operators.DoubleOperator.DoubleMultiplyOperator;
import operators.DoubleOperator.DoubleSubtractOperator;

import java.util.HashMap;
import java.util.Map;


public class DoubleFunctionExecutorProvider implements FunctionExecutorProvider<Double> {
    public static Map<Class<? extends Function>, FunctionExecutor<Double, ? extends Function<Double>>> DOUBLE_FUNCTIONS_MAP = new HashMap<>();
    static {
        DOUBLE_FUNCTIONS_MAP.put(Pow.class, (Pow<Double> function) -> Math.pow(function.getOperand(), function.getPow()));
    }

    @Override
    public FunctionExecutor<Double, ? extends Function<Double>> get(Class<? extends Function<Double>> functionClass) {
        if (DOUBLE_FUNCTIONS_MAP.containsKey(functionClass)) {
            return DOUBLE_FUNCTIONS_MAP.get(functionClass);
        }
        throw new IllegalArgumentException("FunctionExecutor implementation for " + functionClass + " doesn't provided");
    }
}
