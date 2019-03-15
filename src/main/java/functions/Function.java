package functions;

import operands.Operand;

import java.util.Arrays;
import java.util.List;

/**
 * Superinterface for all arithmetic functions
 */
public class Function<T> implements Operand<T> {
    protected List<Operand<T>> args;


    @SafeVarargs
    protected Function(Operand<T>... args) {
        this.args = Arrays.asList(args);
    }


    /**
     * @return the function arguments
     */
    public List<Operand<T>> getArgs() {
        return this.args;
    }
}
