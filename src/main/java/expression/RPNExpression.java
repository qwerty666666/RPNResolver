package expression;

import java.util.*;

/**
 * Expression in Reverse Polish Notation
 */
public class RPNExpression<T> {
    List<Token> tokens;


    public RPNExpression(List<Token> tokens) {
        this.tokens = tokens;
    }


    protected void handleOperatorToken(ListIterator<Token> iterator, OperatorExecutor<T> oe) {
        Token token = iterator.previous();

        if (iterator.nextIndex() < 2) {
            throw new IllegalStateException("Invalid tokens list. Operator requires two operands");
        }

        T right = ((OperandSupplier<T>)iterator.previous()).get();
        iterator.remove();
        T left = ((OperandSupplier<T>)iterator.previous()).get();
        iterator.remove();

        iterator.next();
        iterator.set(new OperandSupplier<>(oe.exec(left, right)));
    }


    protected void handleFunctionToken(ListIterator<Token> iterator, FunctionExecutor<T> fe) {
        Token token = iterator.previous();

        int requiredArgsCnt = ((Function)token).getArgs().size();
        if (iterator.nextIndex() < requiredArgsCnt) {
            throw new IllegalStateException("Invalid tokens list. Function " + token + " require " + requiredArgsCnt + " operands");
        }

        List<Object> args = new ArrayList<>();
        for (int i = 0; i < requiredArgsCnt; i++) {
            args.add(0, ((OperandSupplier<T>)iterator.previous()).get());
            iterator.remove();
        }

        iterator.next();
        iterator.set(new OperandSupplier<>(fe.exec(args.toArray())));
    }


    public T calc(OperatorExecutorProvider<T> operatorExecutorProvider, FunctionExecutorProvider<T> functionExecutorProvider) {
        List<Token> tokens = new ArrayList<>(this.tokens);

        ListIterator<Token> iterator = tokens.listIterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            if (token instanceof OperandSupplier) {
                // remain it in tokens list
            } else if (Token.isTokenOperator(token)) {
                handleOperatorToken(iterator, operatorExecutorProvider.get((Class<? extends Operator<T>>)token.getClass()));
            } else if (Token.isTokenFunction(token)) {
                handleFunctionToken(iterator, functionExecutorProvider.get((Class<? extends Function<T>>)token.getClass()));
            } else {
                throw new IllegalArgumentException("Unknown token type " + token);
            }
        }

        if (tokens.size() == 1 && tokens.get(0) instanceof OperandSupplier) {
            return ((OperandSupplier<T>)tokens.get(0)).get();
        } else {
            throw new IllegalStateException("The tokens list is in invalid state after calculating");
        }
    }


    public List<Token> getTokens() {
        return tokens;
    }
}
