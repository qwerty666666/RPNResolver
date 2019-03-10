package operators;

public abstract class AddOperator<T> implements Operator<T> {
    @Override
    public int getPrecedence() {
        return 4;
    }

    @Override
    public OperatorAssociativity getAssociativity() {
        return OperatorAssociativity.LEFT_ASSOCIATIVE;
    }
}