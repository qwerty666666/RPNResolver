package functions;

public class IntegerPowExecutor implements FunctionExecutor<Integer, Pow<Integer>> {
    @Override
    public Integer exec(Pow<Integer> function) {
        return Math.pow(function.getArg(), function.getPow());
    }
}
