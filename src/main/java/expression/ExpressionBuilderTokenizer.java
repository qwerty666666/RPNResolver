package expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ExpressionBuilderTokenizer<T> implements Tokenizer<T> {
    protected ExpressionBuilder<T> eb;

    public ExpressionBuilderTokenizer(ExpressionBuilder<T> eb) {
        this.eb = eb;
    }

    /**
     * Tokenize the expression to plain tokens
     *
     * @return tokens stack
     */
    @Override
    public List<Token> tokenize() {
        return this.getExpressionTokens(eb);
    }


    /**
     * Retrieve operator tokens
     */
    protected List<Token> getOperatorTokens(Operator<T> operator) {
        return Collections.singletonList(operator);
    }


    /**
     * @return tokens stack of expression
     */
    protected List<Token> getExpressionTokens(ExpressionBuilder<T> eb) {
        List<Token> tokens = new ArrayList<>();

        for (Token unit: eb.getUnits()) {
            if (Token.isTokenOperator(unit)) {
                tokens.addAll(this.getOperatorTokens((Operator<T>)unit));
            } else if (Token.isTokenOperand(unit)) {
                tokens.addAll(this.getOperandTokens((Operand<T>)unit));
            } else {
                throw new IllegalArgumentException("Unknown token type");
            }
        }

        return tokens;
    }


    /**
     * Retrieve operand supplier tokens
     */
    protected List<Token> getOperandSupplierTokens(OperandSupplier<T> operand) {
        return Collections.singletonList(operand);
    }


    /**
     * Retrieve function tokens
     */
    protected List<Token> getFunctionTokens(Function<T> function) {
        List<Token> tokens = new ArrayList<>();

        tokens.add(function);
        tokens.add(Parentheses.OPENING_PAREN);

        int ind = 0;
        for (Object arg: function.getArgs()) {
            if (arg instanceof Operand) {
                tokens.addAll(this.getOperandTokens((Operand<T>)arg));
            } else {
                tokens.add(new OperandSupplier<>(arg));
            }

            if (++ind < function.getArgs().size()) {
                tokens.add(FUNCTION_ARGUMENT_SEPARATOR);
            }
        }

        tokens.add(Parentheses.CLOSING_PAREN);

        return tokens;
    }


    /**
     * Retrieve units from another expressionBuilder. I.e. adds expressionBuilder in brackets
     */
    protected List<Token> getGroupTokens(ExpressionBuilder<T> eb) {
        List<Token> tokens = new ArrayList<>();

        tokens.add(Parentheses.OPENING_PAREN);
        tokens.addAll(this.getExpressionTokens(eb));
        tokens.add(Parentheses.CLOSING_PAREN);

        return tokens;
    }


    /**
     * @return operand tokens
     */
    protected List<Token> getOperandTokens(Operand<T> operand) {
        if (operand instanceof Function) {
            return this.getFunctionTokens((Function<T>)operand);
        } else if (operand instanceof ExpressionBuilder) {
            return this.getGroupTokens((ExpressionBuilder<T>)operand);
        } else if (operand instanceof OperandSupplier) {
            return this.getOperandSupplierTokens((OperandSupplier<T>)operand);
        } else {
            throw new IllegalArgumentException("Argument should be instance of Function, ExpressionBuilder or OperandSupplier");
        }
    }
}
