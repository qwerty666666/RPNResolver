package expression;

import operators.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ExpressionBuilderTokenizerTest {
    private ExpressionBuilderTokenizer<Object> ebtMock;


    @BeforeEach
    void setUp() {
        ebtMock = mock(ExpressionBuilderTokenizer.class, InvocationOnMock::callRealMethod);
    }


    @Test
    void testPushOperator() {
        Operator op = mock(Operator.class);

        assertEquals(
            op,
            ebtMock.getOperatorTokens(op).get(0),
            "When push operator, it must be at the last position"
        );
    }


    @Test
    void testGetOperandSupplierTokens() {
        OperandSupplier<Object> operand = mock(OperandSupplier.class);

        assertArrayEquals(
            new Object[]{ operand },
            ebtMock.getOperandSupplierTokens(operand).toArray(),
            "OperandSupplier must be simply added"
        );
    }


    @Test
    void testGetFunctionTokens() {
        Operand arg1 = mock(OperandSupplier.class);
        Operand arg2 = mock(OperandSupplier.class);
        Function<Object> f = mock(Function.class);
        when(f.getArgs()).thenReturn(Arrays.asList(arg1, arg2));

        assertArrayEquals(
            ebtMock.getFunctionTokens(f).toArray(),
            new Object[] {
                f,
                Parentheses.OPENING_PAREN,
                    arg1,
                    ExpressionBuilderTokenizer.FUNCTION_ARGUMENT_SEPARATOR,
                    arg2,
                Parentheses.CLOSING_PAREN,
            },
            "When push operand, it must be at the last position"
        );
    }


    @Test
    void testGetGroupTokensRecursively() {
        Operand operand1 = mock(OperandSupplier.class);
        Operand operand2 = mock(OperandSupplier.class);
        Operand operand3 = mock(OperandSupplier.class);
        Operand operand4 = mock(OperandSupplier.class);

        List<Token> tokens = ebtMock.getGroupTokens(
                new ExpressionBuilder<>()
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
                    new AddOperator<>(),
                    Parentheses.OPENING_PAREN,
                        operand2,
                        new AddOperator<>(),
                        operand3,
                    Parentheses.CLOSING_PAREN,
                    new AddOperator<>(),
                    operand4,
                Parentheses.CLOSING_PAREN
            },
            tokens.toArray(),
            "Groups should be added recursively"
        );
    }
}