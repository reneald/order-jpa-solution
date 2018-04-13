package com.switchfully.order.service.items;

import com.switchfully.order.domain.items.Item;
import com.switchfully.order.domain.items.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static com.switchfully.order.domain.items.ItemTestBuilder.anItem;

public class ItemServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ItemService itemService;
    private ItemValidator itemValidatorMock;
    private ItemRepository itemRepositoryMock;

    @Before
    public void setupService() {
        itemRepositoryMock = Mockito.mock(ItemRepository.class);
        itemValidatorMock = Mockito.mock(ItemValidator.class);
        itemService = new ItemService(itemRepositoryMock, itemValidatorMock);
    }

    @Test
    public void createItem_happyPath() {
        Item item = anItem().build();
        Mockito.when(itemValidatorMock.isValidForCreation(item)).thenReturn(true);
        Mockito.when(itemRepositoryMock.save(item)).thenReturn(item);

        Item createdItem = itemService.createItem(item);

        Assertions.assertThat(createdItem).isNotNull();
    }

    @Test
    public void createItem_givenItemThatIsNotValidForCreation_thenThrowException() {
        Item item = anItem().build();
        Mockito.when(itemValidatorMock.isValidForCreation(item)).thenReturn(false);
        Mockito.doThrow(IllegalStateException.class).when(itemValidatorMock)
                .throwInvalidStateException(item, "creation");

        expectedException.expect(IllegalStateException.class);

        itemService.createItem(item);
    }

    @Test
    public void updateItem_happyPath() {
        Item item = anItem().build();
        Mockito.when(itemValidatorMock.isValidForUpdating(item)).thenReturn(true);
        Mockito.when(itemRepositoryMock.update(item)).thenReturn(item);

        Item updatedItem = itemService.updateItem(item);

        Assertions.assertThat(updatedItem).isNotNull();
    }

    @Test
    public void updateItem_givenItemThatIsNotValidForUpdating_thenThrowException() {
        Item item = anItem().build();
        Mockito.when(itemValidatorMock.isValidForUpdating(item)).thenReturn(false);
        Mockito.doThrow(IllegalStateException.class).when(itemValidatorMock)
                .throwInvalidStateException(item, "updating");

        expectedException.expect(IllegalStateException.class);

        itemService.updateItem(item);
    }

}
