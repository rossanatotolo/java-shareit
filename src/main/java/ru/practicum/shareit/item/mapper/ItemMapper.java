package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(final Item item) {
        final ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }

    public Item toItem(final User user, final ItemDto itemDto) {
        final Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(user);
        item.setAvailable(itemDto.getAvailable());

        return item;
    }

    public List<ItemDto> toListDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toItemDto(item));
        }

        return result;
    }

    public ItemDtoOutput toItemDtoOutput(final Item item, List<CommentDto> comments) {

        final ItemDtoOutput itemDtoOutput = new ItemDtoOutput();

        itemDtoOutput.setId(item.getId());
        itemDtoOutput.setName(item.getName());
        itemDtoOutput.setDescription(item.getDescription());
        itemDtoOutput.setAvailable(item.getAvailable());
        itemDtoOutput.setComments(comments);

        return itemDtoOutput;
    }
}
