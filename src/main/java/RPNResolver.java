import operators.Operator;

import java.util.Map;
import java.util.Stack;

public class RPNResolver<T> {
    protected ExpressionBuilder expressionBuilder;
    protected Stack<Object> RPNStack = new Stack<>();
    protected Stack<Object> operatorStack = new Stack<>();

    protected Map<Class<? extends Operator<T>>, Class<? extends Operator<T>>> operatorsMap;


    public RPNResolver(ExpressionBuilder<T> expressionBuilder, Map<Class<? extends Operator<T>>, Class<? extends Operator<T>>> operatorsMap) {
        this.expressionBuilder = expressionBuilder;
        this.operatorsMap = operatorsMap;

        this.resolve();
    }


    protected Operator<T> getOperator(Class operatorClass) {
        if (this.operatorsMap.containsKey(operatorClass)) {
            try {
                return this.operatorsMap.get(operatorClass).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Can't instantiate " + operatorClass);
            }
        }

        throw new IllegalArgumentException("operators.Operator implementation for " + operatorClass + "doesn't provided");
    }


    protected boolean isOperator(Object token) {
        return (token instanceof Class) && (Operator.class.isAssignableFrom((Class)token));
    }


    protected void resolve() {
//        for (Object token: expressionBuilder.getUnits()) {
//            if (token instanceof operands.OperandSupplier) {
//                RPNStack.push(token);
//            } else if (token instanceof Function) {
//                RPNStack.push(token);
//            } else if (isOperator(token)) {
//                Object topOperator = operatorStack.peek();
//                while ((topOperator instanceof Function) ||
//                    (isOperator(topOperator) && ((operators.Operator)topOperator).getPrecedence() > ((operators.Operator)token).getPrecedence()) ||
//                    (isOperator(topOperator) && ((operators.Operator)topOperator).getPrecedence() == ((operators.Operator)token).getPrecedence()) ||
//                )
//
//            }
//        while (
//              (there is a function at the top of the operator stack)
//              or (there is an operator at the top of the operator stack with greater precedence)
//              or (the operator at the top of the operator stack has equal precedence and is left associative)
//      )
//        and (the operator at the top of the operator stack is not a left parenthesis):
//        pop operators from the operator stack onto the output queue.
//        push it onto the operator stack.
//        if the token is a left paren (i.e. "("), then:
//        push it onto the operator stack.
//        if the token is a right paren (i.e. ")"), then:
//        while the operator at the top of the operator stack is not a left paren:
//        pop the operator from the operator stack onto the output queue.
//        /* if the stack runs out without finding a left paren, then there are mismatched parentheses. */
//        if there is a left paren at the top of the operator stack, then:
//        pop the operator from the operator stack and discard it
//        if there are no more units to read:
//        while there are still operator units on the stack:
//        /* if the operator token on the top of the stack is a paren, then there are mismatched parentheses. */
//        pop the operator from the operator stack onto the output queue.
//        }
    }
}
