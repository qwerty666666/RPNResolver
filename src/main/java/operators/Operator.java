package operators;

/**
 * Superinterface for all mathematical operators
 */
public interface Operator<T> {
    /**
     * @return the operator associativity
     */
    public abstract OperatorAssociativity getAssociativity();


    /**
     * Apply operator for two operands
     *
     * @param left  left operand
     * @param right right operand
     *
     * @return the result of applying operator
     */
    public abstract T apply(T left, T right);
}
