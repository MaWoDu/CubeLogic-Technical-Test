package org.cubelogic.detection;

import org.cubelogic.trading.Order;
import org.cubelogic.trading.Trade;

import java.util.List;

public class SuspiciousOrderDetector {
    private static final double ALLOWED_PRICE_DIFFERENCE_THRESHOLD = 0.10;

    public boolean isSuspiciousTradePattern(Trade trade, List<Order> orders) {
        return orders.stream()
                .filter(order -> isOppositeSide(trade, order))
                .anyMatch(order -> isPriceWithinThreshold(trade, order));
    }

    private boolean isOppositeSide(Trade trade, Order order) {
        return trade.side() != order.side();
    }

    private boolean isPriceWithinThreshold(Trade trade, Order order) {
        double priceThreshold = Math.abs(trade.price() * ALLOWED_PRICE_DIFFERENCE_THRESHOLD);

        return switch (trade.side()) {
            case BUY -> isPriceWithinThresholdForBuy(trade, order, priceThreshold);
            case SELL -> isPriceWithinThresholdForSell(trade, order, priceThreshold);
        };
    }

    private boolean isPriceWithinThresholdForBuy(Trade trade, Order order, double threshold) {
        return order.price() - trade.price() >=  threshold;
    }

    private boolean isPriceWithinThresholdForSell(Trade trade, Order order, double threshold) {
        return trade.price() - order.price() >= threshold;
    }
}
