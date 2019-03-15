import expression.ExpressionUtils;
import operands.OperandSupplier;
import operators.Operator;
import operators.OperatorAssociativity;

import java.util.List;
import java.util.Map;
import java.util.Stack;


public interface RPNConverter<T> {
    /**
     * Convert mathematical expressions to Reverse Polish Notation (RPN).
     *
     * @param tokens expression tokens
     */
    public RPNExpression<T> convert(List<Object> tokens);
}
