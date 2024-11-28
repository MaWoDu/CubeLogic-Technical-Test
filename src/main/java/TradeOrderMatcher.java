import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class TradeOrderMatcher {

    private static final Duration TIME_WINDOW = Duration.ofMinutes(30);

    public List<TradeOrderMatch> findMatches(List<Trade> trades, List<Order> orders) {
        return trades.stream()
                .map(trade -> new TradeOrderMatch(
                        trade,
                        findMatchingOrders(trade, orders)
                ))
                .collect(Collectors.toList());
    }

    private List<Order> findMatchingOrders(Trade trade, List<Order> orders) {
        return orders.stream()
                .filter(order -> order.id() == trade.id())
                .filter(order -> isWithinTimeWindow(trade, order))
                .collect(Collectors.toList());
    }

    private boolean isWithinTimeWindow(Trade trade, Order order) {
        Duration timeDifference = Duration.between(order.timestamp(), trade.timestamp());
        return !timeDifference.isNegative() &&
                !timeDifference.isZero() &&
                timeDifference.compareTo(TIME_WINDOW) <= 0;
    }
}