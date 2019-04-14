package operators;

public abstract class SubtractOperator<T> implements Operator<T> {
    @Override
    public int getPrecedence() {
        return 4;
    }

    @Override
    public OperatorAssociativity getAssociativity() {
        return OperatorAssociativity.LEFT_ASSOCIATIVE;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass();
    }
}