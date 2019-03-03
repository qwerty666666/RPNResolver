package operators;

/**
 * Associativity of mathematical operators.
 */
public enum OperatorAssociativity {
    /**
     * Operations grouped from the left
     * e.g. 7 − 4 + 2 => (7 − 4) + 2
     */
    LEFT_ASSOCIATIVE,
    /**
     * Operations grouped from the right
     * e.g. 7 − 4 + 2 => 7 − (4 + 2)
     */
    RIGHT_ASSOCIATIVE
}
