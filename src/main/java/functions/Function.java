package functions;

import operands.Operand;

import java.util.List;

/**
 * Superinterface for all arithmetic functions
 */
public abstract class Function<T> implements Operand<T> {
    /**
     * @return the function arguments
     */
    public abstract List<Object> getArgs();
}
