package com.ESI.FastFoodESI.security;

import com.ESI.FastFoodESI.model.Propietario;
import com.ESI.FastFoodESI.repository.PropietarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityService implements UserDetailsService {

    private final PropietarioRepository propietarioRepository;

    public SecurityService(PropietarioRepository propietarioRepository) {
        this.propietarioRepository = propietarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Propietario propietario = propietarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
                .username(propietario.getCorreo())
                .password(propietario.getPassword()) 
                .roles("PROPRIETARIO")
                .build();
    }
}
