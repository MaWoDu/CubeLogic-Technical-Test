## TEST Overview:

“Dear candidate, thanks a lot for your interest in the role and we wish you the best of luck.
This is a coding challenge, we expect it takes no longer than an hour, but there is no time control – feel free to take as much as you find it’s necessary.
Send the solution in either email attachment or post it to your GitHub and provide a link, or any other way you find convenient. 
*Please can you ensure that there are no EXE files included in the Zip folder*
While coding, pay attention to:
- naming
- structures clarity
- overall readability
- think of corner cases
- tests

Use Java and any libraries but try to keep things as simple as possible.
If you find something missing in the task description, make reasonable assumptions and put them in the Readme file.”

## TASK:

Define and implement one Interface, which has only one method: 
it takes a list of trades and a list of orders, and outputs those of them which it finds suspicious.

Trades and orders both have following fields: long id, double price (it may go negative on our marketplace), double volume, Side side (buy or sell), LocalDateTime timestamp.

You find trades and orders suspicious if you see the following pattern in the trader's behaviour:
- in a time, window of 30 minutes before the trade there were placed order(s) of an opposite side.
- the orders you are checking for the trade have a price not more than 10% lower or higher than the trade price 
 (depending on the side: if it's a buy trade, then sell orders should be not more than 10% more expensive, and vice versa)

I.e. we are trying to catch that the trader was attempting to make the market moving to a better price.”

## Requirements & Technical Review

```
Order: intention/request to buy or sell something at a specific price (Has a future intent)
Trade: actual transaction that has occurred when orders are matched (Completed transaction)

In: list<trades>, list<order>
out: list<trades> # flagged ones 

trades/orders: long id, double price, double volume, Side side, LocalDateTime timestamp.
Side: Buy/Sell

suspicions:
- looking at orders 30 minutes before a trade
- price not more than 10% different than the trade price 
```