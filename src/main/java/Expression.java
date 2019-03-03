import operators.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
     * Map operators to its executors i.g. AddOperator => DoubleAddOperator.class
     */
    protected Map<Class<? extends Operator>, Class<? extends Operator<T>>> operatorsMap = new HashMap<>();



    protected Expression() {}


    public Expression(Map<Class<? extends Operator>, Class<? extends Operator<T>>> operatorsMap) {
        this.setOperatorsMap(operatorsMap);
    }


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
        return tokens.isEmpty() ||
            getLastToken() instanceof Operator ||
            getLastToken() == Parentheses.OPENING_PAREN ||
            getLastToken() == Expression.FUNCTION_ARGUMENT_SEPARATOR;
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
     * Push operand to the stack
     */
    protected Expression<T> pushOperand(Operand<T> operand) {
        if (!isPushOperandAllowed()) {
            throw new IllegalStateException("Operand must be placed forward operator or be the first token in expression");
        }

        tokens.add(operand);

        return this;
    }


    /**
     * Push operator to the stack
     */
    protected Expression<T> pushOperator(Operator<T> operator) {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("Operator can't be the first token in expression");
        }
        if (getLastToken() instanceof Operator) {
            throw new IllegalStateException("Operator can't forward another operator");
        }

        tokens.add(operator);

        return this;
    }


    /**
     * Push operand to the stack
     */
    protected Expression<T> pushFunction(Function<T> function) {
        if (!isPushOperandAllowed()) {
            throw new IllegalStateException("Function must be placed forward operator or be the first token in expression");
        }

        tokens.add(function);
        this.openParenthesis();

        int ind = 0;
        for (Object arg: function.getArgs()) {
            if (arg instanceof Expression) {
                this.pushGroup((Expression<T>)arg);
            } else if (arg instanceof Operand) {
                this.pushOperand((Operand<T>)arg);
            } else {
                throw new IllegalArgumentException("Function arguments must be Operand or Expression which can be evaluated to operand");
            }

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
        if (!isPushOperandAllowed()) {
            throw new IllegalStateException("Group must be forward operator or be the first token in expression");
        }

        this.openParenthesis();
        tokens.addAll(group.getTokens());
        this.closeParenthesis();

        return this;
    }


    /**
     * @return the expression tokens stack
     */
    public List<Object> getTokens() {
        return this.tokens;
    }


    /**
     * Map operators to its executors of specified type.
     * i.g. AddOperator.class => new DoubleAddOperator()
     */
    public void setOperatorsMap(Map<Class<? extends Operator>, Class<? extends Operator<T>>> operatorsMap) {
        this.operatorsMap = operatorsMap;
    }


    /**
     * @return executor instance for the specified operator
     */
    protected Operator<T> getOperatorInstance(Class<? extends Operator> operator) {
        if (!this.operatorsMap.containsKey(operator)) {
            throw new IllegalArgumentException("Operator executor doesn't specified for " + operator);
        }

        try {
            return this.operatorsMap.get(operator).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Add operator and operand to the tokens stack
     */
    protected Expression<T> push(Class<? extends Operator> operator, Operand<T> operand) {
        this.pushOperator(this.getOperatorInstance(operator));
        this.pushOperand(operand);

        return this;
    }


    public Expression<T> add(Operand<T> operand) {
        this.push(AddOperator.class, operand);
        return this;
    }


    public Expression<T> subtract(Operand<T> operand) {
        this.push(SubtractOperator.class, operand);
        return this;
    }


    public Expression<T> multiply(Operand<T> operand) {
        this.push(MultiplyOperator.class, operand);
        return this;
    }


    public Expression<T> divide(Operand<T> operand) {
        this.push(DivideOperator.class, operand);
        return this;
    }
}
