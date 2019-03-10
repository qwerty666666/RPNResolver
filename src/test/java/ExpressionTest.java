import operands.Operand;
import operands.OperandSupplier;
import operators.AddOperator;
import operators.Operator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


class ExpressionTest {
//    @Test
//    void testExpressionRedundantClosedParenthesis() {
//        assertThrows(IllegalStateException.class,
//            () -> new ExpressionBuilder<>()
//                .openParenthesis()
//                .closeParenthesis()
//                .closeParenthesis(),
//            "Should be unable to close paren when opened paren doesn't exist"
//        );
//
//        ExpressionBuilder expr = new ExpressionBuilder();
//        expr.units = Arrays.asList(Parentheses.OPENING_PAREN, Mockito.mock(Operator.class));
//        assertThrows(IllegalStateException.class,
//            () -> expr.closeParenthesis(),
//            "Close parenthesis can't be placed after operator"
//        );
//    }
//
//
//    @Test
//    void testPushOperatorIllegalState() {
//        assertThrows(
//            IllegalStateException.class,
//            () -> new ExpressionBuilder<>().pushOperator(Operator.class),
//            "operators.Operator can't be at the first position in expressionBuilder"
//        );
//        assertThrows(
//            IllegalStateException.class,
//            () -> {
//                new ExpressionBuilder<>()
//                    .pushOperand(Mockito.mock(OperandSupplier.class))
//                    .pushOperator(Operator.class)
//                    .pushOperator(Operator.class);
//            },
//            "Operators can't go after another operator"
//        );
//    }
//
//
//    @Test
//    void isPushOperandAllowed() {
//        ExpressionBuilder expr = new ExpressionBuilder();
//
//        expr.units = Arrays.asList(Parentheses.CLOSING_PAREN);
//        assertFalse(expr.isPushOperandAllowed(), "operands.Operand can't forward parenthesis");
//
//        expr.units = Arrays.asList(Mockito.mock(Operand.class));
//        assertFalse(expr.isPushOperandAllowed(), "operands.Operand can't forward another operand");
//
//        expr.units = Arrays.asList(Mockito.mock(Function.class));
//        assertFalse(expr.isPushOperandAllowed(), "operands.Operand can't forward function");
//    }
//
//
//    @Test
//    void pushOperator() {
//        ExpressionBuilder expr = new ExpressionBuilder();
//        expr.pushOperand(Mockito.mock(OperandSupplier.class))
//            .pushOperator(Operator.class);
//
//        assertEquals(expr.getLastUnit(), Operator.class, "When push operator, it must be at the last position");
//    }
//
//
//    @Test
//    void pushOperand() {
//        ExpressionBuilder expr = new ExpressionBuilder();
//        Operand op = Mockito.mock(OperandSupplier.class);
//        expr.pushOperand(op);
//
//        assertEquals(expr.getLastUnit(), op, "When push operand, it must be at the last position");
//    }
//
//
//    @Test
//    void pushFunction() {
//        Function f = Mockito.mock(Function.class, Mockito.CALLS_REAL_METHODS);
//        Operand arg1 = Mockito.mock(OperandSupplier.class);
//        Operand arg2 = Mockito.mock(OperandSupplier.class);
//        f.args = Arrays.asList(arg1, arg2);
//
//        ExpressionBuilder expr = new ExpressionBuilder();
//        expr.pushFunction(f);
//
//        assertArrayEquals(
//            expr.getUnits().toArray(),
//            new Object[] {
//                f,
//                Parentheses.OPENING_PAREN,
//                    arg1,
//                    ExpressionBuilder.FUNCTION_ARGUMENT_SEPARATOR,
//                    arg2,
//                Parentheses.CLOSING_PAREN,
//            },
//            "When push operand, it must be at the last position"
//        );
//    }
//
//
//    @Test
//    void testPushGroupRecursively() {
//        Operand operand1 = Mockito.mock(OperandSupplier.class);
//        Operand operand2 = Mockito.mock(OperandSupplier.class);
//        Operand operand3 = Mockito.mock(OperandSupplier.class);
//        Operand operand4 = Mockito.mock(OperandSupplier.class);
//
//        ExpressionBuilder expr = new ExpressionBuilder()
//            .pushGroup(
//                new ExpressionBuilder()
//                    .pushOperand(operand1)
//                    .add(new ExpressionBuilder()
//                            .pushOperand(operand2)
//                            .add(operand3)
//                    )
//                    .add(operand4)
//            );
//
//        assertArrayEquals(
//            expr.getUnits().toArray(),
//            new Object[]{
//                Parentheses.OPENING_PAREN,
//                    operand1,
//                    AddOperator.class,
//                    Parentheses.OPENING_PAREN,
//                        operand2,
//                        AddOperator.class,
//                        operand3,
//                    Parentheses.CLOSING_PAREN,
//                    AddOperator.class,
//                    operand4,
//                Parentheses.CLOSING_PAREN
//            },
//            "Groups should be added recursively"
//        );
//    }
//
//
//    @Test
//    void push() {
//        Operand operand = Mockito.mock(OperandSupplier.class);
//        ExpressionBuilder e = new ExpressionBuilder()
//            .pushOperand(operand)
//            .push(AddOperator.class, operand);
//
//        assertEquals(AddOperator.class, e.getUnits().get(1),
//            "Should add operator to the units stack"
//        );
//        assertEquals(operand, e.getLastUnit(), "Should push operand to the stack");
//    }
//
//
//    @Test
//    void isTokenOperator() {
//        assertAll("Token is operator when it is subclass of operators.Operator.class",
//            () -> {
//                assertTrue(new ExpressionBuilder<>().isUnitOperator(AddOperator.class));
//                assertTrue(new ExpressionBuilder<>().isUnitOperator(Operator.class));
//            }
//        );
//    }
//
//
//    @Test
//    void pushGenericOperand() {
//        Integer test = new Integer(1);
//        assertTrue(new ExpressionBuilder<Integer>().pushOperand(test).getLastUnit() instanceof OperandSupplier,
//            "Generic type should be converted to operands.OperandSupplier"
//        );
//    }
}