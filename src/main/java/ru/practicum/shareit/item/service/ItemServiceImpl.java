package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems(final long userId) {
        checkUserId(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        log.info("Получение списка всех вещей пользователя.");
        return itemMapper.toListDto(items);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoOutput getItemById(final long userId, final long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));

        List<CommentDto> commentsDto = commentRepository.findAllByItemId(itemId).stream()
                .map(comment -> commentMapper.toCommentDto(comment)).collect(Collectors.toList());

        final ItemDtoOutput itemDtoOutput = itemMapper.toItemDtoOutput(item, commentsDto);

        if ((Objects.equals(item.getOwner().getId(), userId))) {
            Optional<Booking> last = bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(itemId,
                    LocalDateTime.now(), List.of(BookingStatus.APPROVED));
            itemDtoOutput.setLastBooking(last == null ? null : last.get().getEnd());

            Optional<Booking> future = bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(itemId,
                    LocalDateTime.now(), List.of(BookingStatus.APPROVED));
            itemDtoOutput.setNextBooking(future == null ? null : future.get().getStart());
        }

        log.info("Вещь с id {} успешно получена.", itemId);
        return itemDtoOutput;
    }

    @Override
    public ItemDto itemCreate(final long userId, final ItemDto itemDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Item item = itemMapper.toItem(user, itemDto);

        log.info("Вещь с id {} добавлена.", item.getId());
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto itemUpdate(final long userId, final long itemId, final ItemDto itemDto) {
        checkUserId(userId);

        Item item = itemRepository.findById(itemId)
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
        return itemMapper.toItemDto(itemRepository.save(item));
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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));

        log.info("Вещь с id {} удалена.", itemId);
        itemRepository.delete(item);
    }

    @Override
    public CommentDto addComments(long userId, long itemId, CommentDto commentDto) {
        final User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));

        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Ошибка написания отзыва. Пользователь не бронировал вещь.");
        }

        final Comment comment = commentMapper.toComment(commentDto, owner, item);
        commentRepository.save(comment);

        log.info("Пользователь с id {} оставил комментарий к вещи с id {}.", userId, itemId);
        return commentMapper.toCommentDto(comment);
    }

    private void checkUserId(final Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Пользователя с id = {} не существует.", userId);
            throw new NotFoundException("Пользователя с id = {} не существует." + userId);
        }
    }
}
