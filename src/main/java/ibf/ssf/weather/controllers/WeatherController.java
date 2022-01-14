package ibf.ssf.weather.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        List<Weather> weatherList = weatherService.getWeather(city);

        model.addAttribute("city", city);
        model.addAttribute("weatherList", weatherList);
        return "form";
    }
}
