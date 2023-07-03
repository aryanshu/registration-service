package com.example.svc.registration;

import com.example.svc.appuser.AppUser;
import com.example.svc.config.KafkaProducer;
import com.example.svc.registration.token.ConfirmationToken;
import com.example.svc.registration.token.ConfirmationTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class RegistrationController {
    private final KafkaProducer producer;
    private final RegistrationService registrationService;

    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping("/registration")
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path = "/registration/confirm")
    public String confirm(@RequestParam("token") String token) throws JsonProcessingException {
        ConfirmationToken confirmationToken = confirmationTokenService.getUser(token);
        String username = confirmationToken.getAppUser().getEmail();
        Map<String,String> payload = new HashMap<>();
        payload.put("username",username);
//        String kafkaPayload = new ObjectMapper().writeValueAsString(payload);
//        this.producer.sendMessage(kafkaPayload,"test");
        return registrationService.confirmToken(token);
    }

    @GetMapping(path = "login/refresh")
    public void refreshtoken(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

    }

    @PostMapping(path = "/user")
    public String users(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        return "api/v1/user";
    }

}
