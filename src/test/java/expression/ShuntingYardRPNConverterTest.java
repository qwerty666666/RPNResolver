package expression;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import functions.Function;
import functions.FunctionExecutor;
import operators.Operator;
import operators.OperatorAssociativity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import providers.FunctionExecutorProvider;
import providers.OperatorProvider;



class ShuntingYardRPNConverterTest {
    @Test
    void testHandleOperandToken() {
        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(mock(OperatorProvider.class), mock(FunctionExecutorProvider.class));

        Double val = 3.;
        converter.handleOperandToken(val);

        assertAll(() -> {
            assertEquals(val, converter.RPNStack.peek(), "Operand should be pushed at the top of RPN stack");
            assertEquals(1, converter.RPNStack.size(), "RPNStack must contains only one element");
        });
    }


    @Test
    void testHandleOpeningParen() {
        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(mock(OperatorProvider.class), mock(FunctionExecutorProvider.class));
        converter.handleOpeningParen();
        assertAll(() -> {
            assertEquals(Parentheses.OPENING_PAREN, converter.operatorStack.peek(), "Paren should be pushed to the operator stack");
            assertEquals(1, converter.operatorStack.size(), "operatorStack must contains only one element");
        });
    }



    @Test
    void testHandleClosingParen() {
        throw new RuntimeException("TODO");
    }


    @Test
    void testHandleFunctionToken() {
        Function f = mock(Function.class);
        FunctionExecutor fe = mock(FunctionExecutor.class);
        FunctionExecutorProvider fep = mock(FunctionExecutorProvider.class);

        when(fep.get(any())).thenReturn(fe);

        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(mock(OperatorProvider.class), fep);
        converter.handleFunctionToken(f);

        verify(fep).get(Function.class);
        assertAll(() -> {
            assertEquals(fe, converter.operatorStack.peek(), "Function executor should be pushed to the operator stack");
            assertEquals(1, converter.operatorStack.size(), "operatorStack must contains only one element");
        });
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class HandleOperatorTokenTest {
        Operator operatorMock;
        OperatorProvider operatorProviderMock;


        @BeforeAll
        void setUp() {
            operatorMock = mock(Operator.class);
            when(operatorMock.getPrecedence()).thenReturn(1);
            when(operatorMock.getAssociativity()).thenReturn(OperatorAssociativity.LEFT_ASSOCIATIVE);

            operatorProviderMock = mock(OperatorProvider.class);
            when(operatorProviderMock.get(any())).thenReturn(operatorMock);
        }


        void testPopToken(ShuntingYardRPNConverter converter, Object topOperator, Operator operator) {
            converter.operatorStack.push(topOperator);
            converter.handleOperatorToken(Operator.class);

            assertAll(
                () -> assertEquals(operator, converter.operatorStack.peek(), "Operator should be pushed to the operator stack"),
                () -> assertEquals(1, converter.operatorStack.size(), "operatorStack must contains only one element"),
                () -> assertEquals(topOperator, converter.RPNStack.peek(), "Operator on the top of operator stack must be popped into RPN stack"),
                () -> assertEquals(1, converter.RPNStack.size(), "RPN Stack must contains only one element")
            );
        }

        @Test
        void testPopFunctionExecutor() {
            FunctionExecutor fe = mock(FunctionExecutor.class);
            FunctionExecutorProvider fep = mock(FunctionExecutorProvider.class);
            when(fep.get(any())).thenReturn(fe);

            ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter(operatorProviderMock, fep);

            testPopToken(converter, fe, operatorMock);
        }


        @Test
        void testPopOperatorWithGreaterPrecedence() {
            Operator op = mock(Operator.class);
            when(op.getPrecedence()).thenReturn(2);

            ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter(operatorProviderMock, mock(FunctionExecutorProvider.class));

            testPopToken(converter, op, operatorMock);
        }


        @Test
        void testPopOperatorWithEqualPrecedenceAndLeftAssociativity() {
            Operator op = mock(Operator.class);
            when(op.getPrecedence()).thenReturn(1);
            when(op.getAssociativity()).thenReturn(OperatorAssociativity.LEFT_ASSOCIATIVE);

            ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter(operatorProviderMock, mock(FunctionExecutorProvider.class));

            testPopToken(converter, op, operatorMock);
        }


        @Test
        void testBreakOnLeftParen() {
            Operator op1 = mock(Operator.class);
            when(op1.getPrecedence()).thenReturn(2);

            ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter(operatorProviderMock, mock(FunctionExecutorProvider.class));
            converter.operatorStack.push(Parentheses.OPENING_PAREN);
            converter.operatorStack.push(op1);

            converter.handleOperatorToken(Operator.class);

            assertAll(
                () -> assertEquals(op1, converter.RPNStack.peek(), "Operator on the top of operator stack must be popped into RPN stack"),
                () -> assertEquals(1, converter.RPNStack.size(), "RPN Stack must contains only one element"),
                () -> assertEquals(operatorMock, converter.operatorStack.peek(), "Operator should be pushed to the operator stack"),
                () -> assertAll(() -> {
                    assertEquals(operatorMock, converter.operatorStack.pop(), "Operator must be pushed to stack");
                    assertEquals(Parentheses.OPENING_PAREN, converter.operatorStack.pop(), "Left paren must remain in stack");
                })
            );
        }
    }
}