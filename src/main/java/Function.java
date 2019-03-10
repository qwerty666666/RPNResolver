import operands.Operand;

import java.util.Arrays;
import java.util.List;

/**
 * Superinterface for all arithmetic functions
 */
public abstract class Function<T> implements Operand<T> {
    protected List<Operand<T>> args;


    @SafeVarargs
    public Function(Operand<T>... args) {
        this.args = Arrays.asList(args);
    }


    /**
     * @return the function arguments
     */
    public List<Operand<T>> getArgs() {
        return this.args;
    }


    /**
     * @return calculation result of applying the function to the given arguments
     */
    public abstract T calc(T... args);
}
