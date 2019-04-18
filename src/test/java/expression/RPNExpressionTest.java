package expression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RPNExpressionTest {
    Object op1 = new Object();
    Object op2 = new Object();
    OperandSupplier os1 = new OperandSupplier(op1);
    OperandSupplier os2 = new OperandSupplier(op2);
    RPNExpression expr = mock(RPNExpression.class, CALLS_REAL_METHODS);


    @Test
    void operatorReplacedWithTheResultOfItsApplying() {
        List tokens = new ArrayList(Arrays.asList(os1, os2, mock(Operator.class)));

        Object oeResult = new Object();
        OperatorExecutor oe = mock(OperatorExecutor.class);
        doReturn(oeResult).when(oe).exec(op1, op2);

        expr.handleOperatorToken(tokens.listIterator(3), oe);

        verify(oe).exec(op1, op2);
        assertAll("Operator and two operands should be replaced with the result of operator applying",
            () -> assertEquals(1, tokens.size()),
            () -> assertEquals(new OperandSupplier<>(oeResult), tokens.get(0))
        );
    }


    @Test
    void throwsIllegalStateWhenThereIsLessThanTwoOperandsBeforeOperator() {
        List tokens = new ArrayList(Arrays.asList(os1, mock(Operator.class)));
        assertThrows(IllegalStateException.class, () -> expr.handleOperatorToken(tokens.listIterator(2), mock(OperatorExecutor.class)));
    }


    @Test
    void functionReplacedWithTheResultOfItsApplying() {
        Function f = mock(Function.class);
        doReturn(Arrays.asList(1)).when(f).getArgs();

        List tokens = new ArrayList(Arrays.asList(os1, f));

        Object feResult = new Object();
        FunctionExecutor fe = mock(FunctionExecutor.class);
        doReturn(feResult).when(fe).exec(op1);

        expr.handleFunctionToken(tokens.listIterator(2), fe);

        verify(fe).exec(op1);
        assertAll("Function and its arguments should be replaced with the result of its applying",
            () -> assertEquals(1, tokens.size()),
            () -> assertEquals(new OperandSupplier<>(feResult), tokens.get(0))
        );
    }


    @Test
    void throwsIllegalStateWhenThereIsNotEnoughOperandsBeforeFunction() {
        Function f = mock(Function.class);
        doReturn(Arrays.asList(0)).when(f).getArgs();

        List tokens = new ArrayList(Arrays.asList(f));
        assertThrows(IllegalStateException.class, () -> expr.handleFunctionToken(tokens.listIterator(1), mock(FunctionExecutor.class)));
    }


    @Test
    void throwsIllegalStateWhenTheResultingListSizeIsNotEqualToOne() {
        OperatorExecutorProvider oep = mock(OperatorExecutorProvider.class);
        FunctionExecutorProvider fep = mock(FunctionExecutorProvider.class);
        RPNExpression expr = new RPNExpression<>(new ArrayList(Arrays.asList(new OperandSupplier(0), new OperandSupplier(0))));
        assertThrows(IllegalStateException.class, () -> expr.calc(oep, fep));
    }
}