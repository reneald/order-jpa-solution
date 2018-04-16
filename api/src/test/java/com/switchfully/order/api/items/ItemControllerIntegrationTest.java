package com.switchfully.order.api.items;

import com.switchfully.order.ControllerIntegrationTest;
import com.switchfully.order.domain.items.Item;
import com.switchfully.order.domain.items.ItemRepository;
import com.switchfully.order.domain.items.prices.Price;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import java.math.BigDecimal;

import static com.switchfully.order.domain.items.ItemTestBuilder.anItem;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class ItemControllerIntegrationTest extends ControllerIntegrationTest {

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private ItemMapper itemMapper;

    @Override
    public void clearDatabase() {
        itemRepository.getEntityManager().createQuery("DELETE FROM Item").executeUpdate();
    }

    @Test
    public void createItem() {
        ItemDto itemDto = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME), createAnItem(), ItemDto.class);

        assertThat(itemDto.getId()).isNotNull().isNotEmpty();
        assertThat(itemDto).isEqualToIgnoringGivenFields(createAnItem(), "id", "stockUrgency");
    }

    @Test
    public void getAllItems() {
        ItemDto item1 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(12), ItemDto.class);
        ItemDto item2 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(2), ItemDto.class);
        ItemDto item3 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(16), ItemDto.class);
        ItemDto item4 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(8), ItemDto.class);
        ItemDto item5 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(4), ItemDto.class);

        ItemDto[] items = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME), ItemDto[].class);

        assertThat(items).hasSize(5);
        assertThat(items[0]).isEqualToComparingFieldByFieldRecursively(item2);
        assertThat(items[1]).isEqualToComparingFieldByFieldRecursively(item5);
        assertThat(items[2]).isEqualToComparingFieldByFieldRecursively(item4);
        assertThat(items[3]).isEqualToComparingFieldByFieldRecursively(item1);
        assertThat(items[4]).isEqualToComparingFieldByFieldRecursively(item3);
    }

    @Test
    public void getAllItems_givenAStockUrgencyFilter_thenOnlyReturnItemsWithThatUrgency() {
        ItemDto item1 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(12), ItemDto.class);
        ItemDto item2 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(20), ItemDto.class);
        new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(8), ItemDto.class);
        ItemDto item4 = new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(16), ItemDto.class);
        new TestRestTemplate()
                .postForObject(format("http://localhost:%s/%s", getPort(), ItemController.RESOURCE_NAME),
                        createAnItem().withAmountOfStock(4), ItemDto.class);

        ItemDto[] items = new TestRestTemplate()
                .getForObject(format("http://localhost:%s/%s?stockUrgency=STOCK_HIGH", getPort(),
                        ItemController.RESOURCE_NAME), ItemDto[].class);

        assertThat(items).hasSize(3);
        assertThat(items[0]).isEqualToComparingFieldByFieldRecursively(item1);
        assertThat(items[1]).isEqualToComparingFieldByFieldRecursively(item4);
        assertThat(items[2]).isEqualToComparingFieldByFieldRecursively(item2);
    }

    @Test
    public void updateItem() {
        Item alreadyExistingItem = itemRepository.save(anItem()
                .withName("Laptop X10")
                .withDescription("A fancy laptop")
                .withAmountOfStock(15)
                .withPrice(Price.create(BigDecimal.valueOf(1199.95)))
                .build());

        ItemDto itemToUpdate = new ItemDto()
                .withId(alreadyExistingItem.getId())
                .withName(alreadyExistingItem.getName())
                .withDescription("A very fancy laptop")
                .withPrice(alreadyExistingItem.getPrice().getAmountAsFloat())
                .withAmountOfStock(10);

        ResponseEntity<ItemDto> result = new TestRestTemplate()
                .exchange(format("http://localhost:%s/%s/%s", getPort(), ItemController.RESOURCE_NAME, alreadyExistingItem.getId().toString()),
                        HttpMethod.PUT,
                        new HttpEntity<>(itemToUpdate),
                        ItemDto.class);

        assertThat(result.getBody()).isEqualToIgnoringGivenFields(itemToUpdate, "stockUrgency");
    }

    private ItemDto createAnItem() {
        return new ItemDto()
                .withName("Half-Life 3")
                .withDescription("Boehoehoe...")
                .withPrice(45.50f)
                .withAmountOfStock(50510);
    }
}