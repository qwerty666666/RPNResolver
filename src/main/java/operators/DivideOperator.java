package operators;

public abstract class DivideOperator<T> implements Operator<T> {
    @Override
    public int getPrecedence() {
        return 3;
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