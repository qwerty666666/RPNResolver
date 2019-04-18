package it;

import expression.*;
import expression.Function;
import expression.OperandSupplier;
import operators.AddOperator;
import operators.DivideOperator;
import operators.MultiplyOperator;
import operators.SubtractOperator;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("3 + 4 * 128 / ((1 - 3)^2)^3 == 11")
public class IntegrationTest {
    ExpressionBuilder<Double> eb;
    Function<Double> pow1;
    Function<Double> pow2;


    @BeforeAll
    void createExpression() {
        pow2 = new Pow<>(
            new ExpressionBuilder<Double>()
                .add(1.)
                .subtract(3.),
            2
        );
        pow1 = new Pow<>(pow2,3);
        eb = new ExpressionBuilder<Double>()
            .add(3.)
            .add(4.)
            .multiply(128.)
            .divide(pow1);
    }


    private void assertTokensEquals(Object[] expected, Object[] actual) {
        if (expected != actual) {
            assertEquals(expected.length, actual.length, "Arrays have different length");

            for (int i = 0; i < expected.length; i++) {
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


    List<Token> getTokens() {
        return new ExpressionBuilderTokenizer<>(eb).tokenize();
    }
    
    RPNExpression<Double> getRpnExpression() {
        return new ShuntingYardRPNConverter<Double>().convert(this.getTokens());
    }


    @Test
    @Order(1)
    @DisplayName("Tokenizer returns correct stack")
    void testExpression() {
        List<Token> tokens = this.getTokens();

        assertArrayEquals(
            new Object[]{
                new OperandSupplier<>(3.),
                new AddOperator<>(),
                new OperandSupplier<>(4.),
                new MultiplyOperator<>(),
                new OperandSupplier<>(128.),
                new DivideOperator<>(),
                pow1,
                Parentheses.OPENING_PAREN,
                    pow2,
                    Parentheses.OPENING_PAREN,
                        Parentheses.OPENING_PAREN,
                            new OperandSupplier<>(1.),
                            new SubtractOperator<>(),
                            new OperandSupplier<>(3.),
                        Parentheses.CLOSING_PAREN,
                        Tokenizer.FUNCTION_ARGUMENT_SEPARATOR,
                        new OperandSupplier<>(2.),
                    Parentheses.CLOSING_PAREN,
                    Tokenizer.FUNCTION_ARGUMENT_SEPARATOR,
                    new OperandSupplier<>(3.),
                Parentheses.CLOSING_PAREN
            },
            tokens.toArray()
        );
    }


    @Test
    @Order(2)
    @DisplayName("RPN returns correct stack")
    void testRPNResolver() {
        RPNExpression expr = getRpnExpression();

        assertArrayEquals(
            new Object[] {
                new OperandSupplier<>(3.),
                new OperandSupplier<>(4.),
                new OperandSupplier<>(128.),
                new MultiplyOperator(),
                new OperandSupplier<>(1.),
                new OperandSupplier<>(3.),
                new SubtractOperator(),
                new OperandSupplier<>(2.),
                pow2,
                new OperandSupplier<>(3.),
                pow1,
                new DivideOperator(),
                new AddOperator()
            },
            expr.getTokens().toArray()
        );
    }


    @Test
    @Order(3)
    @DisplayName("RPN evals correctly")
    void testRpnExpressionEval() {
        RPNExpression expr = getRpnExpression();
        assertEquals(11., expr.calc(new DoubleOperatorExecutorProvider(), new DoubleFunctionExecutorProvider()));
    }
}
