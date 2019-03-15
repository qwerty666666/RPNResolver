package functions;

@FunctionalInterface
public interface FunctionExecutor<T, S extends Function<T>> {
    public T exec(S function);
}
