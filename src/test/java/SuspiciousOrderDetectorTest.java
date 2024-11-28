import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SuspiciousOrderDetectorTest {
    private SuspiciousOrderDetector detector;
    private LocalDateTime tradeTime;
    private LocalDateTime orderTime;

    @BeforeEach
    void setUp() {
        detector = new SuspiciousOrderDetector();
        tradeTime = LocalDateTime.now();
        orderTime = LocalDateTime.now();
    }

    @Test
    void shouldIdentifySuspiciousPatternForBuyTrade() {
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = of(
                new Order(1L, 105.0, 1.0, Side.SELL, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(trade, orders);

        assertTrue(isSuspicious);
    }
}