import operands.Operand;
import operands.OperandSupplier;
import operators.*;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Class provides fluent interface for building arithmetic expressions. </p>
 *
 * <p>ExpressionBuilder provides methods for all common operators (+, -, /, *).
 * But you can extend this behavior by extending this class and providing your own
 * methods.</p>
 *
 * <p>To start expression you can use any of provided methods.</p>
 *
 * <p>The common use of the class:
 * <pre>
 * {@code
 *     new ExpressionBuilder<Integer> eb = new ExpressionBuilder<>()
 *          .add(1)
 *          .subtract(2);
 * }
 * </pre>
 * </p>
 *
 * <p>The order of operators is determined by their precedence. To provide expression
 * in brackets you should use ExpressionBuilder as an argument for method
 * <pre>{@code
 *     // E.g. this is equals to 1 - 2 * 3
 *     new ExpressionBuilder<Integer> eb = new ExpressionBuilder<>()
 *          .add(1)
 *          .subtract(2)
 *          .multiply(3);
 *
 *     // this is equals to (1 - 2) * 3
 *     new ExpressionBuilder<Integer> eb = new ExpressionBuilder<>()
 *          .add(new ExpressionBuilder<Integer>()
 *                  .add(1)
 *                  .subtract(2)
 *          )
 *          .multiply(3);
 * }</pre>
 * </p>
 *
 * @param <T>
 */
public class ExpressionBuilder<T> implements Operand<T> {
    /**
     * Stack expressionBuilder units.
     */
    protected List<Object> units = new ArrayList<>();


    /**
     * @return The last unit in the stack
     */
    protected Object getLastUnit() {
        if (units.isEmpty()) {
            return null;
        }
        return units.get(units.size() - 1);
    }

    /**
     * Check if the unit is operator
     */
    protected boolean isUnitOperator(Object unit) {
        return (unit instanceof Class) && Operator.class.isAssignableFrom((Class)unit);
    }


    /**
     * Check if the unit is operator
     */
    protected boolean isUnitOperand(Object unit) {
        return (unit instanceof Operand);
    }


    /**
     * Push operator to the stack
     */
    protected ExpressionBuilder<T> pushOperator(Class<? extends Operator> operator) {
        if (units.isEmpty()) {
            throw new IllegalStateException("operators.Operator can't be the first token in expressionBuilder");
        }

        if (isUnitOperator(getLastUnit())) {
            throw new IllegalStateException("operators.Operator can't forward another operator");
        }

        units.add(operator);

        return this;
    }


    /**
     * Push operand to the stack
     */
    protected ExpressionBuilder<T> pushOperand(T operand) {
        // replace T with OperandSupplier
        this.pushOperand(new OperandSupplier<>(operand));
        return this;
    }


    /**
     * Push operand to the stack
     */
    protected ExpressionBuilder<T> pushOperand(Operand<T> operand) {
        if (!units.isEmpty() && isUnitOperand(getLastUnit())) {
            throw new IllegalStateException("Can't push operand forward another operand");
        }

        units.add(operand);
        return this;
    }


    /**
     * Add operator and operand to the units stack
     */
    protected ExpressionBuilder<T> push(Class<? extends Operator> operator, Operand<T> operand) {
        if (this.units.size() > 0) {
            this.pushOperator(operator);
        }

        this.pushOperand(operand);

        return this;
    }


    /****************************
     *
     *    Public interface
     *
     ***************************/


    /**
     * Add operand to expressionBuilder. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public ExpressionBuilder<T> add(Operand<T> operand) {
        this.push(AddOperator.class, operand);
        return this;
    }


    /**
     * Subtract operand from expressionBuilder. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public ExpressionBuilder<T> subtract(Operand<T> operand) {
        this.push(SubtractOperator.class, operand);
        return this;
    }


    /**
     * Add multiplying to operand to expressionBuilder. Be aware of operators precedence. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public ExpressionBuilder<T> multiply(Operand<T> operand) {
        this.push(MultiplyOperator.class, operand);
        return this;
    }


    /**
     * Add dividing by operand to expressionBuilder. Be aware of operators precedence. <br>
     * If operand is ExpressionBuilder operand will be considered as group in brackets.
     */
    public ExpressionBuilder<T> divide(Operand<T> operand) {
        this.push(DivideOperator.class, operand);
        return this;
    }


    /**
     * @return the expressionBuilder units stack
     */
    public List<Object> getUnits() {
        return this.units;
    }
}
