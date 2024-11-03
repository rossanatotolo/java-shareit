package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(final long userId);

    ItemDtoOutput getItemById(final long userId, final long itemId);

    ItemDto itemCreate(final long userId, final ItemDto itemDto);

    ItemDto itemUpdate(final long userId, final long itemId, final ItemDto itemDto);

    List<ItemDto> itemSearch(final String text);

    void itemDelete(final Long itemId);

    CommentDto addComments(final long userId, final long itemId, final CommentDto commentDto);
}
