package com.ESI.FastFoodESI.security;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.model.Propietario;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.repository.PropietarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Service
public class SecurityService implements UserDetailsService {

    private final PropietarioRepository propietarioRepository;
    private final ClienteRepository clienteRepository;

    public SecurityService(PropietarioRepository propietarioRepository,
                           ClienteRepository clienteRepository) {
        this.propietarioRepository = propietarioRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // BUSCAR PROPIETARIO
        Optional<Propietario> propietario = propietarioRepository.findByCorreo(username);
        if (propietario.isPresent()) {
            return User.builder()
                    .username(propietario.get().getCorreo())
                    .password(propietario.get().getPassword())
                    .roles("PROPIETARIO")
                    .build();
        }

        // BUSCAR CLIENTE
        Optional<Cliente> cliente = clienteRepository.findByCorreo(username);
        if (cliente.isPresent()) {
            return User.builder()
                    .username(cliente.get().getCorreo())
                    .password(cliente.get().getPassword())
                    .roles("CLIENTE")
                    .build();
        }

        // SI NO EXISTE
        throw new UsernameNotFoundException("Usuario no encontrado");
    }

    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }
}