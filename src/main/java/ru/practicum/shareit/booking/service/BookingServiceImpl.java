package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDtoOutput createBooking(final Long userId, BookingDtoInput bookingDtoInput) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Item item = itemRepository.findById(bookingDtoInput.getItemId())
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + bookingDtoInput.getItemId()));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступена для бронирования.");
        }

        final Booking booking = bookingMapper.toBooking(bookingDtoInput, user, item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        log.info("Вещь с id {} успешно забронирована.", bookingDtoInput.getItemId());
        return bookingMapper.toBookingDtoOutput(booking, userMapper.toUserDto(user), itemMapper.toItemDto(item));
    }


    @Override
    @Transactional
    public BookingDtoOutput confirmationBooking(final Long userId, final Long bookingId, final Boolean approved) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запроса на бронирование не существует."));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ValidationException("Подтверждение бронирования доступно только владельцу вещи.");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Вещь не ожиданиет бронирования.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        final Booking updateBooking = bookingRepository.save(booking);

        log.info("Запрос на подтверждение бронирования вещи с id = {} выполнен.", bookingId);
        return bookingMapper.toBookingDtoOutput(updateBooking,
                userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem()));
    }


    @Override
    @Transactional(readOnly = true)
    public BookingDtoOutput getBookingById(final Long userId, final Long bookingId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Запроса на бронирование не существует."));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)
                && !Objects.equals(booking.getBooker().getId(), userId)) {
            throw new ValidationException("Ошибка доступа к бронированию у пользователя. ");
        }

        log.info("Запрос на получение забронированной вещи с id = {} выполнен.", bookingId);
        return bookingMapper.toBookingDtoOutput(booking,
                userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem()));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDtoOutput> getAllBookingsFromUser(final Long userId, final String state) {
        final LocalDateTime time = LocalDateTime.now();
        final List<Booking> bookings;

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, time, time, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, time, sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, time, sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusIs(userId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusIs(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new ValidationException("Неверный параметр запроса state.");
        }

        log.info("Запрос на получение всех бронированний пользователя с id = {} выполнен.", userId);
        return bookings.stream()
                .map(booking -> bookingMapper.toBookingDtoOutput(booking,
                        userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDtoOutput> getAllBookingsFromOwner(final Long userId, final String state) {
        final LocalDateTime time = LocalDateTime.now();
        final List<Booking> bookings;

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));

        if (itemRepository.findAllByOwnerId(userId).isEmpty()) {
            throw new ValidationException("Вещи у пользователя не найдены.");
        }

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerId(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, time, time, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, time, sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, time, sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusIs(userId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusIs(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new ValidationException("Неверно передан параметр state");
        }

        log.info("Запрос на получение всех бронированных вещей пользователя с id = {} выполнен.", userId);
        return bookings.stream()
                .map(booking -> bookingMapper.toBookingDtoOutput(booking,
                        userMapper.toUserDto(booking.getBooker()), itemMapper.toItemDto(booking.getItem())))
                .collect(Collectors.toList());
    }
}


