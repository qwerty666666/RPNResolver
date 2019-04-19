package expression;

import java.util.List;


public interface RPNConverter<T> {
    /**
     * Convert mathematical expressions to Reverse Polish Notation (RPN).
     *
     * @param tokens expression tokens
     */
    public RPNExpression<T> convert(List<Token> tokens);
}
