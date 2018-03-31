package edu.odu.cs.gold.controller;

import edu.odu.cs.gold.model.Event;
import edu.odu.cs.gold.model.Garage;
import edu.odu.cs.gold.repository.EventRepository;
import edu.odu.cs.gold.repository.GarageRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

    private GarageRepository garageRepository;
    private EventRepository eventRepository;

    public HomeController(GarageRepository garageRepository,
                          EventRepository eventRepository) {
        this.garageRepository = garageRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping({"","/","/index"})
    public String index(Model model,
                        @RequestParam(value = "error", required = false) String dangerMessage) {

        List<Event> events = new ArrayList<>(eventRepository.findAll());
        if(events != null) {
            //events.sort(Comparator.comparing(Event::getEventDateTime));
            model.addAttribute("events",events);
        }

        List<Garage> garages = new ArrayList<>(garageRepository.findAll());
        garages.sort(Comparator.comparing(Garage::getName));
        StringBuilder currentAvailabilityDataString = new StringBuilder();
        for (Garage garage : garages) {
            currentAvailabilityDataString.append(garage.getAvailableSpaces() + ",");
        }
        model.addAttribute("currentAvailabilityDataString", currentAvailabilityDataString.toString());

        // Alerts
        if (dangerMessage != null) {
            model.addAttribute("dangerMessage", dangerMessage);
        }
        return "home/index";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings/index";
    }

    @GetMapping("/login")
    public String login() {
        return "home/login";
    }

    // Login form with error
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "home/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

}
