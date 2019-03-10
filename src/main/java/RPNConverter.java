import expression.ExpressionUtils;
import operands.OperandSupplier;
import operators.Operator;
import operators.OperatorAssociativity;

import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Implementation of the Shunting-yard algorithm invented by Edsger Dijkstra.
 * Convert mathematical expressions to Reverse Polish Notation (RPN). And used to evaluate
 * this expressions.
 *
 * @param <T>
 */
public class RPNConverter<T> {
    protected Stack<Object> RPNStack = new Stack<>();
    protected Stack<Object> operatorStack = new Stack<>();

    protected Map<Class<? extends Operator<T>>, Class<? extends Operator<T>>> operatorsMap;


    public RPNConverter(Map<Class<? extends Operator<T>>, Class<? extends Operator<T>>> operatorsMap) {
        this.operatorsMap = operatorsMap;
    }


    protected Operator<T> getOperator(Class operatorClass) {
        if (this.operatorsMap.containsKey(operatorClass)) {
            try {
                return this.operatorsMap.get(operatorClass).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Can't instantiate " + operatorClass);
            }
        }

        throw new IllegalArgumentException("Operator implementation for " + operatorClass + "doesn't provided");
    }


    protected void convert(List<Object> tokens) {
        for (Object token: tokens) {
            if (token instanceof OperandSupplier) {
                RPNStack.push(token);
            } else if (token instanceof Function) {
                operatorStack.push(token);
            } else if (token == Tokenizer.FUNCTION_ARGUMENT_SEPARATOR) {
                // ignore it
            } else if (ExpressionUtils.isTokenOperator(token)) {
                while (!operatorStack.isEmpty()) {
                    Object topOperator = operatorStack.peek();

                    if (topOperator == Parentheses.OPENING_PAREN) {
                        break;
                    }

                    if (topOperator instanceof Function) {
                        RPNStack.push(operatorStack.pop());
                        continue;
                    }

                    if (ExpressionUtils.isTokenOperator(topOperator)) {
                        int tokenPrecedence = ((Operator)token).getPrecedence();
                        int topTokenPrecedence = ((Operator)topOperator).getPrecedence();
                        OperatorAssociativity topTokenAssociativity = ((Operator)topOperator).getAssociativity();

                        if ((topTokenPrecedence > tokenPrecedence) ||
                            ((topTokenPrecedence == tokenPrecedence) && (topTokenAssociativity == OperatorAssociativity.LEFT_ASSOCIATIVE))
                        ) {
                            RPNStack.push(operatorStack.pop());
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

                if (operatorStack.peek() == Parentheses.OPENING_PAREN) {
                    operatorStack.pop();
                } else {
                    throw new IllegalArgumentException("Opening paren is missed");
                }
            }
        }

        while (!operatorStack.isEmpty()) {
            Object operator = operatorStack.pop();
            if (operator == Parentheses.CLOSING_PAREN || operator == Parentheses.OPENING_PAREN) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }

            RPNStack.push(operator);
        }
    }
}
