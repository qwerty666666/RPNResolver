package it;

import expression.*;
import functions.Function;
import functions.Pow;
import operands.OperandSupplier;
import operators.AddOperator;
import operators.DivideOperator;
import operators.MultiplyOperator;
import operators.SubtractOperator;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("3 + 4 * 128 / ((1 - 3)^2)^3 == 19")
public class IntegrationTest {
    ExpressionBuilder eb;
    List<Object> tokens;


    @BeforeEach
    void createExpression() {
        eb = new ExpressionBuilder<Double>()
            .add(3.)
            .add(4.)
            .multiply(128.)
            .divide(new Pow<>(
                new Pow<>(
                    new ExpressionBuilder<Double>()
                        .add(1.)
                        .subtract(3.),
                    2
                ),
                3
            ));
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


    @Test
    @Order(1)
    @DisplayName("Tokenizer returns correct stack")
    void testExpression() {
        tokens = new TokenizerImpl<Double>().tokenize(eb);

        assertTokensEquals(
            new Object[]{
                new OperandSupplier<>(3.),
                AddOperator.class,
                new OperandSupplier<>(4.),
                MultiplyOperator.class,
                new OperandSupplier<>(128.),
                DivideOperator.class,
                Pow.class,
                Parentheses.OPENING_PAREN,
                    Pow.class,
                    Parentheses.OPENING_PAREN,
                        Parentheses.OPENING_PAREN,
                            new OperandSupplier<>(1.),
                            SubtractOperator.class,
                            new OperandSupplier<>(3.),
                        Parentheses.CLOSING_PAREN,
                        Tokenizer.FUNCTION_ARGUMENT_SEPARATOR,
                        2.,
                    Parentheses.CLOSING_PAREN,
                    Tokenizer.FUNCTION_ARGUMENT_SEPARATOR,
                    3.,
                Parentheses.CLOSING_PAREN
            },
            tokens.toArray()
        );
    }


    @Test
    @Order(2)
    @DisplayName("RPN has correct stack")
    void testRPNResolver() {
        RPNExpression expr = new ShuntingYardRPNConverter<>(ExpressionUtils.DOUBLE_OPERATORS_MAP, ExpressionUtils.DOUBLE_FUNCTIONS_MAP)
            .convert(tokens);

        assertArrayEquals(
            new Object[] {

            },
            expr.getTokens().toArray()
        );
    }
}
