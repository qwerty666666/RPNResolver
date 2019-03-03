import operators.Operator;

import java.util.Stack;

public class RPNResolver {
    protected Expression expression;
    protected Stack<Object> RPNStack = new Stack<>();
    protected Stack<Operator> operatorStack = new Stack<>();


    public RPNResolver(Expression expression) {
        this.expression = expression;
        this.resolve();
    }


    protected void resolve() {

    }
}
