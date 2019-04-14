package expression;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import functions.Function;
import functions.FunctionExecutor;
import operands.OperandSupplier;
import operators.AddOperator;
import operators.DoubleOperator.DoubleAddOperator;
import operators.DoubleOperator.DoubleMultiplyOperator;
import operators.MultiplyOperator;
import operators.Operator;
import operators.OperatorAssociativity;
import org.junit.jupiter.api.*;
import providers.DoubleFunctionExecutorProvider;
import providers.DoubleOperatorProvider;
import providers.FunctionExecutorProvider;
import providers.OperatorProvider;

import java.util.ArrayList;
import java.util.Arrays;


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


    @Nested
    class HandleCLosingParenTokenTest {
        ShuntingYardRPNConverter converter;


        @BeforeEach
        void setUp() {
            converter = new ShuntingYardRPNConverter<>(mock(OperatorProvider.class), mock(FunctionExecutorProvider.class));
        }


        @Test
        void testPopAllUntilOpeningParen() {
            Operator op1 = mock(Operator.class);
            Operator op2 = mock(Operator.class);
            converter.operatorStack.push(op1);
            converter.operatorStack.push(Parentheses.OPENING_PAREN);
            converter.operatorStack.push(op2);

            converter.handleClosingParen();

            assertAll("All items before opening paren must remain in operatorStack", () -> {
                assertEquals(op1, converter.operatorStack.peek());
                assertEquals(1, converter.operatorStack.size());
            });
            assertAll("All items after opening paren must be popped into RPNStack", () -> {
                assertEquals(op2, converter.RPNStack.peek());
                assertEquals(1, converter.RPNStack.size());
            });
        }


        @Test
        void testFunctionBeforeOpeningParenPoppedIntoRPNStack() {
            FunctionExecutor fe = mock(FunctionExecutor.class);
            converter.operatorStack.push(fe);
            converter.operatorStack.push(Parentheses.OPENING_PAREN);

            converter.handleClosingParen();

            assertAll("Function before opening paren popped into RPNStack", () -> {
                assertEquals(fe, converter.RPNStack.peek());
                assertEquals(1, converter.RPNStack.size());
            });
        }


        @Test
        void testThrowsIllegalStateWhenThereIsNoOpeningParen() {
            assertThrows(IllegalStateException.class, () -> converter.handleClosingParen());
        }
    }


    @Test
    void testOperatorStackRest() {
        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(mock(OperatorProvider.class), mock(FunctionExecutorProvider.class));
        Operator op = mock(Operator.class);
        converter.operatorStack.push(op);

        converter.convert(new ArrayList<>());

        assertEquals(op, converter.RPNStack.peek(), "The rest of operatorStack should be pushed into RPNStack");
    }


    @Test
    void testHandleFunctionToken() {
        Function f = mock(Function.class);
        FunctionExecutor fe = mock(FunctionExecutor.class);
        FunctionExecutorProvider fep = mock(FunctionExecutorProvider.class);

        when(fep.get(any())).thenReturn(fe);

        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(mock(OperatorProvider.class), fep);
        converter.handleFunctionToken(f);

        verify(fep).get(f.getClass());
        assertAll(() -> {
            assertEquals(fe, converter.operatorStack.peek(), "Function executor should be pushed to the operator stack");
            assertEquals(1, converter.operatorStack.size(), "operatorStack must contains only one element");
        });
    }


    @Nested
    class HandleOperatorTokenTest {
        Operator operatorMock;
        OperatorProvider operatorProviderMock;


        @BeforeEach
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
        void testRemainOperatorWithGreaterPrecedence() {
            Operator op = mock(Operator.class);
            when(op.getPrecedence()).thenReturn(2);

            ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter(operatorProviderMock, mock(FunctionExecutorProvider.class));
            converter.operatorStack.push(op);
            converter.handleOperatorToken(Operator.class);

            assertAll(
                () -> assertAll("Operator should be pushed to stack", () -> {
                    assertEquals(2, converter.operatorStack.size());
                    assertEquals(operatorMock, converter.operatorStack.pop());
                    assertEquals(op, converter.operatorStack.pop());
                }),
                () -> assertEquals(0, converter.RPNStack.size(), "RPN Stack shouldn't contains any element")
            );
        }


        @Test
        void testPopOperatorWithLowerPrecedence() {
            Operator op = mock(Operator.class);
            when(op.getPrecedence()).thenReturn(0);

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
            when(op1.getPrecedence()).thenReturn(0);

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


    @Test
    void testPreserveOperatorsPrecedence() {
        ShuntingYardRPNConverter<Double> converter = new ShuntingYardRPNConverter<>(new DoubleOperatorProvider(), new DoubleFunctionExecutorProvider());
        RPNExpression<Double> expr = converter.convert(Arrays.asList(
            new OperandSupplier<>(1),
            AddOperator.class,
            new OperandSupplier<>(2),
            MultiplyOperator.class,
            new OperandSupplier<>(3)
        ));

        assertArrayEquals(
            new Object[] {
                new OperandSupplier<>(1),
                new OperandSupplier<>(2),
                new OperandSupplier<>(3),
                new DoubleMultiplyOperator(),
                new DoubleAddOperator()
            },
            expr.getTokens().toArray()
        );
    }
}