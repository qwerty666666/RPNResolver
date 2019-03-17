package functions;


import operands.Operand;

import java.util.Arrays;
import java.util.List;


/**
 * Math power function
 * @param <T>
 */
public class Pow<T> extends Function<T> {
    Operand<T> operand;
    T operandVal = null;
    double pow;

    public Pow(Operand<T> operand, double pow) {
        this.operand = operand;
        this.pow = pow;
    }

    public void setArgs(T operand, double pow) {
        this.operandVal = operand;
        this.pow = pow;
    }

    @Override
    public List<Object> getArgs() {
        return Arrays.asList(operand, pow);
    }

    public T getOperand() {
        return this.operandVal;
    }

    public double getPow() {
        return this.pow;
    }
}
