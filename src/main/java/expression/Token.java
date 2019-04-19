package expression;

/**
 * Common interface for arithmetic operators and operands. <br>
 * So any Expression consists of Tokens.
 */
public interface Token {
    static boolean isTokenOperand(Token token) {
        return token instanceof Operand;
    }

    static boolean isTokenOperator(Token token) {
        return token instanceof Operator;
    }

    static boolean isTokenFunction(Token token) {
        return token instanceof Function;
    }
}
