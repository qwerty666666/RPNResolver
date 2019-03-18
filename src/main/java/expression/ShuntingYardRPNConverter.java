package expression;

import functions.Function;
import functions.FunctionExecutor;
import operands.Operand;
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

    protected Map<Class<? extends Operator>, Operator<T>> operatorsMap;
    protected Map<Class<? extends Function>, FunctionExecutor<T, ? extends Function<T>>> functionsMap;


    public ShuntingYardRPNConverter(
            Map<Class<? extends Operator>, Operator<T>> operatorsMap,
            Map<Class<? extends Function>, FunctionExecutor<T, ? extends Function<T>>> functionsMap
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
        throw new IllegalArgumentException("FunctionExecutor implementation for " + functionClass + "doesn't provided");
    }


    protected void handleFunctionToken(Function token) {
        operatorStack.push(this.getFunctionExecutor(token.getClass()));
    }


    protected void handleOperatorToken(Class operatorClass) {
        Operator token = this.getOperator(operatorClass);

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
                int tokenPrecedence = (token).getPrecedence();
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

        operatorStack.push(token);
    }


    protected void handleOpeningParen() {
        operatorStack.push(Parentheses.OPENING_PAREN);
    }


    protected void handleClosingParen() {
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
    }


    protected void handleOperandToken(Object operand) {
        RPNStack.push(operand);
    }


    @Override
    public RPNExpression<T> convert(List<Object> tokens) {
        for (Object token: tokens) {
            if (token instanceof Function) {
                handleFunctionToken((Function)token);
            } else if (token == Tokenizer.FUNCTION_ARGUMENT_SEPARATOR) {
                // ignore it
            } else if (ExpressionUtils.isTokenOperator(token)) {
                handleOperatorToken((Class)token);
            } else if (token == Parentheses.OPENING_PAREN) {
                handleOpeningParen();
            } else if (token == Parentheses.CLOSING_PAREN) {
                handleClosingParen();
            } else {
                handleOperandToken(token);
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
