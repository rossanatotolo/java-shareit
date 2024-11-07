package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    APPROVED, // Бронирование подтверждено владельцем
    CANCELED, // Бронирование отменено заказчиком
    REJECTED, // Бронирование отклонено владельцем
    WAITING //  Бронирование, ожидает одобрения владельца
}

