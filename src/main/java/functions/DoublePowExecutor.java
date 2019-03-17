package functions;

public class DoublePowExecutor implements FunctionExecutor<Double, Pow<Double>> {
    @Override
    public Double exec(Pow<Double> function) {
        return Math.pow(function.getOperand(), function.getPow());
    }
}
