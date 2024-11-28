import org.cubelogic.detection.SuspiciousOrderDetector;
import org.cubelogic.trading.Order;
import org.cubelogic.trading.Side;
import org.cubelogic.trading.Trade;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        Trade buyTrade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = of(
                new Order(1L, 111.0, 1.0, Side.SELL, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(buyTrade, orders);

        assertTrue(isSuspicious);
    }

    @Test
    void shouldIdentifySuspiciousPatternForBuyTradeDecimal() {
        Trade buyTrade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = of(
                new Order(1L, 110.001, 1.0, Side.SELL, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(buyTrade, orders);

        assertTrue(isSuspicious);
    }

    @Test
    void shouldIdentifySuspiciousPatternForSellTrade() {
        Trade sellTrade = new Trade(1L, 100.0, 1.0, Side.SELL, tradeTime);

        List<Order> orders = of(
                new Order(1L, 89, 1.0, Side.BUY, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(sellTrade, orders);

        assertTrue(isSuspicious);
    }

    @Test
    void shouldIdentifySuspiciousPatternForSellTradeDecimal() {
        Trade sellTrade = new Trade(1L, 100.0, 1.0, Side.SELL, tradeTime);

        List<Order> orders = of(
                new Order(1L, 89.999, 1.0, Side.BUY, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(sellTrade, orders);

        assertTrue(isSuspicious);
    }

    @Test
    void shouldNotBeQuestionableSellWhenPriceOutsideThreshold() {
        Trade sellTrade = new Trade(1L, 100.0, 1.0, Side.SELL, tradeTime);
        List<Order> orders = of(
                new Order(1L, 109.0, 1.0, Side.BUY, orderTime),
                new Order(1L, 108.0, 1.0, Side.BUY, orderTime),
                new Order(1L, 107.0, 1.0, Side.BUY, orderTime),
                new Order(1L, 106.0, 1.0, Side.BUY, orderTime),
                new Order(1L, 105.0, 1.0, Side.BUY, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(sellTrade, orders);

        assertFalse(isSuspicious);
    }

    @Test
    void shouldNotBeQuestionableBuyWhenPriceOutsideThreshold() {
        Trade buyTrade = new Trade(1L, 100.0, 1.0, Side.SELL, tradeTime);
        List<Order> orders = of(
                new Order(1L, 99.0, 1.0, Side.SELL, orderTime),
                new Order(1L, 98.0, 1.0, Side.SELL, orderTime),
                new Order(1L, 97.0, 1.0, Side.SELL, orderTime),
                new Order(1L, 96.0, 1.0, Side.SELL, orderTime),
                new Order(1L, 95.0, 1.0, Side.SELL, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(buyTrade, orders);

        assertFalse(isSuspicious);
    }

    @Test
    void shouldNotBeQuestionableWithEmptyOrders() {
        Trade buyTrade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);
        boolean isSuspicious = detector.isSuspiciousTradePattern(buyTrade, Collections.emptyList());
        assertFalse(isSuspicious);
    }

    @Test
    void shouldBeQuestionableIfAtLeastOneOrderMatchesPattern() {
        Trade buyTrade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);
        List<Order> orders = of(
                new Order(1L, 120.0, 1.0, Side.SELL, orderTime),
                new Order(2L, 105.0, 1.0, Side.SELL, orderTime)
        );

        boolean isSuspicious = detector.isSuspiciousTradePattern(buyTrade, orders);

        assertTrue(isSuspicious);
    }
}