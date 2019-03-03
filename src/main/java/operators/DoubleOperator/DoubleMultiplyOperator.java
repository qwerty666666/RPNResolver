package operators.DoubleOperator;

import operators.MultiplyOperator;

public class DoubleMultiplyOperator extends MultiplyOperator<Double> {
    @Override
    public Double apply(Double left, Double right) {
        return left * right;
    }
}
