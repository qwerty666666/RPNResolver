package expression;

@FunctionalInterface
public interface FunctionExecutor<T> {
    /**
     * @param args
     * @return the result of applying function to arguments args
     */
    T exec(Object... args);
}
