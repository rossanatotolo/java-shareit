package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOutput itemRequestCreate(final long userId, final ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOutput> getAllRequestByUser(final long userId);

    List<ItemRequestDtoOutput> getAllRequests(final long userId);

    ItemRequestDtoOutput getRequestById(final long requestId);
}
