package edu.odu.cs.gold.controller;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import edu.odu.cs.gold.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.crypto.password.PasswordEncoder;

import edu.odu.cs.gold.model.User;
import edu.odu.cs.gold.service.EmailService;
import edu.odu.cs.gold.service.UserService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

@Controller
public class RegisterController {

    private PasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private EmailService emailService;
    private UserRepository userRepository;

    @Autowired
    public RegisterController(PasswordEncoder bCryptPasswordEncoder, UserService userService, EmailService emailService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    // Return registration form template
    @GetMapping("/user/register")
    public String showRegistrationPage(Model model, User user){
        model.addAttribute("user", user);
        return "user/register";
    }

    // Process form input data
    @PostMapping("/user/register")
    public String processRegistrationForm(Model model, @Valid User user, BindingResult bindingResult, HttpServletRequest request) {
        // Lookup user in database by e-mail
        boolean userExists = userService.userExists(user.getEmail());
        System.out.println("User exists: " + userExists);
        if (userExists) {
            model.addAttribute("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.");
            bindingResult.reject("email");
        }
        else {
            // Disable user until they click on confirmation link in email
            user.setEnabled(false);
            // Generate random 36-character string token for confirmation link
            user.setConfirmationToken(UUID.randomUUID().toString());
            user.setId(UUID.randomUUID().toString());
            user.setRole("user");

            userService.saveUser(user);
            String appUrl = request.getScheme() + "://" + request.getServerName();
            SimpleMailMessage registrationEmail = new SimpleMailMessage();
            registrationEmail.setTo(user.getEmail());
            registrationEmail.setSubject("Registration Confirmation");
            registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
                    + appUrl + ":8083/user/confirm?token=" + user.getConfirmationToken());
            registrationEmail.setFrom("noreply@ParkODU.cs.odu.edu");
            emailService.sendEmail(registrationEmail);
            model.addAttribute("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
        }
        return "user/register";
    }

    // Process confirmation link
    @GetMapping("/user/confirm")
    public String showConfirmationPage(Model model, @RequestParam("token") String token) {

        User user = userRepository.findByKey(token);
        if (user == null) { // No token found in DB
            model.addAttribute("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else { // Token found
           model.addAttribute("confirmationToken", token);
        }
        return "user/confirm";
    }

    // Process confirmation link
    @PostMapping("/user/confirm")
    public String processConfirmationForm(Model model, BindingResult bindingResult, @RequestParam Map requestParams, RedirectAttributes redir) {
        Zxcvbn passwordCheck = new Zxcvbn();
        Strength strength = passwordCheck.measure((String)requestParams.get("password"));
        if (strength.getScore() < 3) {
            bindingResult.reject("password");
            redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");
            model.addAttribute("redirect:confirm?token=" + requestParams.get("token"));
            System.out.println(requestParams.get("token"));
            return "user/confirm";
        }
        // Find the user associated with the reset token
        User user = userRepository.findByKey((String)requestParams.get("token"));
        // Set new Password
        user.setPassword(bCryptPasswordEncoder.encode((String)requestParams.get("password")));
        // Set user to enabled
        user.setEnabled(true);
        // Save user
        userService.saveUser(user);
        model.addAttribute("successMessage", "Your password has been set!");
        return "user/confirm";
    }

}
