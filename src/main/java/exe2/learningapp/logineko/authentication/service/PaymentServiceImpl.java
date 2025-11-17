package exe2.learningapp.logineko.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
//import vn.payos.type.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;


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
        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount((long) amount)
                .description(description)
                .returnUrl(successPage)
                .cancelUrl(failurePage)
                .item(PaymentLinkItem.builder()
                        .name(description)
                        .price((long) amount)
                        .quantity(1)
                        .build())
                .build();

        CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);

        return data.getCheckoutUrl();
    }

}
