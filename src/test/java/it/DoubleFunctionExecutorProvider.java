package it;

import expression.Function;
import expression.FunctionExecutor;
import expression.FunctionExecutorProvider;

import java.util.HashMap;
import java.util.Map;


public class DoubleFunctionExecutorProvider implements FunctionExecutorProvider<Double> {
    public static Map<Class<? extends Function>, FunctionExecutor<Double>> DOUBLE_FUNCTIONS_MAP = new HashMap<>();
    static {
        DOUBLE_FUNCTIONS_MAP.put(Pow.class, (Object... args) -> Math.pow((Double)args[0], (Double)args[1]));
    }

    @Override
    public FunctionExecutor<Double> get(Class<? extends Function<Double>> functionClass) {
        if (DOUBLE_FUNCTIONS_MAP.containsKey(functionClass)) {
            return DOUBLE_FUNCTIONS_MAP.get(functionClass);
        }
        throw new IllegalArgumentException("FunctionExecutor implementation for " + functionClass + " doesn't provided");
    }
}
