import java.util.List;

public record TradeOrderMatch(
        Trade trade,
        List<Order> matchingOrders
) {
}