package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getAllItems(final long userId) {
        checkUserId(userId);
        log.info("Получение списка всех вещей пользователя.");
        return itemRepository.getAllItems(userId).stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto getItemById(final long itemId) {
        log.info("Получение вещи по id.");
        final Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto itemCreate(final long userId, final ItemDto itemDto) {
        final User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Item item = itemMapper.toItem(user, itemDto);
        log.info("Пользователь с id {} добавлен.", item.getId());
        return itemMapper.toItemDto(itemRepository.itemCreate(item));
    }

    @Override
    public ItemDto itemUpdate(final long userId, final long itemId, final ItemDto itemDto) {
        checkUserId(userId);

        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            log.warn("Пользователь с id = {} не является владельцем вещи с id = {}.", userId, itemId);
            throw new NotFoundException("Редактировать вещь может только владелец.");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        log.info("Вещь с id {} обновлена.", item.getId());
        return itemMapper.toItemDto(itemRepository.itemUpdate(itemId, item));
    }

    @Override
    public List<ItemDto> itemSearch(final String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Получение списка вещей по текстовому запросу {}.", text);
        return itemRepository.itemSearch(text.trim().toLowerCase()).stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    public void itemDelete(final Long itemId) {
        checkItemId(itemId);
        log.info("Вещь с id {} удалена.", itemId);
        itemRepository.itemDelete(itemId);
    }

    private void checkUserId(final Long userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            log.warn("Пользователя с id = {} не существует.", userId);
            throw new NotFoundException("Пользователя с id = {} не существует." + userId);
        }
    }

    private void checkItemId(final Long itemId) {
        if (itemId == null || itemRepository.getItemById(itemId).isEmpty()) {
            log.warn("Вещи с id = {} не существует.", itemId);
            throw new NotFoundException("Вещи с id = {} не существует." + itemId);
        }
    }
}
