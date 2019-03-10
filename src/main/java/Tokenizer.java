import operands.Operand;
import operands.OperandSupplier;
import operators.*;

import java.util.ArrayList;
import java.util.List;


public class Tokenizer<T> implements Operand<T> {
    /**
     * Stack expressionBuilder units.
     * Token can be operands.Operand, operators.Operator, Function and Parentheses
     */
    protected List<Object> tokens = new ArrayList<>();
    /**
     * Count of non-closed parenthesis in expressionBuilder
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
            lastToken == Tokenizer.FUNCTION_ARGUMENT_SEPARATOR;
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
    protected Tokenizer<T> openParenthesis() {
        this.tokens.add(Parentheses.OPENING_PAREN);
        this.openedParenthesisCount++;

        return this;
    }


    /**
     * Close parenthesis
     */
    protected Tokenizer<T> closeParenthesis() {
        if (this.openedParenthesisCount <= 0) {
            throw new IllegalStateException("ExpressionBuilder has no any opened parenthesis");
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
    protected Tokenizer<T> pushOperator(Class<? extends Operator> operator) {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("operators.Operator can't be the first token in expressionBuilder");
        }

        if (isTokenOperator(getLastToken())) {
            throw new IllegalStateException("operators.Operator can't forward another operator");
        }

        tokens.add(operator);

        return this;
    }


    /**
     * Push operand supplier to the stack
     */
    protected Tokenizer<T> pushOperandSupplier(OperandSupplier<T> operand) {
        this.tokens.add(operand);
        return this;
    }


    /**
     * Push operand to the stack
     */
    protected Tokenizer<T> pushFunction(Function<T> function) {
        tokens.add(function);
        this.openParenthesis();

        int ind = 0;
        for (Operand<T> arg: function.getArgs()) {
            this.pushOperand(arg);

            if (++ind < function.getArgs().size()) {
                tokens.add(Tokenizer.FUNCTION_ARGUMENT_SEPARATOR);
            }
        }

        this.closeParenthesis();

        return this;
    }


    /**
     * Push units from another expressionBuilder. I.e. adds expressionBuilder in brackets
     */
    protected Tokenizer<T> pushGroup(Tokenizer<T> group) {
        this.openParenthesis();
        tokens.addAll(group.getTokens());
        this.closeParenthesis();

        return this;
    }


    /**
     * Add operator and operand to the units stack
     */
    protected Tokenizer<T> push(Class<? extends Operator> operator, Operand<T> operand) {
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
    public Tokenizer<T> pushOperand(T operand) {
        // replace T with operands.OperandSupplier
        this.pushOperand(new OperandSupplier<>(operand));
        return this;
    }


    /**
     * Push operand to the stack
     */
    public Tokenizer<T> pushOperand(Operand<T> operand) {
        if (!isPushOperandAllowed()) {
            throw new IllegalStateException("operands.Operand must be placed forward operator or be the first token in expressionBuilder");
        }

        if (operand instanceof Function) {
            this.pushFunction((Function<T>)operand);
        } else if (operand instanceof Tokenizer) {
            this.pushGroup((Tokenizer<T>)operand);
        } else if (operand instanceof OperandSupplier) {
            this.pushOperandSupplier((OperandSupplier<T>)operand);
        } else {
            throw new IllegalArgumentException("Argument should be instance of Function, ExpressionBuilder or operands.OperandSupplier");
        }

        return this;
    }


    /**
     * Add operand to expressionBuilder. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public Tokenizer<T> add(Operand<T> operand) {
        this.push(AddOperator.class, operand);
        return this;
    }


    /**
     * Subtract operand from expressionBuilder. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public Tokenizer<T> subtract(Operand<T> operand) {
        this.push(SubtractOperator.class, operand);
        return this;
    }


    /**
     * Add multiplying to operand to expressionBuilder. Be aware of operators precedence. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public Tokenizer<T> multiply(Operand<T> operand) {
        this.push(MultiplyOperator.class, operand);
        return this;
    }


    /**
     * Add dividing by operand to expressionBuilder. Be aware of operators precedence. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public Tokenizer<T> divide(Operand<T> operand) {
        this.push(DivideOperator.class, operand);
        return this;
    }


    /**
     * @return the expressionBuilder units stack
     */
    public List<Object> getTokens() {
        return this.tokens;
    }
}
