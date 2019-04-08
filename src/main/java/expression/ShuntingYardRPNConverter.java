package expression;

import functions.Function;
import functions.FunctionExecutor;
import operands.Operand;
import operators.Operator;
import operators.OperatorAssociativity;
import providers.FunctionExecutorProvider;
import providers.OperatorProvider;

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

    protected OperatorProvider<T> operatorProvider;
    protected FunctionExecutorProvider<T> functionExecutorProvider;


    public ShuntingYardRPNConverter(OperatorProvider<T> operatorProvider, FunctionExecutorProvider<T> functionExecutorProvider) {
        this.operatorProvider = operatorProvider;
        this.functionExecutorProvider = functionExecutorProvider;
    }


    protected Operator<T> getOperator(Class operatorClass) {
        return this.operatorProvider.get(operatorClass);
    }


    protected FunctionExecutor<T, ? extends Function<T>> getFunctionExecutor(Class functionClass) {
        return this.functionExecutorProvider.get(functionClass);
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

            if (topOperator instanceof FunctionExecutor) {
                RPNStack.push(token);
                operatorStack.pop();
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
