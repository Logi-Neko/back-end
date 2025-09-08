package exe2.learningapp.logineko.authentication.service;

public interface SubscriptionService {
    void subscribeUserToPremium(String username);
    void unsubscribeUserFromPremium(String username);
    boolean isUserPremium(String username);

}
