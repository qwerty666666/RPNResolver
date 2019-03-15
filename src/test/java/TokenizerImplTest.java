import expression.ExpressionUtils;
import functions.Function;
import operands.Operand;
import operands.OperandSupplier;
import operators.AddOperator;
import operators.Operator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TokenizerImplTest {
    @Test
    void testPushOperator() {
        assertEquals(
            Operator.class,
            new TokenizerImpl()
                .getOperatorTokens(Operator.class)
                .get(0),
            "When push operator, it must be at the last position"
        );
    }


    @Test
    void testGetOperandSupplierTokens() {
        OperandSupplier operand = Mockito.mock(OperandSupplier.class);

        assertArrayEquals(
            new Object[]{operand},
            new TokenizerImpl()
                .getOperandSupplierTokens(operand)
                .toArray(),
            "OperandSupplier must be simply added"
        );
    }


    @Test
    void testGetFunctionTokens() {
        Operand arg1 = Mockito.mock(OperandSupplier.class);
        Operand arg2 = Mockito.mock(OperandSupplier.class);
        Function f = new Function(arg1, arg2);

        assertArrayEquals(
            new TokenizerImpl().getFunctionTokens(f).toArray(),
            new Object[] {
                f,
                Parentheses.OPENING_PAREN,
                    arg1,
                    TokenizerImpl.FUNCTION_ARGUMENT_SEPARATOR,
                    arg2,
                Parentheses.CLOSING_PAREN,
            },
            "When push operand, it must be at the last position"
        );
    }


    @Test
    void testGetGroupTokensRecursively() {
        Operand operand1 = Mockito.mock(OperandSupplier.class);
        Operand operand2 = Mockito.mock(OperandSupplier.class);
        Operand operand3 = Mockito.mock(OperandSupplier.class);
        Operand operand4 = Mockito.mock(OperandSupplier.class);

        List<Object> tokens = new TokenizerImpl().getGroupTokens(
                new ExpressionBuilder()
                    .pushOperand(operand1)
                    .add(new ExpressionBuilder()
                            .pushOperand(operand2)
                            .add(operand3)
                    )
                    .add(operand4)
            );

        assertArrayEquals(
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
            tokens.toArray(),
            "Groups should be added recursively"
        );
    }


    @Test
    void testIsOperator() {
        assertAll("Token is operator when it is subclass of Operator.class",
            () -> {
                assertTrue(ExpressionUtils.isTokenOperator(AddOperator.class));
                assertTrue(ExpressionUtils.isTokenOperator(Operator.class));
            }
        );
    }



    @Test
    void testExpressions() {
        new ExpressionBuilder<Integer>()
            .add(3)
            .add(4)
            .multiply(2)
            .divide(new ExpressionBuilder<Integer>()
                .add(1)
                .subtract(5)
            );
    }
}