package com.switchfully.order.api.items;

import com.switchfully.order.domain.items.Item;
import com.switchfully.order.domain.items.prices.Price;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.util.reflection.Whitebox;

import java.math.BigDecimal;
import java.util.UUID;

import static com.switchfully.order.domain.items.Item.ItemBuilder.item;
import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void toDto() {
        Item item = item()
                .withName("Half-Life 3")
                .withDescription("Boehoehoehoeeeee")
                .withPrice(Price.create(BigDecimal.valueOf(49.50)))
                .withAmountOfStock(50520)
                .build();
        UUID itemId = UUID.randomUUID();
        Whitebox.setInternalState(item, "id", itemId);

        ItemDto itemDto = new ItemMapper().toDto(item);

        assertThat(itemDto)
                .isEqualToComparingFieldByField(new ItemDto()
                        .withId(itemId)
                        .withName("Half-Life 3")
                        .withDescription("Boehoehoehoeeeee")
                        .withPrice(49.50f)
                        .withAmountOfStock(50520)
                        .withStockUrgency(item.getStockUrgency().name()));
    }

    @Test
    public void toDomainWithId() {
        UUID itemId = UUID.randomUUID();

        Item item = new ItemMapper().toDomain(itemId, new ItemDto()
                .withId(itemId)
                .withName("Half-Life 3")
                .withDescription("Boehoehoe")
                .withPrice(45f)
                .withAmountOfStock(50520));

        assertThat(item)
                .isEqualToComparingFieldByFieldRecursively(item()
                        .withId(itemId)
                        .withName("Half-Life 3")
                        .withDescription("Boehoehoe")
                        .withPrice(Price.create(BigDecimal.valueOf(45.0)))
                        .withAmountOfStock(50520)
                        .build());
    }

    @Test
    public void toDomainWithId_givenIdNotMatchingIdInDto_thenThrowException() {
        UUID itemIdPath = UUID.randomUUID();
        UUID itemIdBody = UUID.randomUUID();

        expectedException.expectMessage("When updating an item, the provided ID in the path should match the " +
                "ID in the body: ID in path = " + itemIdPath.toString() + ", ID in body = " + itemIdBody.toString());
        expectedException.expect(IllegalArgumentException.class);

        new ItemMapper()
                .toDomain(itemIdPath, new ItemDto().withId(itemIdBody));
    }

    @Test
    public void toDomainWithId_givenIdButIdInBodyIsNull_thenUseGivenId() {
        UUID itemId = UUID.randomUUID();

        Item item = new ItemMapper().toDomain(itemId, new ItemDto()
                .withoutId()
                .withName("Half-Life 3")
                .withDescription("Boehoehoe")
                .withPrice(45f)
                .withAmountOfStock(50520));

        assertThat(item)
                .isEqualToComparingFieldByFieldRecursively(item()
                        .withId(itemId)
                        .withName("Half-Life 3")
                        .withDescription("Boehoehoe")
                        .withPrice(Price.create(BigDecimal.valueOf(45.0)))
                        .withAmountOfStock(50520)
                        .build());
    }

    @Test
    public void toDomain() {
        Item item = new ItemMapper().toDomain(new ItemDto()
                .withName("Half-Life 3")
                .withDescription("Boehoehoe")
                .withPrice(45f)
                .withAmountOfStock(50520));

        assertThat(item)
                .isEqualToComparingFieldByFieldRecursively(item()
                        .withName("Half-Life 3")
                        .withDescription("Boehoehoe")
                        .withPrice(Price.create(BigDecimal.valueOf(45.0)))
                        .withAmountOfStock(50520)
                        .build());
    }

    @Test
    public void toDomain_givenId_thenAlsoCorrectlyMapId() {
        UUID id = UUID.randomUUID();
        Item item = new ItemMapper().toDomain(new ItemDto()
                .withId(id)
                .withName("Half-Life 3")
                .withDescription("Boehoehoe")
                .withPrice(45f)
                .withAmountOfStock(50520));

        assertThat(item)
                .isEqualToComparingFieldByFieldRecursively(item()
                        .withId(id)
                        .withName("Half-Life 3")
                        .withDescription("Boehoehoe")
                        .withPrice(Price.create(BigDecimal.valueOf(45.0)))
                        .withAmountOfStock(50520)
                        .build());
    }

}