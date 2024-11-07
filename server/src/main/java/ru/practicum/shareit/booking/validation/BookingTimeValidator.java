package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.model.Booking;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingTimeValidator implements ConstraintValidator<ValidBookingTime, Booking> {

    @Override
    public void initialize(ValidBookingTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext context) {
        if (booking == null) {
            return true;
        }
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        return start != null && end != null && start.isBefore(end) && start.equals(end);
    }
}