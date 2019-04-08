package providers;

import operators.*;
import operators.DoubleOperator.DoubleAddOperator;
import operators.DoubleOperator.DoubleDivideOperator;
import operators.DoubleOperator.DoubleMultiplyOperator;
import operators.DoubleOperator.DoubleSubtractOperator;
import java.util.HashMap;
import java.util.Map;


public class DoubleOperatorProvider implements OperatorProvider<Double> {
    static Map<Class<? extends Operator>, Operator<Double>> DOUBLE_OPERATORS_MAP = new HashMap<>();
    static {
        DOUBLE_OPERATORS_MAP.put(AddOperator.class, new DoubleAddOperator());
        DOUBLE_OPERATORS_MAP.put(SubtractOperator.class, new DoubleSubtractOperator());
        DOUBLE_OPERATORS_MAP.put(MultiplyOperator.class, new DoubleMultiplyOperator());
        DOUBLE_OPERATORS_MAP.put(DivideOperator.class, new DoubleDivideOperator());
    }

    @Override
    public Operator<Double> get(Class<? extends Operator<Double>> operatorClass) {
        if (DOUBLE_OPERATORS_MAP.containsKey(operatorClass)) {
            return DOUBLE_OPERATORS_MAP.get(operatorClass);
        }
        throw new IllegalArgumentException("Operator implementation for " + operatorClass + " doesn't provided");
    }
}
