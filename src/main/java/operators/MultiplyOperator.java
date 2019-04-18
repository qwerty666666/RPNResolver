package operators;

import expression.Operator;
import expression.OperatorAssociativity;

public class MultiplyOperator<T> extends Operator<T> {
    @Override
    public int getPrecedence() {
        return 3;
    }

    @Override
    public OperatorAssociativity getAssociativity() {
        return OperatorAssociativity.LEFT_ASSOCIATIVE;
    }
}