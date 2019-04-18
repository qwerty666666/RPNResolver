package expression;

import java.util.List;


/**
 * Tokenize the expression.
 * Split given expression into operators, operands, functions, brackets and so on.
 *
 * @param <T>
 */
public interface Tokenizer<T> {
    /**
     * Token representing arguments separator in functions
     */
     Token FUNCTION_ARGUMENT_SEPARATOR = new Token(){};


    /**
     * Tokenize the expression to to plain tokens
     *
     * @return tokens stack
     */
    List<Token> tokenize();
}
