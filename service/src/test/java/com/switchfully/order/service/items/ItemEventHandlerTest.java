package com.switchfully.order.service.items;

import com.switchfully.order.IntegrationTest;
import com.switchfully.order.domain.items.Item;
import com.switchfully.order.domain.items.ItemRepository;
import com.switchfully.order.domain.orders.orderitems.events.OrderItemCreatedEvent;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;

import javax.inject.Inject;

import static com.switchfully.order.domain.items.ItemTestBuilder.anItem;
import static com.switchfully.order.domain.orders.orderitems.OrderItemTestBuilder.anOrderItem;

public class ItemEventHandlerTest extends IntegrationTest{

    @Inject
    private ItemEventHandler.OrderItemCreatedEventListener orderItemCreatedEventListener;

    @Inject
    private ItemRepository itemRepository;

    @After
    public void resetDatabase() {
        itemRepository.reset();
    }

    @Test
    public void onApplicationEvent_givenAnOrderItemCreatedEvent_thenUpdateStockOfItem() {
        Item persistedItem = itemRepository.save(anItem().withAmountOfStock(10).build());

        orderItemCreatedEventListener.onApplicationEvent(new OrderItemCreatedEvent(anOrderItem()
                .withItemId(persistedItem.getId()).withOrderedAmount(4).build()));

        Assertions.assertThat(itemRepository.get(persistedItem.getId()).getAmountOfStock())
                .isEqualTo(6);
    }

}