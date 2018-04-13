package com.switchfully.order.service.items;

import com.switchfully.order.domain.items.Item;
import com.switchfully.order.domain.items.ItemRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
public class ItemService {

    private ItemRepository itemRepository;
    private ItemValidator itemValidator;

    @Inject
    public ItemService(ItemRepository itemRepository, ItemValidator itemValidator) {
        this.itemRepository = itemRepository;
        this.itemValidator = itemValidator;
    }

    public Item createItem(Item item) {
        if (!itemValidator.isValidForCreation(item)) {
            itemValidator.throwInvalidStateException(item, "creation");
        }
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        if (!itemValidator.isValidForUpdating(item)) {
            itemValidator.throwInvalidStateException(item, "updating");
        }
        return itemRepository.update(item);
    }

    public Item getItem(UUID itemId) {
        return itemRepository.get(itemId);
    }

    public void decrementStockForItem(UUID itemId, int amountToDecrement) {
        Item item = itemRepository.get(itemId);
        item.decrementStock(amountToDecrement);
        itemRepository.update(item);
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(itemRepository.getAll().values());
    }
}
