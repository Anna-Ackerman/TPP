package hello;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mariuszgromada.math.mxparser.Expression;

class CalculatorControllerTest {

    @Test
    void testCorrectExpression() {
        // Тест, що має пройти
        Expression expression = new Expression("1+2*2");
        double result = expression.calculate();
        assertEquals(5, result, "1+2*2 should return 5");
    }

//    @Test
//    void testIncorrectExpression() {
//        // Тест, що має не пройти
//        Expression expression = new Expression("5-1");
//        double result = expression.calculate();
//        assertEquals(2, result, "5-1 should return 4, not 2");
//    }
}