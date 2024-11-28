package org.cubelogic.detection;

import org.cubelogic.trading.Order;
import org.cubelogic.trading.Trade;
import org.cubelogic.trading.TradeOrderMatch;
import org.cubelogic.trading.TradeOrderMatcher;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SuspiciousTradeService {
    private final TradeOrderMatcher matcher;
    private final SuspiciousOrderDetector detector;

    public SuspiciousTradeService(TradeOrderMatcher matcher, SuspiciousOrderDetector detector) {
        this.matcher = matcher;
        this.detector = detector;
    }

    public List<Trade> findSuspiciousTrades(List<Trade> trades, List<Order> orders) {
        return matcher.findMatches(trades, orders).stream()
                .filter(match -> !match.matchingOrders().isEmpty())
                .filter(match -> detector.isSuspiciousTradePattern(match.trade(), match.matchingOrders()))
                .map(TradeOrderMatch::trade)
                .collect(toList());
    }
}