package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {
    public ItemRequest toItemRequest(final ItemRequestDto itemRequestDto, final User user) {

        final ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(user);

        return itemRequest;
    }

    public ItemRequestDtoOutput toItemRequestDtoOutput(final ItemRequest itemRequest) {

        final ItemRequestDtoOutput itemRequestDtoOutput = new ItemRequestDtoOutput();

        itemRequestDtoOutput.setId(itemRequest.getId());
        itemRequestDtoOutput.setCreated(itemRequest.getCreated());
        itemRequestDtoOutput.setDescription(itemRequest.getDescription());

        return itemRequestDtoOutput;
    }

}
