package org.cubelogic.trading;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public class Trade {
    private final long id;
    private final double price;
    private final double volume;
    private final Side side;
    private final LocalDateTime tradeTime;

    public Trade(long id, double price, double volume, Side side, LocalDateTime tradeTime) {
        this.id = id;
        this.price = price;
        this.volume = volume;
        this.side = side;
        this.tradeTime = tradeTime;
    }


    public Side side() {
        return side;
    }

    public double price() {
        return price;
    }

    public Temporal timestamp() {
        return tradeTime;
    }

    public long id() {
        return id;
    }
}
