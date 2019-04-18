package expression;

/**
 * Superinterface for all arithmetic operators
 */
public abstract class Operator<T> implements Token {
    /**
     * @return the operator associativity
     */
    public abstract OperatorAssociativity getAssociativity();
    /**
     * @return the operator precedence
     */
    public abstract int getPrecedence();


    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass();
    }
}
