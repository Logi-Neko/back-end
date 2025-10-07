package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.entity.SubscriptionPrice;
import exe2.learningapp.logineko.authentication.repository.SubscriptionPriceRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPriceServiceImpl implements SubscriptionPriceService {
    private final SubscriptionPriceRepository subscriptionPriceRepository;

    @Override
    @Transactional
    public SubscriptionPrice add(Long price, Long duration) {
        return subscriptionPriceRepository.save(
                SubscriptionPrice.builder()
                        .price(price)
                        .duration(duration)
                        .build()
        );
    }

    @Override
    @Transactional
    public SubscriptionPrice update(Long id, Long price, Long duration) {
        SubscriptionPrice subscriptionPrice = subscriptionPriceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        subscriptionPrice.setPrice(price);
        subscriptionPrice.setDuration(duration);
        return subscriptionPriceRepository.save(subscriptionPrice);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SubscriptionPrice subscriptionPrice = subscriptionPriceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));
        subscriptionPriceRepository.delete(subscriptionPrice);
    }

    @Override
    public List<SubscriptionPrice> findAll() {
        return subscriptionPriceRepository.findAll();
    }
}
