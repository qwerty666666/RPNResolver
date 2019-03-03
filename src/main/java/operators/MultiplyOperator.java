package operators;

public abstract class MultiplyOperator<T> implements Operator<T> {
    @Override
    public OperatorAssociativity getAssociativity() {
        return OperatorAssociativity.LEFT_ASSOCIATIVE;
    }
}