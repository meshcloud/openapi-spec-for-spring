package io.meshcloud.openapispecforspring.test.app59;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time")
class App59TimeController {
    @GetMapping("/schedule")
    Schedule schedule() {
        return new Schedule(
                Instant.EPOCH,
                LocalDate.EPOCH,
                LocalDateTime.MIN,
                OffsetDateTime.MIN,
                ZonedDateTime.now(),
                LocalTime.MIDNIGHT,
                OffsetTime.MIN,
                YearMonth.of(2026, 10),
                MonthDay.of(10, 13),
                Year.of(2026),
                Duration.ZERO,
                Period.ZERO,
                ZoneId.of("Europe/Berlin"),
                ZoneOffset.UTC);
    }

    record Schedule(
            Instant instant,
            LocalDate localDate,
            LocalDateTime localDateTime,
            OffsetDateTime offsetDateTime,
            ZonedDateTime zonedDateTime,
            LocalTime localTime,
            OffsetTime offsetTime,
            YearMonth yearMonth,
            MonthDay monthDay,
            Year year,
            Duration duration,
            Period period,
            ZoneId zoneId,
            ZoneOffset zoneOffset) {}
}
