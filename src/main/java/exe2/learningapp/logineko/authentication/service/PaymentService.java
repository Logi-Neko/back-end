package exe2.learningapp.logineko.authentication.service;

public interface PaymentService {
    String createPaymentLink(long orderCode, int amount, String description) throws Exception;
}
