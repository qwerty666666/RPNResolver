package operators;

public abstract class SubtractOperator<T> implements Operator<T> {
    @Override
    public OperatorAssociativity getAssociativity() {
        return OperatorAssociativity.LEFT_ASSOCIATIVE;
    }
}