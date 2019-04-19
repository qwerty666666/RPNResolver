package expression;

@FunctionalInterface
public interface FunctionExecutorProvider<T> {
    /**
     * Provide FunctionExecutor implementation for specified Function class
     */
    FunctionExecutor<T> get(Class<? extends Function<T>> functionClass);
}
