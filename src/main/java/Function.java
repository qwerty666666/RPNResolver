import java.util.Arrays;
import java.util.List;

/**
 * Superinterface for all mathematical functions
 */
public abstract class Function<T> implements Operand<T> {
    protected List<Object> args;


    @SafeVarargs
    public Function(Operand<T>... args) {
        this.args = Arrays.asList(args);
    }


    /**
     * @return the function arguments
     */
    public List<Object> getArgs() {
        return this.args;
    }


    /**
     * @return calculation result of applying the function to the given arguments
     */
    public abstract T calc(T... args);
}
