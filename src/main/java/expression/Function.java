package expression;

import java.util.List;

/**
 * <p>Superinterface for all arithmetic functions.</p>
 * <p>Functions doesn't responsible for giving the result of their applying to arguments. The result
 * is provided by FunctionExecutors.</p>
 * <p>The only purpose of Functions is to provide semantic interface for Expressions.</p>
 */
public abstract class Function<T> implements Operand<T> {
    /**
     * @return the function arguments
     */
    public abstract List<Object> getArgs();
}
