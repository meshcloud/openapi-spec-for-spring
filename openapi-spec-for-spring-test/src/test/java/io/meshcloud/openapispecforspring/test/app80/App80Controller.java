package io.meshcloud.openapispecforspring.test.app80;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App80Controller {
    @GetMapping("/stations")
    Station station() {
        return new Station(
                Station.class,
                Locale.GERMANY,
                TimeZone.getTimeZone("Europe/Berlin"),
                Currency.getInstance("EUR"),
                Charset.forName("UTF-8"),
                Pattern.compile("[A-Z]{3}"));
    }

    record Station(
            Class<?> kind, Locale locale, TimeZone timeZone, Currency currency, Charset charset, Pattern codePattern) {}
}
