package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.entity.SubscriptionPrice;
import exe2.learningapp.logineko.authentication.service.SubscriptionPriceService;
import exe2.learningapp.logineko.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription_prices")
@RequiredArgsConstructor
public class SubscriptionPriceController {
    private final SubscriptionPriceService subscriptionPriceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPrice>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success(subscriptionPriceService.findAll())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPrice>> add(
            @RequestParam Long price,
            @RequestParam Long duration
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(subscriptionPriceService.add(price, duration))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPrice>> update(
            @PathVariable Long id,
            @RequestParam Long price,
            @RequestParam Long duration
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(subscriptionPriceService.update(id, price, duration))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        subscriptionPriceService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Success")
        );
    }
}
