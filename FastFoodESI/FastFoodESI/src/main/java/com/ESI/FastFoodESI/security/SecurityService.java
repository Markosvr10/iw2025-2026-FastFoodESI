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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;

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

        //verificacion
        Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(username);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            // comprobamos si esta verificado
            if (!cliente.isVerificado()) {
                throw new DisabledException("Tu cuenta no está verificada. Revisa tu correo.");
            }
            // -------------------------------------------

            // Si está verificado, creamos el usuario para el login
            return User.builder()
                    .username(cliente.getCorreo())
                    .password(cliente.getPassword())
                    .roles("CLIENTE")
                    .build();
        }

        // si no es propietario ni cliente
        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }

    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();

        // Comprobamos que auth no sea null para evitar errores
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return (UserDetails) auth.getPrincipal();
        }
        return null;
    }

}