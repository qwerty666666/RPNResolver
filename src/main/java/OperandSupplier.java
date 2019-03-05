import java.util.function.Supplier;

public class OperandSupplier<T> implements Operand<T>, Supplier<T> {
    protected T val;

    public OperandSupplier(T val) {
        this.val = val;
    }

    @Override
    public T get() {
        return val;
    }
}
