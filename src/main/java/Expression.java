import operators.*;

import java.util.ArrayList;
import java.util.List;


public class Expression<T> implements Operand<T> {
    /**
     * Stack expression tokens.
     * Token can be Operand, Operator, Function and Parentheses
     */
    protected List<Object> tokens = new ArrayList<>();
    /**
     * Count of non-closed parenthesis in expression
     */
    protected int openedParenthesisCount = 0;
    /**
     * Separate arguments in functions
     */
    public static Object FUNCTION_ARGUMENT_SEPARATOR = new Object();


    /**
     * @return The last token in the stack
     */
    protected Object getLastToken() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.get(tokens.size() - 1);
    }


    /**
     * @return can push operand to the top of the stack
     */
    protected boolean isPushOperandAllowed() {
        Object lastToken = getLastToken();
        return tokens.isEmpty() ||
            this.isTokenOperator(lastToken) ||
            lastToken == Parentheses.OPENING_PAREN ||
            lastToken == Expression.FUNCTION_ARGUMENT_SEPARATOR;
    }


    /**
     * Check if the token is operator
     */
    protected boolean isTokenOperator(Object token) {
        return (token instanceof Class) && Operator.class.isAssignableFrom((Class)token);
    }


    /**
     * Open parenthesis
     */
    protected Expression<T> openParenthesis() {
        this.tokens.add(Parentheses.OPENING_PAREN);
        this.openedParenthesisCount++;

        return this;
    }


    /**
     * Close parenthesis
     */
    protected Expression<T> closeParenthesis() {
        if (this.openedParenthesisCount <= 0) {
            throw new IllegalStateException("Expression has no any opened parenthesis");
        }
        if (this.getLastToken() instanceof Operator) {
            throw new IllegalStateException("Close parenthesis can't be placed after operator");
        }

        this.tokens.add(Parentheses.CLOSING_PAREN);
        this.openedParenthesisCount--;

        return this;
    }


    /**
     * Push operator to the stack
     */
    protected Expression<T> pushOperator(Class<? extends Operator> operator) {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("Operator can't be the first token in expression");
        }

        if (isTokenOperator(getLastToken())) {
            throw new IllegalStateException("Operator can't forward another operator");
        }

        tokens.add(operator);

        return this;
    }


    /**
     * Push operand supplier to the stack
     */
    protected Expression<T> pushOperandSupplier(OperandSupplier<T> operand) {
        this.tokens.add(operand);
        return this;
    }


    /**
     * Push operand to the stack
     */
    protected Expression<T> pushFunction(Function<T> function) {
        tokens.add(function);
        this.openParenthesis();

        int ind = 0;
        for (Operand<T> arg: function.getArgs()) {
            this.pushOperand(arg);

            if (++ind < function.getArgs().size()) {
                tokens.add(Expression.FUNCTION_ARGUMENT_SEPARATOR);
            }
        }

        this.closeParenthesis();

        return this;
    }


    /**
     * Push tokens from another expression. I.e. adds expression in brackets
     */
    protected Expression<T> pushGroup(Expression<T> group) {
        this.openParenthesis();
        tokens.addAll(group.getTokens());
        this.closeParenthesis();

        return this;
    }


    /**
     * Add operator and operand to the tokens stack
     */
    protected Expression<T> push(Class<? extends Operator> operator, Operand<T> operand) {
        this.pushOperator(operator);
        this.pushOperand(operand);

        return this;
    }


    /****************************
     *
     *    Public interface
     *
     ***************************/


    /**
     * Push operand to the stack
     */
    public Expression<T> pushOperand(T operand) {
        // replace T with OperandSupplier
        this.pushOperand(new OperandSupplier<>(operand));
        return this;
    }


    /**
     * Push operand to the stack
     */
    public Expression<T> pushOperand(Operand<T> operand) {
        if (!isPushOperandAllowed()) {
            throw new IllegalStateException("Operand must be placed forward operator or be the first token in expression");
        }

        if (operand instanceof Function) {
            this.pushFunction((Function<T>)operand);
        } else if (operand instanceof Expression) {
            this.pushGroup((Expression<T>)operand);
        } else if (operand instanceof OperandSupplier) {
            this.pushOperandSupplier((OperandSupplier<T>)operand);
        } else {
            throw new IllegalArgumentException("Argument should be instance of Function, Expression or OperandSupplier");
        }

        return this;
    }


    /**
     * Add operand to expression. <br>
     * If operand is Expression operand will be considered as group in brackets.
     */
    public Expression<T> add(Operand<T> operand) {
        this.push(AddOperator.class, operand);
        return this;
    }


    /**
     * Subtract operand from expression. <br>
     * If operand is Expression operand will be considered as group in brackets.
     */
    public Expression<T> subtract(Operand<T> operand) {
        this.push(SubtractOperator.class, operand);
        return this;
    }


    /**
     * Add multiplying to operand to expression. Be aware of operators precedence. <br>
     * If operand is Expression operand will be considered as group in brackets.
     */
    public Expression<T> multiply(Operand<T> operand) {
        this.push(MultiplyOperator.class, operand);
        return this;
    }


    /**
     * Add dividing by operand to expression. Be aware of operators precedence. <br>
     * If operand is Expression operand will be considered as group in brackets.
     */
    public Expression<T> divide(Operand<T> operand) {
        this.push(DivideOperator.class, operand);
        return this;
    }


    /**
     * @return the expression tokens stack
     */
    public List<Object> getTokens() {
        return this.tokens;
    }
}
