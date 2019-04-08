package expression;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import functions.Function;
import functions.FunctionExecutor;
import operators.Operator;
import operators.OperatorAssociativity;
import org.junit.jupiter.api.Test;
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


    @Test
    void testHandleOperatorToken() {
        Operator op = mock(Operator.class);
        when(op.getPrecedence()).thenReturn(1);
        when(op.getAssociativity()).thenReturn(OperatorAssociativity.LEFT_ASSOCIATIVE);

        OperatorProvider operatorProvider = mock(OperatorProvider.class);
        when(operatorProvider.get(any())).thenReturn(op);

        FunctionExecutor fe = mock(FunctionExecutor.class);

        ShuntingYardRPNConverter converter;

        converter = new ShuntingYardRPNConverter(operatorProvider, mock(FunctionExecutorProvider.class));
        converter.operatorStack.push(fe);
        converter.handleOperatorToken(Operator.class);

        assertAll(() -> {
            assertEquals(op, converter.operatorStack.peek(), "Operator should be pushed to the operator stack");
            assertEquals(1, converter.operatorStack.size(), "operatorStack must contains only one element");
            assertEquals(fe, converter.RPNStack.peek(), "FunctionExecutor must be popped into RPN stack");
            assertEquals(1, converter.RPNStack.peek(), "RPN Stack must contains only one element");
        });

//        Operator op1 = mock(Operator.class);
//        when(op1.getAssociativity()).thenReturn(OperatorAssociativity.LEFT_ASSOCIATIVE);
//        when(op1.getPrecedence()).thenReturn(2);
//        converter.operatorStack.push(mock(Function.class));
    }
}