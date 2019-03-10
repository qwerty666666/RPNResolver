package expression;

import operands.Operand;
import operators.AddOperator;
import operators.Operator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionUtilsTest {

    @Test
    void isTokenOperator() {
        assertAll("Token is operator when it is subclass of Operator.class",
            () -> {
                assertTrue(ExpressionUtils.isTokenOperator(AddOperator.class));
                assertTrue(ExpressionUtils.isTokenOperator(Operator.class));
            }
        );
    }

    @Test
    void isTokenOperand() {
        assertTrue(
            ExpressionUtils.isTokenOperand(Mockito.mock(Operand.class)),
            "Token is operand when it extends Operand class"
        );
    }
}