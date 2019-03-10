import operands.Operand;
import operands.OperandSupplier;
import operators.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Tokenize the expression.
 * Split giver expression into operators, operands, functions, brackets and so on.
 *
 * @param <T>
 */
public class Tokenizer<T> {
    /**
     * Token representing arguments separator in functions
     */
    public static Object FUNCTION_ARGUMENT_SEPARATOR = new Object();


    /**
     * Tokenize the expression to to plain tokens
     *
     * @return tokens stack
     */
    public List<Object> tokenize(ExpressionBuilder eb) {
        return this.getExpressionTokens(eb);
    }


    /**
     * Check if the unit is operator
     */
    protected boolean isOperator(Object unit) {
        return (unit instanceof Class) && Operator.class.isAssignableFrom((Class)unit);
    }


    /**
     * Check if the unit is operator
     */
    protected boolean isOperand(Object unit) {
        return (unit instanceof Operand);
    }


    /**
     * Retrieve operator tokens
     */
    protected List<Object> getOperatorTokens(Class<? extends Operator> operator) {
        return Arrays.asList(operator);
    }


    /**
     * @return tokens stack of expression
     */
    protected List<Object> getExpressionTokens(ExpressionBuilder<T> eb) {
        List<Object> tokens = new ArrayList<>();

        for (Object unit: eb.getUnits()) {
            if (isOperator(unit)) {
                tokens.addAll(this.getOperatorTokens((Class<? extends Operator>) unit));
            } else if (isOperand(unit)) {
                tokens.addAll(this.getOperandTokens((Operand)unit));
            } else {
                throw new IllegalArgumentException("Expression units can be only operand and operators");
            }
        }

        return tokens;
    }


    /**
     * Retrieve operand supplier tokens
     */
    protected List<Object> getOperandSupplierTokens(OperandSupplier<T> operand) {
        return Arrays.asList(operand);
    }


    /**
     * Retrieve function tokens
     */
    protected List<Object> getFunctionTokens(Function<T> function) {
        List<Object> tokens = new ArrayList<>();

        tokens.add(function);
        tokens.add(Parentheses.OPENING_PAREN);

        int ind = 0;
        for (Operand<T> arg: function.getArgs()) {
            tokens.addAll(this.getOperandTokens(arg));

            if (++ind < function.getArgs().size()) {
                tokens.add(Tokenizer.FUNCTION_ARGUMENT_SEPARATOR);
            }
        }

        tokens.add(Parentheses.CLOSING_PAREN);

        return tokens;
    }


    /**
     * Retrieve units from another expressionBuilder. I.e. adds expressionBuilder in brackets
     */
    protected List<Object> getGroupTokens(ExpressionBuilder<T> eb) {
        List<Object> tokens = new ArrayList<>();

        tokens.add(Parentheses.OPENING_PAREN);
        tokens.addAll(this.getExpressionTokens(eb));
        tokens.add(Parentheses.CLOSING_PAREN);

        return tokens;
    }


    /**
     * @return operand tokens
     */
    protected List<Object> getOperandTokens(Operand<T> operand) {
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
