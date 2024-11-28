import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

class TradeOrderMatcherTest {
    private TradeOrderMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new TradeOrderMatcher();
    }

    @Test
    void shouldMatchOrdersBasedOnId() {
        LocalDateTime tradeTime = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = List.of(
                new Order(1L, 105.0, 1.0, Side.SELL, tradeTime.minusMinutes(15)),
                new Order(2L, 105.0, 1.0, Side.SELL, tradeTime.minusMinutes(15))
        );

        List<TradeOrderMatch> matches = matcher.findMatches(List.of(trade), orders);

        assertThat(matches)
                .hasSize(1)
                .element(0)
                .satisfies(match -> {
                    assertThat(match.trade()).isEqualTo(trade);
                    assertThat(match.matchingOrders()).containsExactly(orders.get(0));
                });
    }

    @Test
    void shouldMatchOrdersBasedOnTime() {
        LocalDateTime tradeTime = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = List.of(
                new Order(1L, 105.0, 1.0, Side.SELL, tradeTime.minusMinutes(30)),
                new Order(1L, 105.0, 1.0, Side.SELL, tradeTime.minusMinutes(31))
        );

        List<TradeOrderMatch> matches = matcher.findMatches(List.of(trade), orders);

        assertThat(matches)
                .hasSize(1)
                .element(0)
                .satisfies(match -> {
                    assertThat(match.trade()).isEqualTo(trade);
                    assertThat(match.matchingOrders()).containsExactly(orders.get(0));
                });
    }

    @Test
    void shouldMatchMultipleOrdersForTrade() {
        LocalDateTime tradeTime = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = List.of(
                new Order(1L, 105.0, 1.0, Side.SELL, tradeTime.minusMinutes(15)),
                new Order(1L, 106.0, 1.0, Side.SELL, tradeTime.minusMinutes(20))
        );

        List<TradeOrderMatch> matches = matcher.findMatches(List.of(trade), orders);

        assertThat(matches)
                .hasSize(1)
                .element(0)
                .satisfies(match -> {
                    assertThat(match.trade()).isEqualTo(trade);
                    assertThat(match.matchingOrders()).containsExactlyElementsOf(orders);
                });
    }

    @Test
    void shouldNotMatchOrdersOutsideTimeWindow() {
        LocalDateTime tradeTime = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = List.of(
                new Order(1L, 105.0, 1.0, Side.SELL, tradeTime.minusMinutes(31))
        );

        List<TradeOrderMatch> matches = matcher.findMatches(List.of(trade), orders);

        assertThat(matches)
                .hasSize(1)
                .element(0)
                .satisfies(match -> {
                    assertThat(match.trade()).isEqualTo(trade);
                    assertThat(match.matchingOrders()).isEmpty();
                });
    }

    @Test
    void shouldNotMatchOrdersWithDifferentId() {
        LocalDateTime tradeTime = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<Order> orders = List.of(
                new Order(2L, 105.0, 1.0, Side.SELL, tradeTime.minusMinutes(15))
        );

        List<TradeOrderMatch> matches = matcher.findMatches(List.of(trade), orders);

        assertThat(matches)
                .hasSize(1)
                .element(0)
                .satisfies(match -> {
                    assertThat(match.trade()).isEqualTo(trade);
                    assertThat(match.matchingOrders()).isEmpty();
                });
    }

    @Test
    void shouldMatchOrdersForMultipleTrades() {
        LocalDateTime time = LocalDateTime.now();
        List<Trade> trades = List.of(
                new Trade(1L, 100.0, 1.0, Side.BUY, time),
                new Trade(2L, 200.0, 2.0, Side.SELL, time)
        );

        List<Order> orders = List.of(
                new Order(1L, 105.0, 1.0, Side.SELL, time.minusMinutes(15)),
                new Order(2L, 195.0, 2.0, Side.BUY, time.minusMinutes(20))
        );

        List<TradeOrderMatch> matches = matcher.findMatches(trades, orders);

        assertThat(matches)
                .hasSize(2)
                .satisfies(matchList -> {
                    TradeOrderMatch firstMatch = matchList.get(0);
                    TradeOrderMatch secondMatch = matchList.get(1);

                    assertThat(firstMatch.trade()).isEqualTo(trades.get(0));
                    assertThat(firstMatch.matchingOrders()).containsExactly(orders.get(0));

                    assertThat(secondMatch.trade()).isEqualTo(trades.get(1));
                    assertThat(secondMatch.matchingOrders()).containsExactly(orders.get(1));
                });
    }

    @Test
    void shouldHandleEmptyOrdersList() {
        LocalDateTime tradeTime = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, tradeTime);

        List<TradeOrderMatch> matches = matcher.findMatches(List.of(trade), Collections.emptyList());

        assertThat(matches)
                .hasSize(1)
                .element(0)
                .satisfies(match -> {
                    assertThat(match.trade()).isEqualTo(trade);
                    assertThat(match.matchingOrders()).isEmpty();
                });
    }

    @Test
    void shouldHandleEmptyTradesList() {
        LocalDateTime orderTime = LocalDateTime.now();
        List<Order> orders = List.of(
                new Order(1L, 105.0, 1.0, Side.SELL, orderTime)
        );

        List<TradeOrderMatch> matches = matcher.findMatches(Collections.emptyList(), orders);

        assertThat(matches).isEmpty();
    }
}