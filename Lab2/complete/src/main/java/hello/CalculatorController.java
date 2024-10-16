package hello;

import org.mariuszgromada.math.mxparser.Expression;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculatorController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/calculate")
    public String calculate(@RequestParam String expression, Model model) {
        try {
            Expression e = new Expression(expression);
            double result = e.calculate();

            model.addAttribute("result", result);
            return "result";
        } catch (Exception e) {
            model.addAttribute("error", "Error: invalid expression");
            return "result";
        }
    }
}
