package operators;

import operators.OperatorAssociativity;

/**
 * Superinterface for all arithmetic operators
 */
public interface Operator<T> {
    /**
     * @return the operator associativity
     */
    public abstract OperatorAssociativity getAssociativity();
    /**
     * @return the operator precedence
     */
    public abstract int getPrecedence();


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
