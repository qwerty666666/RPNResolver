package operators;

public abstract class DivideOperator<T> implements Operator<T> {
    @Override
    public OperatorAssociativity getAssociativity() {
        return OperatorAssociativity.LEFT_ASSOCIATIVE;
    }
}