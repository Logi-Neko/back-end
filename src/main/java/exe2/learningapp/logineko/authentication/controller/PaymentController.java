package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public String createPayment(
            @RequestParam long orderCode,
            @RequestParam int amount,
            @RequestParam String description
    ) throws Exception {
        return paymentService.createPaymentLink(orderCode, amount, description);
    }
}
