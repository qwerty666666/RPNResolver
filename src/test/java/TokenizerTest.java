import operands.Operand;
import operands.OperandSupplier;
import operators.AddOperator;
import operators.Operator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TokenizerTest {
    @Test
    void testPushOperator() {
        assertEquals(
            Operator.class,
            new Tokenizer()
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
            new Tokenizer()
                .getOperandSupplierTokens(operand)
                .toArray(),
            "OperandSupplier must be simply added"
        );
    }


    @Test
    void testGetFunctionTokens() {
        Function f = Mockito.mock(Function.class, Mockito.CALLS_REAL_METHODS);
        Operand arg1 = Mockito.mock(OperandSupplier.class);
        Operand arg2 = Mockito.mock(OperandSupplier.class);
        f.args = Arrays.asList(arg1, arg2);

        assertArrayEquals(
            new Tokenizer().getFunctionTokens(f).toArray(),
            new Object[] {
                f,
                Parentheses.OPENING_PAREN,
                    arg1,
                    Tokenizer.FUNCTION_ARGUMENT_SEPARATOR,
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

        List<Object> tokens = new Tokenizer().getGroupTokens(
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
                assertTrue(new ExpressionBuilder<>().isUnitOperator(AddOperator.class));
                assertTrue(new ExpressionBuilder<>().isUnitOperator(Operator.class));
            }
        );
    }


    @Test
    void testIsOperand() {
        assertTrue(
            new Tokenizer().isOperand(Mockito.mock(Operand.class)),
            "Token is operand when it extends Operand class"
        );
    }
}