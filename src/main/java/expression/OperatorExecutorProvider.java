package expression;


@FunctionalInterface
public interface OperatorExecutorProvider<T> {
    /**
     * Provide OperatorExecutor implementation for specified Operator class
     */
    OperatorExecutor<T> get(Class<? extends Operator<T>> operatorClass);
}
