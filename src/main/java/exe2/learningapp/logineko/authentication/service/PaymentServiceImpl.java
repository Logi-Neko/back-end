package exe2.learningapp.logineko.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PayOS payOS;

    @Value("${payos.success-page}")
    private String successPage;

    @Value("${payos.failure-page}")
    private String failurePage;

    @Override
    public String createPaymentLink(long orderCode, int amount, String description) throws Exception {
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .returnUrl(successPage)
                .cancelUrl(failurePage)
                .build();

        CheckoutResponseData data = payOS.createPaymentLink(paymentData);

        return data.getCheckoutUrl();
    }

}
