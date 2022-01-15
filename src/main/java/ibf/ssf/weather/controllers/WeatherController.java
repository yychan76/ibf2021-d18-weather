package ibf.ssf.weather.controllers;
import static ibf.ssf.weather.Constants.DEFAULT_TIME_ZONE;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ibf.ssf.weather.error.CityNotFoundException;
import ibf.ssf.weather.models.Weather;
import ibf.ssf.weather.services.WeatherService;

@Controller
@RequestMapping(
    path = "/weather",
    produces=MediaType.TEXT_HTML_VALUE
)
public class WeatherController {
    private final Logger logger = Logger.getLogger(WeatherController.class.getName());

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public String submitForm(@RequestParam String city, Model model) {
        logger.info("User requested: %s".formatted(city));

        List<Weather> weatherList;
        try {
            weatherList = weatherService.getWeather(city);
            Weather weather = weatherList.iterator().next();
            String countryCode = weather.getCountryCode();
            Locale locale = new Locale("", countryCode);

            // unix tick units are in seconds, to convert to milliseconds multiply 1000
            Date resultDate = new Date(weather.getTimestamp() * 1000L);
            logger.info("resultDateLocalTime: %s".formatted(dateToLocalTime(resultDate)));
            long minutesAgo = ChronoUnit.MINUTES.between(dateToLocalTime(resultDate), LocalTime.now());
            logger.info("minutesAgo: %s".formatted(minutesAgo));
            model.addAttribute("weatherList", weatherList);
            model.addAttribute("countryName", locale.getDisplayCountry());
            model.addAttribute("countryFlag", localeToEmoji(locale));
            model.addAttribute("resultDate", resultDate);
            model.addAttribute("minutesAgo", minutesAgo);
        } catch (CityNotFoundException e) {
            logger.info(e.getMessage());
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("city", city);
        return "form";
    }

    private LocalTime dateToLocalTime(Date date) {
        return LocalTime.ofInstant(date.toInstant(), DEFAULT_TIME_ZONE);
    }

    /* code from https://stackoverflow.com/questions/30494284/android-get-country-emoji-flag-using-locale
         Emoji is a Unicode symbols. Based on the Unicode character table Emoji flags
         consist of 26 alphabetic Unicode characters (A-Z) intended to be used to encode
         ISO 3166-1 alpha-2 two-letter country codes (wiki). That means it is possible
         to split two-letter country code and convert each A-Z letter to regional
         indicator symbol letter:
     */
    private String localeToEmoji(Locale locale) {
        String countryCode = locale.getCountry();
        int firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
        int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;
        return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
    }
}
