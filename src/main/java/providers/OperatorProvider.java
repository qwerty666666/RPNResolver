package providers;

import operators.Operator;

@FunctionalInterface
public interface OperatorProvider<T> {
    public Operator<T> get(Class<? extends Operator<T>> operatorClass);
}
