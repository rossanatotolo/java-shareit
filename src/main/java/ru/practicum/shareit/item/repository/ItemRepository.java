package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getAllItems(final long userId);

    Optional<Item> getItemById(final long itemId);

    Item itemCreate(final Item item);

    Item itemUpdate(final long itemId, final Item item);

    List<Item> itemSearch(final String text);

    void itemDelete(final Long itemId);
}
