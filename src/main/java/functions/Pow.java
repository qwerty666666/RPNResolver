package functions;


import operands.Operand;

import java.util.Arrays;

public class Pow<T> extends Function<T> {
    public Pow(Operand<T> arg, Double pow) {
        this.args = Arrays.asList(arg, );
    }
}
