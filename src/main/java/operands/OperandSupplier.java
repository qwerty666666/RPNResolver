package operands;

import jdk.nashorn.internal.objects.annotations.Function;

import java.util.function.Supplier;


/**
 * Class for supplying plain operands to expressionBuilder.
 * E.g. in ExpressionBuilder<Integer> operand "3" will be converted to OperandSupplier<Integer>(3)
 *
 * @param <T>
 */
public class OperandSupplier<T> implements Operand<T>, Supplier<T> {
    protected T val;

    public OperandSupplier(T val) {
        this.val = val;
    }

    @Override
    public T get() {
        return val;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OperandSupplier) {
            return val.equals(((OperandSupplier)obj).get());
        }
        return false;
    }
}
