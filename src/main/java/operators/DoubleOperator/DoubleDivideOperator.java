package operators.DoubleOperator;

import operators.DivideOperator;

public class DoubleDivideOperator extends DivideOperator<Double> {
    @Override
    public Double apply(Double left, Double right) {
        return left / right;
    }
}
