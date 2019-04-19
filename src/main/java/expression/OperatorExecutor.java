package expression;


@FunctionalInterface
public interface OperatorExecutor<T> {
    /**
     * @return the result of applying operator to left and right operands
     */
    T exec(T left, T right);
}
