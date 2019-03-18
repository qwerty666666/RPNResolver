package expression;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;


class ShuntingYardRPNConverterTest {
    @Test
    void testHandleOperandToken() {
        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(Mockito.mock(Map.class), Mockito.mock(Map.class));

        Double val = 3.;
        converter.handleOperandToken(val);

        assertAll(() -> {
            assertEquals(val, converter.RPNStack.peek(), "Operand should be pushed at the top of RPN stack");
            assertEquals(1, converter.RPNStack.size(), "RPNStack must contains only one element");
        });
    }


    @Test
    void testHandleOpeningParen() {
        ShuntingYardRPNConverter converter = new ShuntingYardRPNConverter<>(Mockito.mock(Map.class), Mockito.mock(Map.class));
        converter.handleOpeningParen();
        assertEquals(Parentheses.OPENING_PAREN, converter.operatorStack.peek(), "Paren should be pushed to the operator stack");
    }
}