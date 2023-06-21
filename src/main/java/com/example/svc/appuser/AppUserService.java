package com.example.svc.appuser;

import com.example.svc.registration.token.ConfirmationToken;
import com.example.svc.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";
    private static Logger logger = LoggerFactory.getLogger(AppUserService.class);
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if (userExists) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.
            if(!appUserRepository.findByEmail(appUser.getEmail()).get().isEnabled()){
                String savedPassword = appUserRepository.findByEmail(appUser.getEmail()).get().getPassword();

                if(!bCryptPasswordEncoder.matches(appUser.getPassword(), savedPassword)){
                    throw new RuntimeException("Wrong credentials, Password & username is not correct ");
                }
                else{
                   logger.info("Getting Token");
                   AppUser appUserFromRepo =  appUserRepository.findByEmail(appUser.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
                   String token = getConfirmationToken(appUserFromRepo);
                   logger.info("Confirmation link is sent, Please, Confirm your account");
                   return token;

                }
            }

            else {
                throw new IllegalStateException("email is already registered");
            }
        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        String token = getConfirmationToken(appUser);

//        TODO: SEND EMAIL

        return token;
    }

    private String getConfirmationToken(AppUser appUser) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );


        confirmationTokenService.saveConfirmationToken(
                confirmationToken);

        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
