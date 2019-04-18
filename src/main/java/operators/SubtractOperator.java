package operators;

import expression.Operator;
import expression.OperatorAssociativity;

public class SubtractOperator<T> extends Operator<T> {
    @Override
    public int getPrecedence() {
        return 4;
    }

    @Override
    public OperatorAssociativity getAssociativity() {
        return OperatorAssociativity.LEFT_ASSOCIATIVE;
    }
}