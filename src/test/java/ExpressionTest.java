import operators.AddOperator;
import operators.Operator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


class ExpressionTest {
    @Test
    void testExpressionRedundantClosedParenthesis() {
        assertThrows(IllegalStateException.class,
            () -> new Expression<>()
                .openParenthesis()
                .closeParenthesis()
                .closeParenthesis(),
            "Should be unable to close paren when opened paren doesn't exist"
        );

        Expression expr = new Expression();
        expr.tokens = Arrays.asList(Parentheses.OPENING_PAREN, Mockito.mock(Operator.class));
        assertThrows(IllegalStateException.class,
            () -> expr.closeParenthesis(),
            "Close parenthesis can't be placed after operator"
        );
    }


    @Test
    void testPushOperatorIllegalState() {
        assertThrows(
            IllegalStateException.class,
            () -> new Expression<>().pushOperator(Operator.class),
            "Operator can't be at the first position in expression"
        );
        assertThrows(
            IllegalStateException.class,
            () -> {
                new Expression<>()
                    .pushOperand(Mockito.mock(OperandSupplier.class))
                    .pushOperator(Operator.class)
                    .pushOperator(Operator.class);
            },
            "Operators can't go after another operator"
        );
    }


    @Test
    void isPushOperandAllowed() {
        Expression expr = new Expression();

        expr.tokens = Arrays.asList(Parentheses.CLOSING_PAREN);
        assertFalse(expr.isPushOperandAllowed(), "Operand can't forward parenthesis");

        expr.tokens = Arrays.asList(Mockito.mock(Operand.class));
        assertFalse(expr.isPushOperandAllowed(), "Operand can't forward another operand");

        expr.tokens = Arrays.asList(Mockito.mock(Function.class));
        assertFalse(expr.isPushOperandAllowed(), "Operand can't forward function");
    }


    @Test
    void pushOperator() {
        Expression expr = new Expression();
        expr.pushOperand(Mockito.mock(OperandSupplier.class))
            .pushOperator(Operator.class);

        assertEquals(expr.getLastToken(), Operator.class, "When push operator, it must be at the last position");
    }


    @Test
    void pushOperand() {
        Expression expr = new Expression();
        Operand op = Mockito.mock(OperandSupplier.class);
        expr.pushOperand(op);

        assertEquals(expr.getLastToken(), op, "When push operand, it must be at the last position");
    }


    @Test
    void pushFunction() {
        Function f = Mockito.mock(Function.class, Mockito.CALLS_REAL_METHODS);
        Operand arg1 = Mockito.mock(OperandSupplier.class);
        Operand arg2 = Mockito.mock(OperandSupplier.class);
        f.args = Arrays.asList(arg1, arg2);

        Expression expr = new Expression();
        expr.pushFunction(f);

        assertArrayEquals(
            expr.getTokens().toArray(),
            new Object[] {
                f,
                Parentheses.OPENING_PAREN,
                    arg1,
                    Expression.FUNCTION_ARGUMENT_SEPARATOR,
                    arg2,
                Parentheses.CLOSING_PAREN,
            },
            "When push operand, it must be at the last position"
        );
    }


    @Test
    void testPushGroupRecursively() {
        Operand operand1 = Mockito.mock(OperandSupplier.class);
        Operand operand2 = Mockito.mock(OperandSupplier.class);
        Operand operand3 = Mockito.mock(OperandSupplier.class);
        Operand operand4 = Mockito.mock(OperandSupplier.class);

        Expression expr = new Expression()
            .pushGroup(
                new Expression()
                    .pushOperand(operand1)
                    .add(new Expression()
                            .pushOperand(operand2)
                            .add(operand3)
                    )
                    .add(operand4)
            );

        assertArrayEquals(
            expr.getTokens().toArray(),
            new Object[]{
                Parentheses.OPENING_PAREN,
                    operand1,
                    AddOperator.class,
                    Parentheses.OPENING_PAREN,
                        operand2,
                        AddOperator.class,
                        operand3,
                    Parentheses.CLOSING_PAREN,
                    AddOperator.class,
                    operand4,
                Parentheses.CLOSING_PAREN
            },
            "Groups should be added recursively"
        );
    }


    @Test
    void push() {
        Operand operand = Mockito.mock(OperandSupplier.class);
        Expression e = new Expression()
            .pushOperand(operand)
            .push(AddOperator.class, operand);

        assertEquals(AddOperator.class, e.getTokens().get(1),
            "Should add operator to the tokens stack"
        );
        assertEquals(operand, e.getLastToken(), "Should push operand to the stack");
    }


    @Test
    void isTokenOperator() {
        assertAll("Token is operator when it is subclass of Operator.class",
            () -> {
                assertTrue(new Expression<>().isTokenOperator(AddOperator.class));
                assertTrue(new Expression<>().isTokenOperator(Operator.class));
            }
        );
    }


    @Test
    void pushGenericOperand() {
        Integer test = new Integer(1);
        assertTrue(new Expression<Integer>().pushOperand(test).getLastToken() instanceof OperandSupplier,
            "Generic type should be converted to OperandSupplier"
        );
    }
}