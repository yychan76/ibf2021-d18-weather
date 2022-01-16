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
        // populate this for the form to show dynamic title without city not found error value at landing page
        model.addAttribute("landing", true);
        return "form";
    }
}
