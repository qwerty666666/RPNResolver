package expression;

import java.util.List;

/**
 * Expression in Reverse Polish Notation
 */
public class RPNExpression<T> {
    List<Object> tokens;


    public RPNExpression(List<Object> tokens) {
        this.tokens = tokens;
    }


    public T calc() {
        // TODO
        return null;
    }


    public List<Object> getTokens() {
        return tokens;
    }
}
