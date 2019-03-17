package expression;

import expression.ExpressionBuilder;
import operands.Operand;
import operands.OperandSupplier;
import operators.AddOperator;
import operators.Operator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;


class ExpressionBuilderTest {
    @Test
    void testPushOperatorIllegalState() {
        assertThrows(
            IllegalStateException.class,
            () -> {
                new ExpressionBuilder<>()
                    .pushOperator(Operator.class);
            },
            "Operator can't be at the first position in expressionBuilder"
        );
        assertThrows(
            IllegalStateException.class,
            () -> {
                new ExpressionBuilder<>()
                    .pushOperand(Mockito.mock(OperandSupplier.class))
                    .pushOperator(Operator.class)
                    .pushOperator(Operator.class);
            },
            "Operators can't go after another operator"
        );
    }


    @Test
    void testPushOperandIllegalState() {
        ExpressionBuilder<Integer> eb = new ExpressionBuilder<Integer>()
            .pushOperand(1);

        assertThrows(
            IllegalStateException.class,
            () -> eb.pushOperand(1),
            "Operand can't forward another operand"
        );
    }


    @Test
    void testPushOperator() {
        ExpressionBuilder expr = new ExpressionBuilder();
        expr.pushOperand(Mockito.mock(OperandSupplier.class))
            .pushOperator(Operator.class);

        assertEquals(expr.getLastUnit(), Operator.class, "When push operator, it must be at the last position");
    }


    @Test
    void testPushOperand() {
        ExpressionBuilder eb = new ExpressionBuilder();
        Operand op = Mockito.mock(OperandSupplier.class);
        eb.pushOperand(op);

        assertEquals(eb.getLastUnit(), op, "When push operand, it must be at the last position");
    }


    @Test
    void testPush() {
        Operand operand = Mockito.mock(OperandSupplier.class);
        ExpressionBuilder e = new ExpressionBuilder()
            .push(AddOperator.class, operand)
            .push(AddOperator.class, operand);

        assertAll("push",
            () -> assertNotEquals(AddOperator.class, e.getUnits().get(0),
                "When call push method for the first time (when stack is empty), operator must be omitted"
            ),
            () -> assertEquals(AddOperator.class, e.getUnits().get(1),
                "Must add operator to the units stack"
            ),
            () -> assertEquals(operand, e.getLastUnit(),
                "Must push operand to the stack"
            )
        );
    }


    @Test
    void testPushGenericOperand() {
        Integer test = new Integer(1);
        assertTrue(new ExpressionBuilder<Integer>().pushOperand(test).getLastUnit() instanceof OperandSupplier,
            "Generic type should be converted to OperandSupplier"
        );
    }
}