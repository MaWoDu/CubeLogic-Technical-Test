import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuspiciousTradeServiceTest {
    @Mock
    private TradeOrderMatcher matcher;

    @Mock
    private SuspiciousOrderDetector detector;

    private SuspiciousTradeService service;

    @BeforeEach
    void setUp() {
        service = new SuspiciousTradeService(matcher, detector);
    }

    @Test
    void shouldIdentifySuspiciousTrade() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, now);
        Order order = new Order(1L, 110.0, 1.0, Side.SELL, now.minusMinutes(15));

        List<Trade> trades = List.of(trade);
        List<Order> orders = List.of(order);
        TradeOrderMatch match = new TradeOrderMatch(trade, orders);

        when(matcher.findMatches(trades, orders))
                .thenReturn(List.of(match));
        when(detector.isSuspiciousTradePattern(trade, orders))
                .thenReturn(true);

        // When
        List<Trade> suspiciousTrades = service.findSuspiciousTrades(trades, orders);

        // Then
        assertThat(suspiciousTrades)
                .hasSize(1)
                .containsExactly(trade);

        verify(matcher).findMatches(trades, orders);
        verify(detector).isSuspiciousTradePattern(trade, orders);
    }

    @Test
    void shouldNotIdentifyTradeWhenNoMatchingOrders() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Trade trade = new Trade(1L, 100.0, 1.0, Side.BUY, now);

        List<Trade> trades = List.of(trade);
        List<Order> orders = Collections.emptyList();
        TradeOrderMatch match = new TradeOrderMatch(trade, Collections.emptyList());

        when(matcher.findMatches(trades, orders))
                .thenReturn(List.of(match));

        // When
        List<Trade> suspiciousTrades = service.findSuspiciousTrades(trades, orders);

        // Then
        assertThat(suspiciousTrades).isEmpty();

        verify(matcher).findMatches(trades, orders);
        verifyNoInteractions(detector);
    }

    @Test
    void shouldHandleMultipleTrades() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Trade trade1 = new Trade(1L, 100.0, 1.0, Side.BUY, now);
        Trade trade2 = new Trade(2L, 200.0, 2.0, Side.SELL, now);

        Order order1 = new Order(1L, 110.0, 1.0, Side.SELL, now.minusMinutes(15));
        Order order2 = new Order(2L, 180.0, 2.0, Side.BUY, now.minusMinutes(15));

        List<Trade> trades = List.of(trade1, trade2);
        List<Order> allOrders = List.of(order1, order2);

        TradeOrderMatch match1 = new TradeOrderMatch(trade1, List.of(order1));
        TradeOrderMatch match2 = new TradeOrderMatch(trade2, List.of(order2));

        when(matcher.findMatches(trades, allOrders))
                .thenReturn(List.of(match1, match2));
        when(detector.isSuspiciousTradePattern(trade1, List.of(order1)))
                .thenReturn(true);
        when(detector.isSuspiciousTradePattern(trade2, List.of(order2)))
                .thenReturn(false);

        // When
        List<Trade> suspiciousTrades = service.findSuspiciousTrades(trades, allOrders);

        // Then
        assertThat(suspiciousTrades)
                .hasSize(1)
                .containsExactly(trade1);

        verify(matcher).findMatches(trades, allOrders);
        verify(detector).isSuspiciousTradePattern(trade1, List.of(order1));
        verify(detector).isSuspiciousTradePattern(trade2, List.of(order2));
    }

    @Test
    void shouldHandleEmptyInput() {
        // Given
        List<Trade> trades = Collections.emptyList();
        List<Order> orders = Collections.emptyList();

        when(matcher.findMatches(trades, orders))
                .thenReturn(Collections.emptyList());

        // When
        List<Trade> suspiciousTrades = service.findSuspiciousTrades(trades, orders);

        // Then
        assertThat(suspiciousTrades).isEmpty();

        verify(matcher).findMatches(trades, orders);
        verifyNoInteractions(detector);
    }
}