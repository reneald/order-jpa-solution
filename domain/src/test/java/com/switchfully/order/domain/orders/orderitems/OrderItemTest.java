package com.switchfully.order.domain.orders.orderitems;

import com.switchfully.order.domain.items.prices.Price;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.switchfully.order.domain.orders.orderitems.OrderItem.OrderItemBuilder.orderItem;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemTest {

    @Test
    public void getShippingDate_givenItemWasInStock_thenReturnCurrentDatePlusOneDay() {
        Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        OrderItem orderItem = new OrderItem(orderItem()
                .withOrderedAmount(5)
                .withShippingDateBasedOnAvailableItemStock(10),
                fixedClock);

        assertThat(orderItem.getShippingDate()).isEqualTo(LocalDate.now(fixedClock).plusDays(1));
    }

    @Test
    public void getShippingDate_givenItemWasNotInStock_thenReturnCurrentDatePlusSevenDays() {
        Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        OrderItem orderItem = new OrderItem(orderItem()
                .withOrderedAmount(5)
                .withShippingDateBasedOnAvailableItemStock(4),
                fixedClock);

        assertThat(orderItem.getShippingDate()).isEqualTo(LocalDate.now(fixedClock).plusDays(7));
    }

    @Test
    public void getTotalPrice() {
        OrderItem orderItem = orderItem()
                .withItemPrice(Price.create(BigDecimal.valueOf(15)))
                .withOrderedAmount(3)
                .build();

        Price totalPrice = orderItem.getTotalPrice();

        assertThat(totalPrice.getAmount()
                .equals(BigDecimal.valueOf(15).multiply(BigDecimal.valueOf(3))))
                .isTrue();
    }

}