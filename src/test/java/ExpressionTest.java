import operators.AddOperator;
import operators.DoubleOperator.DoubleAddOperator;
import operators.Operator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;

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
        Operator operatorMock = Mockito.mock(Operator.class);
        Operand operandMock = Mockito.mock(Operand.class);

        assertThrows(
            IllegalStateException.class,
            () -> new Expression<>().pushOperator(operatorMock),
            "Operator can't be at the first position in expression"
        );
        assertThrows(
            IllegalStateException.class,
            () -> {
                new Expression<>()
                    .pushOperand(operandMock)
                    .pushOperator(operatorMock)
                    .pushOperator(operatorMock);
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
        Operator op = Mockito.mock(Operator.class);
        expr.pushOperand(Mockito.mock(Operand.class))
            .pushOperator(op);

        assertEquals(expr.getLastToken(), op, "When push operator, it must be at the last position");
    }


    @Test
    void pushOperand() {
        Expression expr = new Expression();
        Operand op = Mockito.mock(Operand.class);
        expr.pushOperand(op);

        assertEquals(expr.getLastToken(), op, "When push operand, it must be at the last position");
    }


    @Test
    void testPushFunction() {
        Function f = Mockito.mock(Function.class, Mockito.CALLS_REAL_METHODS);
        Operand arg1 = Mockito.mock(Operand.class);
        Operand arg2 = Mockito.mock(Operand.class);
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
        Operand operand1 = Mockito.mock(Operand.class);
        Operand operand2 = Mockito.mock(Operand.class);
        Operand operand3 = Mockito.mock(Operand.class);
        Operand operand4 = Mockito.mock(Operand.class);
        Operator operator1 = Mockito.mock(Operator.class);
        Operator operator2 = Mockito.mock(Operator.class);
        Operator operator3 = Mockito.mock(Operator.class);

        Expression expr = new Expression()
            .pushGroup(
                new Expression()
                    .pushOperand(operand1)
                    .pushOperator(operator1)
                    .pushGroup(
                        new Expression()
                            .pushOperand(operand2)
                            .pushOperator(operator2)
                            .pushOperand(operand3)
                    )
                    .pushOperator(operator3)
                    .pushOperand(operand4)
            );

        assertArrayEquals(
            expr.getTokens().toArray(),
            new Object[]{
                Parentheses.OPENING_PAREN,
                    operand1,
                    operator1,
                    Parentheses.OPENING_PAREN,
                        operand2,
                        operator2,
                        operand3,
                    Parentheses.CLOSING_PAREN,
                    operator3,
                    operand4,
                Parentheses.CLOSING_PAREN
            },
            "Groups should be added recursively"
        );
    }


    @Test
    void testUnspecifiedOperator() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Expression().getOperatorInstance(AddOperator.class),
            "Must throw exception when Operator implementation is not specified"
        );
    }


    @Test
    void push() {
        Operand operand = Mockito.mock(Operand.class);
        Expression e = new Expression(new HashMap() {{
            put(AddOperator.class, DoubleAddOperator.class);
        }})
            .pushOperand(operand)
            .push(AddOperator.class, operand);

        assertTrue(e.getTokens().get(1) instanceof DoubleAddOperator,
            "Should create operator implementation and add it to tokens stack"
        );
        assertEquals(operand, e.getLastToken(), "Should push operand to the stack");
    }
}