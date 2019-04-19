package expression;

import operators.AddOperator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


class ExpressionBuilderTest {
    @Test
    void testPushOperatorIllegalState() {
        Operator operatorMock = mock(Operator.class);
        Operand operandMock = mock(Operand.class);

        assertThrows(
            IllegalStateException.class,
            () -> {
                new ExpressionBuilder<>()
                    .pushOperator(operatorMock);
            },
            "Operator can't be at the first position in expressionBuilder"
        );
        assertThrows(
            IllegalStateException.class,
            () -> {
                new ExpressionBuilder<>()
                    .pushOperand(operandMock)
                    .pushOperator(operatorMock)
                    .pushOperator(operatorMock);
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
        Operator op = mock(Operator.class);

        ExpressionBuilder expr = new ExpressionBuilder();
        expr.pushOperand(Mockito.mock(OperandSupplier.class))
            .pushOperator(op);

        assertEquals(op, expr.getLastUnit(), "When push operator, it must be at the last position");
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
            .push(new AddOperator(), operand)
            .push(new AddOperator(), operand);

        assertAll("push",
            () -> assertNotEquals(new AddOperator<>(), e.getUnits().get(0),
                "When call push method for the first time (when stack is empty), operator must be omitted"
            ),
            () -> assertEquals(new AddOperator<>(), e.getUnits().get(1),
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