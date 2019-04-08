package providers;

import functions.Function;
import functions.FunctionExecutor;

@FunctionalInterface
public interface FunctionExecutorProvider<T> {
    public FunctionExecutor<T, ? extends Function<T>> get(Class<? extends Function<T>> functionClass);
}
