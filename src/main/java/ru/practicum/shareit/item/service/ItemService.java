package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(final long userId);

    ItemDto getItemById(final long itemId);

    ItemDto itemCreate(final long userId, final ItemDto itemDto);

    ItemDto itemUpdate(final long userId, final long itemId, final ItemDto itemDto);

    List<ItemDto> itemSearch(final String text);

    void itemDelete(final Long itemId);
}
