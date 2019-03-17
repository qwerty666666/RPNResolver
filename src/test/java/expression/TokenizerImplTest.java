package expression;

import expression.ExpressionBuilder;
import expression.ExpressionUtils;
import expression.Parentheses;
import expression.TokenizerImpl;
import functions.Function;
import functions.Pow;
import operands.Operand;
import operands.OperandSupplier;
import operators.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
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
        Function f = Mockito.mock(Function.class);
        Mockito.when(f.getArgs()).thenReturn(Arrays.asList(arg1, arg2));

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



    private void assertTokensEquals(Object[] expected, Object[] actual) {
        if (expected != actual) {
            assertEquals(expected.length, actual.length, "Arrays have different length");

            for(int i = 0; i < expected.length; i++) {
                Object expectedElement = expected[i];
                Object actualElement = actual[i];
                if (expectedElement != actualElement) {
                    if (actualElement instanceof Function) {
                        assertEquals(expectedElement, actualElement.getClass(),
                            "Functions at " + i + " position are different"
                        );
                    } else {
                        assertEquals(expectedElement, actualElement, "Elements at " + i + " position are different");
                    }
                }
            }
        }
    }


    @Test
    void testExpressions() {
        ExpressionBuilder eb = new ExpressionBuilder<Double>()
            .add(3.)
            .add(4.)
            .multiply(2.)
            .divide(new Pow<>(
                new Pow<>(
                    new ExpressionBuilder<Double>()
                        .add(1.)
                        .subtract(5.),
                    2
                ),
                3
            ));

        assertTokensEquals(
            new Object[] {
                new OperandSupplier<>(3.),
                AddOperator.class,
                new OperandSupplier<>(4.),
                MultiplyOperator.class,
                new OperandSupplier<>(2.),
                DivideOperator.class,
                Pow.class,
                    Parentheses.OPENING_PAREN,
                        Pow.class,
                        Parentheses.OPENING_PAREN,
                            Parentheses.OPENING_PAREN,
                                new OperandSupplier<>(1.),
                                SubtractOperator.class,
                                new OperandSupplier<>(5.),
                            Parentheses.CLOSING_PAREN,
                            Tokenizer.FUNCTION_ARGUMENT_SEPARATOR,
                            2.,
                        Parentheses.CLOSING_PAREN,
                        Tokenizer.FUNCTION_ARGUMENT_SEPARATOR,
                        3.,
                    Parentheses.CLOSING_PAREN
            },
            new TokenizerImpl().tokenize(eb).toArray()
        );
    }
}