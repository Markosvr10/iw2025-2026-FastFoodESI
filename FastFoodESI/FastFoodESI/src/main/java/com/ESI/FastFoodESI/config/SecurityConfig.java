package com.ESI.FastFoodESI.config;

import com.ESI.FastFoodESI.model.Propietario;
import com.ESI.FastFoodESI.repository.PropietarioRepository; // <--- 1. Importar
import com.ESI.FastFoodESI.ui.views.publico.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    // 2. Declaramos el repositorio
    private final PropietarioRepository propietarioRepository;

    // 3. Lo inyectamos en el constructor
    public SecurityConfig(PropietarioRepository propietarioRepository) {
        this.propietarioRepository = propietarioRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll());

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));

        http.headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()));

        setLoginView(http, LoginView.class);

        // --- LÓGICA DE REDIRECCIÓN INTELIGENTE ---
        http.formLogin(login -> login
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    String redirectUrl = "/";

                    if ("ROLE_PROPIETARIO".equals(role)) {
                        // Por defecto, al panel de admin
                        redirectUrl = "admin/negocios";

                        // BUSCAMOS QUIÉN ES EL QUE ENTRA
                        String email = authentication.getName();
                        Optional<Propietario> userOpt = propietarioRepository.findByCorreo(email);

                        // SI SU APELLIDO ES "Establecimiento", ES UNA PANTALLA TÁCTIL -> AL HUB
                        if (userOpt.isPresent() &&
                                "Establecimiento".equalsIgnoreCase(userOpt.get().getApellido())) {
                            redirectUrl = "hub-empleados";
                        }
                    }
                    // Si implementas otros roles base, puedes poner más 'else if' aquí

                    response.sendRedirect(redirectUrl);
                }));

        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll());

        super.configure(http);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}