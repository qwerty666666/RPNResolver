package operators.DoubleOperator;

import operators.SubtractOperator;

public class DoubleSubtractOperator extends SubtractOperator<Double> {
    @Override
    public Double apply(Double left, Double right) {
        return left - right;
    }
}
