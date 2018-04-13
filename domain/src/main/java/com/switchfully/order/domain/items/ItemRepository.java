package com.switchfully.order.domain.items;

import com.switchfully.order.domain.Repository;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ItemRepository extends Repository<Item, ItemDatabase> {

    @Inject
    public ItemRepository(ItemDatabase database) {
        super(database);
    }
}
