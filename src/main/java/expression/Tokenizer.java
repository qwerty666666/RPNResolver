package expression;

import java.util.List;


/**
 * Tokenize the expression.
 * Split giver expression into operators, operands, functions, brackets and so on.
 *
 * @param <T>
 */
public interface Tokenizer<T> {
    /**
     * Token representing arguments separator in functions
     */
    public static Object FUNCTION_ARGUMENT_SEPARATOR = new Object();


    /**
     * Tokenize the expression to to plain tokens
     *
     * @return tokens stack
     */
    public List<Object> tokenize(ExpressionBuilder eb);
}
