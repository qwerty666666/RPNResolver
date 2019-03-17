package expression;

import functions.Function;
import functions.FunctionExecutor;
import operands.OperandSupplier;
import operators.Operator;
import operators.OperatorAssociativity;

import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Implementation of the Shunting-yard algorithm invented by Edsger Dijkstra.
 *
 * @param <T>
 */
public class ShuntingYardRPNConverter<T> implements RPNConverter<T> {
    protected Stack<Object> RPNStack = new Stack<>();
    protected Stack<Object> operatorStack = new Stack<>();

    protected Map<Class<? extends Operator<T>>, Operator<T>> operatorsMap;
    protected Map<Class<? extends Function<T>>, FunctionExecutor<T, ? extends Function<T>>> functionsMap;


    public ShuntingYardRPNConverter(
            Map<Class<? extends Operator<T>>, Operator<T>> operatorsMap,
            Map<Class<? extends Function<T>>, FunctionExecutor<T, ? extends Function<T>>> functionsMap
    ) {
        this.operatorsMap = operatorsMap;
        this.functionsMap = functionsMap;
    }


    protected Operator<T> getOperator(Class operatorClass) {
        if (this.operatorsMap.containsKey(operatorClass)) {
            return this.operatorsMap.get(operatorClass);
        }
        throw new IllegalArgumentException("Operator implementation for " + operatorClass + "doesn't provided");
    }


    protected FunctionExecutor<T, ? extends Function<T>> getFunctionExecutor(Class functionClass) {
        if (this.functionsMap.containsKey(functionClass)) {
            return this.functionsMap.get(functionClass);
        }
        throw new IllegalArgumentException("functions.FunctionExecutor implementation for " + functionClass + "doesn't provided");
    }


    @Override
    public RPNExpression<T> convert(List<Object> tokens) {
        for (Object token: tokens) {
            if (token instanceof OperandSupplier) {
                RPNStack.push(token);
            } else if (token instanceof Function) {
                operatorStack.push(this.getFunctionExecutor(token.getClass()));
            } else if (token == Tokenizer.FUNCTION_ARGUMENT_SEPARATOR) {
                // ignore it
            } else if (ExpressionUtils.isTokenOperator(token)) {
                while (!operatorStack.isEmpty()) {
                    Object topOperator = operatorStack.peek();

                    if (topOperator == Parentheses.OPENING_PAREN) {
                        break;
                    }

                    if (topOperator instanceof Function) {
                        RPNStack.push(this.getFunctionExecutor(operatorStack.pop().getClass()));
                        continue;
                    }

                    if (ExpressionUtils.isTokenOperator(topOperator)) {
                        int tokenPrecedence = ((Operator)token).getPrecedence();
                        int topTokenPrecedence = ((Operator)topOperator).getPrecedence();
                        OperatorAssociativity topTokenAssociativity = ((Operator)topOperator).getAssociativity();

                        if ((topTokenPrecedence > tokenPrecedence) ||
                                ((topTokenPrecedence == tokenPrecedence) && (topTokenAssociativity == OperatorAssociativity.LEFT_ASSOCIATIVE))
                        ) {
                            RPNStack.push(this.getOperator(operatorStack.pop().getClass()));
                            continue;
                        }
                    }

                    break;
                }

            } else if (token == Parentheses.OPENING_PAREN) {
                operatorStack.push(token);
            } else if (token == Parentheses.CLOSING_PAREN) {
                while (!operatorStack.isEmpty() && operatorStack.peek() != Parentheses.OPENING_PAREN) {
                    RPNStack.push(operatorStack.pop());
                }

                if (operatorStack.isEmpty()) {
                    throw new IllegalArgumentException("Opening paren is missed");
                }

                operatorStack.pop();  // remove opening paren

                if (!operatorStack.isEmpty() && (operatorStack.peek() instanceof Function)) {
                    RPNStack.push(operatorStack.pop());
                }
            } else {
                throw new IllegalArgumentException("Unknown token type " + token.getClass());
            }
        }

        while (!operatorStack.isEmpty()) {
            Object operator = operatorStack.pop();
            if (operator == Parentheses.CLOSING_PAREN || operator == Parentheses.OPENING_PAREN) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }

            RPNStack.push(operator);
        }

        return new RPNExpression<>(RPNStack);
    }
}
