package com.switchfully.order.service.items;

import com.switchfully.order.IntegrationTest;
import com.switchfully.order.domain.items.Item;
import com.switchfully.order.domain.items.ItemRepository;
import com.switchfully.order.domain.items.prices.Price;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static com.switchfully.order.domain.items.ItemTestBuilder.anItem;
import static org.assertj.core.api.Assertions.assertThat;

public class ItemServiceIntegrationTest extends IntegrationTest {

    @Inject
    private ItemService itemService;

    @Inject private ItemRepository itemRepository;

    @Test
    public void createItem() {
        Item createdItem = itemService.createItem(anItem()
                .withName("The Martian")
                .withDescription("A cool book written by a software engineer")
                .withAmountOfStock(239)
                .withPrice(Price.create(BigDecimal.valueOf(10.90)))
                .build());

        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getId()).isNotNull().isNotEqualTo("");
        assertThat(createdItem.getName()).isEqualTo("The Martian");
        assertThat(createdItem.getDescription()).isEqualTo("A cool book written by a software engineer");
        assertThat(createdItem.getAmountOfStock()).isEqualTo(239);
        assertThat(createdItem.getPrice().getAmount()).isEqualTo(BigDecimal.valueOf(10.90));
    }

    @Test
    public void getItem() {
        Item createdItem = itemService.createItem(anItem().build());

        Item itemFromDb = itemService.getItem(createdItem.getId());

        assertThat(itemFromDb)
                .isNotNull()
                .isEqualTo(itemFromDb);
    }

    @Test
    public void getAllItems() {
        Item createdItem1 = itemService.createItem(anItem().build());
        Item createdItem2 = itemService.createItem(anItem().build());

        List<Item> allItems = itemService.getAllItems();

        assertThat(allItems).contains(createdItem1, createdItem2);
    }

}