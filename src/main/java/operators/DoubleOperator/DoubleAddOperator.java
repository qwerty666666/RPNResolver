package operators.DoubleOperator;

import operators.AddOperator;

public class DoubleAddOperator extends AddOperator<Double> {
    @Override
    public Double apply(Double left, Double right) {
        return left + right;
    }
}
