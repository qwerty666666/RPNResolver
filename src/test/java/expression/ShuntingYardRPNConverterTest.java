package expression;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import functions.Function;
import functions.FunctionExecutor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;


class ShuntingYardRPNConverterTest {
    @Test
    void testHandleOperandToken() {
        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(mock(Map.class), mock(Map.class));

        Double val = 3.;
        converter.handleOperandToken(val);

        assertAll(() -> {
            assertEquals(val, converter.RPNStack.peek(), "Operand should be pushed at the top of RPN stack");
            assertEquals(1, converter.RPNStack.size(), "RPNStack must contains only one element");
        });
    }


    @Test
    void testHandleOpeningParen() {
        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(mock(Map.class), mock(Map.class));
        converter.handleOpeningParen();
        assertAll(() -> {
            assertEquals(Parentheses.OPENING_PAREN, converter.operatorStack.peek(), "Paren should be pushed to the operator stack");
            assertEquals(1, converter.operatorStack.size(), "operatorStack must contains only one element");
        });
    }



    @Test
    void testHandleFunctionToken() {
        ShuntingYardRPNConverter converter = mock(ShuntingYardRPNConverter.class, Mockito.CALLS_REAL_METHODS);

        FunctionExecutor fe = mock(FunctionExecutor.class);
        doReturn(fe).when(converter).getFunctionExecutor(any());

        converter.handleFunctionToken(mock(Function.class));

        verify(converter).getFunctionExecutor(Function.class);

        assertAll(() -> {
            assertEquals(fe, converter.operatorStack.peek(), "Function executor should be pushed to the operator stack");
            assertEquals(1, converter.operatorStack.size(), "operatorStack must contains only one element");
        });
    }
}