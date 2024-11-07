package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponseRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutput;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    final Sort sort = Sort.by(Sort.Direction.DESC, "created");

    @Override
    public ItemRequestDtoOutput itemRequestCreate(final long userId, final ItemRequestDto itemRequestDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));
        final ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto,
                user));

        log.info("Запрос с id {} создан.", itemRequest.getId());
        final ItemRequestDtoOutput itemRequestDtoOutput = itemRequestMapper.toItemRequestDtoOutput(itemRequest);
        itemRequestDtoOutput.setRequester(userMapper.toUserDto(user));

        return itemRequestDtoOutput;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoOutput> getAllRequestByUser(final long userId) {
        final List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId, sort);
        List<ItemRequestDtoOutput> responseList = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDtoOutput itemRequestDtoOutput = itemRequestMapper.toItemRequestDtoOutput(itemRequest);

            List<ItemDtoResponseRequest> items = itemRepository.findAllByRequest(itemRequest)
                    .stream()
                    .map(itemMapper::toItemDtoResponseRequest)
                    .collect(Collectors.toList());

            itemRequestDtoOutput.setItems(items);
            itemRequestDtoOutput.setRequester(userMapper.toUserDto(itemRequest.getRequester()));
            responseList.add(itemRequestDtoOutput);
        }

        log.info("Получение всех запросов пользователя с id = {}.", userId);
        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoOutput> getAllRequests(final long userId) {
        final List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId, sort);
        List<ItemRequestDtoOutput> responselist = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDtoOutput itemRequestDtoOutput = itemRequestMapper.toItemRequestDtoOutput(itemRequest);
            itemRequestDtoOutput.setRequester(userMapper.toUserDto(itemRequest.getRequester()));
            responselist.add(itemRequestDtoOutput);
        }

        log.info("Получение всех запросов, созданных другими пользователями, кроме запросов пользователя с id = {}.", userId);
        return responselist;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoOutput getRequestById(final long requestId) {
        final ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = {} не найден." + requestId));
        final ItemRequestDtoOutput itemRequestDtoOutput = itemRequestMapper.toItemRequestDtoOutput(itemRequest);
        final List<ItemDtoResponseRequest> items = itemRepository.findAllByRequest(itemRequest)
                .stream()
                .map(itemMapper::toItemDtoResponseRequest)
                .collect(Collectors.toList());

        itemRequestDtoOutput.setItems(items);
        itemRequestDtoOutput.setRequester(userMapper.toUserDto(itemRequest.getRequester()));

        log.info("Получение запроса по id = {}.", requestId);
        return itemRequestDtoOutput;
    }
}
