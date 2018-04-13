package com.switchfully.order.domain.items;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static com.switchfully.order.domain.items.ItemTestBuilder.anItem;
import static org.assertj.core.api.Assertions.assertThat;

public class ItemTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void decrementStock() {
        Item item = anItem().withAmountOfStock(10).build();
        item.decrementStock(8);
        assertThat(item.getAmountOfStock()).isEqualTo(2);
    }

    @Test
    public void decrementStock_givenHigherAmountToDecrementThanActualRemainingStock_thenThrowException() {
        UUID itemId = UUID.randomUUID();
        Item item = anItem().withId(itemId).withAmountOfStock(7).build();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Decrementing the stock amount of an item " + itemId.toString() + " below 0 is not allowed");

        item.decrementStock(8);
    }

    @Test
    public void getStockUrgency_givenAmountOfStockLowerThan5_thenLowStockUrgency() {
        Item item = anItem().withAmountOfStock(4).build();
        assertThat(item.getStockUrgency()).isEqualTo(Item.StockUrgency.STOCK_LOW);
    }

    @Test
    public void getStockUrgency_givenAmountOfStockLowerThan10_thenMediumStockUrgency() {
        Item item = anItem().withAmountOfStock(7).build();
        assertThat(item.getStockUrgency()).isEqualTo(Item.StockUrgency.STOCK_MEDIUM);
    }

    @Test
    public void getStockUrgency_givenAmountOfStockEqualTo10_thenHighStockUrgency() {
        Item item = anItem().withAmountOfStock(10).build();
        assertThat(item.getStockUrgency()).isEqualTo(Item.StockUrgency.STOCK_HIGH);
    }

    @Test
    public void getStockUrgency_givenAmountOfStockHigherThan10_thenHighStockUrgency() {
        Item item = anItem().withAmountOfStock(11).build();
        assertThat(item.getStockUrgency()).isEqualTo(Item.StockUrgency.STOCK_HIGH);
    }


}