package ibf.ssf.weather.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(
    path = "/",
    produces=MediaType.TEXT_HTML_VALUE
)
public class MainController {

    @GetMapping
    public String showForm(Model model) {
        return "form";
    }
}
