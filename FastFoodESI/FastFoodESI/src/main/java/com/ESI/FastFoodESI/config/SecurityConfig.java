package com.ESI.FastFoodESI.config;

import com.ESI.FastFoodESI.ui.views.publico.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                        // No hace falta poner "/login" o "/" aquí, Vaadin lo gestiona con las anotaciones de las vistas
        );

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));

        http.headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()));


        //Login
        setLoginView(http, LoginView.class);
        // Personalización del Login (Success Handler) -> te redirige
        http.formLogin(login -> login
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    String redirectUrl = "/";

                    if ("ROLE_PROPIETARIO".equals(role)) {
                        redirectUrl = "admin/negocios";
                    } else if ("ROLE_COCINA".equals(role)) {
                        // redirectUrl = "cocina/pedidos";
                    }

                    response.sendRedirect(redirectUrl);
                })
        );

        // Logout
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll());


        super.configure(http);  // activa la seguridad interna de Vaadin y permite que @AnonymousAllowed funcione
    }

    //LO COMENTO DE MOMENTO, PARA Q LAS CUENTAS DEL data.sql AL NO TENER LAS CONTRASEÑAS ENCRIPTADAS SIGAN FUNCIONANDO
    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}