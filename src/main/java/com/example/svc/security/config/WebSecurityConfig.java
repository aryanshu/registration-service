package com.example.svc.security.config;

import com.example.svc.appuser.AppUserService;
import com.example.svc.config.KafkaProducer;
import com.example.svc.filter.CustomAuthenticationFilter;
import com.example.svc.filter.CustomAuthorizationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
@Service
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserDetailsService userDetailsService;

    private final KafkaProducer kafkaProducer;

    private final DiscoveryClient discoveryClient;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/api/v1/login/**","/api/v1/registration/**").permitAll();
        http.authorizeRequests().antMatchers( "/api/v1/user/**").hasAnyAuthority("USER");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilter(customAuthenticationFilter);


//        http
//                .csrf().disable()
//                .authorizeRequests()
//                    .antMatchers("/api/v1/login/**","/api/v1/registration/**")
//                    .permitAll()
//                .antMatchers(POST, "/api/v1/user/**")
//                .hasAnyAuthority("USER")
//                .anyRequest()
//                .authenticated().and()
//                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilter(customAuthenticationFilter);
//                .formLogin();
//                .successHandler((request, response, authentication) -> {
//                    // Produce Kafka message here
//                    String username = authentication.getName();
//                    Map<String,String> payload = new HashMap<>();
//                    payload.put("username",username);
//                    String kafkaPayload = new ObjectMapper().writeValueAsString(payload);
//                    kafkaProducer.sendMessage(kafkaPayload,"test");
//                    // Redirect to desired URL after successful login

                    // Make the GET request
//                    RestTemplate restTemplate = new RestTemplate();
//                    List<ServiceInstance> instances = discoveryClient.getInstances("HANDLER-SVC");
//                    String handlerSvcUrl = instances.get(0).getUri().toString();
//
//                    if (!handlerSvcUrl.startsWith("http")) {
//                        handlerSvcUrl = "http://" + handlerSvcUrl;
//                    }
//
//
//                    String url =handlerSvcUrl + "/api/v1/profile/?Id=9";
//                    ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
//                    System.out.println(responseEntity.toString());
//                    response.sendRedirect("/dashboard");
//                });
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
//        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }
}
