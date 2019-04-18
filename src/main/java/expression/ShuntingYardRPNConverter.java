package expression;

import java.util.List;
import java.util.Stack;


/**
 * Implementation of the Shunting-yard algorithm invented by Edsger Dijkstra.
 *
 * @param <T>
 */
public class ShuntingYardRPNConverter<T> implements RPNConverter<T> {
    /**
     * The result stack of expression items in reverse polish notation
     */
    protected Stack<Token> RPNStack = new Stack<>();
    /**
     * The operator stack in Shunting Yard algorithm.
     * Can contains any of Function, Parentheses.OPENING_PAREN, Parentheses.CLOSING_PAREN, Operator
     */
    protected Stack<Token> operatorStack = new Stack<>();


    protected void handleFunctionToken(Function<T> token) {
        operatorStack.push(token);
    }


    protected void handleOperatorToken(Operator<T> token) {
        while (!operatorStack.isEmpty()) {
            Token topOperator = operatorStack.peek();

            if (topOperator == Parentheses.OPENING_PAREN) {
                break;
            }

            if (topOperator instanceof Function) {
                RPNStack.push(topOperator);
                operatorStack.pop();
                continue;
            }

            if (topOperator instanceof Operator) {
                int tokenPrecedence = token.getPrecedence();
                int topTokenPrecedence = ((Operator)topOperator).getPrecedence();
                OperatorAssociativity tokenAssociativity = token.getAssociativity();

                if ((topTokenPrecedence <= tokenPrecedence && tokenAssociativity == OperatorAssociativity.LEFT_ASSOCIATIVE)
                    || (topTokenPrecedence > tokenPrecedence && tokenAssociativity == OperatorAssociativity.RIGHT_ASSOCIATIVE)
                ) {
                    RPNStack.push(topOperator);
                    operatorStack.pop();
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

        if (operatorStack.isEmpty() || operatorStack.peek() != Parentheses.OPENING_PAREN) {
            throw new IllegalStateException("Opening paren is missed");
        }

        operatorStack.pop();  // remove opening paren

        if (!operatorStack.isEmpty() && (operatorStack.peek() instanceof Function)) {
            RPNStack.push(operatorStack.pop());
        }
    }


    protected void handleOperandToken(Operand<T> operand) {
        RPNStack.push(operand);
    }


    @Override
    public RPNExpression<T> convert(List<Token> tokens) {
        for (Token token: tokens) {
            if (Token.isTokenFunction(token)) {
                handleFunctionToken((Function)token);
            } else if (token == Tokenizer.FUNCTION_ARGUMENT_SEPARATOR) {
                // ignore it
            } else if (Token.isTokenOperator(token)) {
                handleOperatorToken((Operator)token);
            } else if (token == Parentheses.OPENING_PAREN) {
                handleOpeningParen();
            } else if (token == Parentheses.CLOSING_PAREN) {
                handleClosingParen();
            } else if (Token.isTokenOperand(token)){
                handleOperandToken((Operand<T>)token);
            } else {
                throw new IllegalArgumentException("Unknown token type " + token.getClass() + "[" + token + "]");
            }
        }

        while (!operatorStack.isEmpty()) {
            Token operator = operatorStack.pop();
            if (operator == Parentheses.CLOSING_PAREN || operator == Parentheses.OPENING_PAREN) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }

            RPNStack.push(operator);
        }

        return new RPNExpression<>(RPNStack);
    }
}
