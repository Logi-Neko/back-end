package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.entity.SubscriptionPrice;

import java.util.List;

public interface SubscriptionPriceService {
    SubscriptionPrice add(Long price, Long duration);

    SubscriptionPrice update(Long id, Long price, Long duration);

    void delete(Long id);

    List<SubscriptionPrice> findAll();
}
