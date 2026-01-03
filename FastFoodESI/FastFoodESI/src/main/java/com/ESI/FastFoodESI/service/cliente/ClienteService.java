package com.ESI.FastFoodESI.service.cliente;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.service.cliente.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //REGISTRO
    @Transactional
    public void registrarCliente(Cliente cliente) {
        // i el email ya existe
        if (clienteRepository.findByCorreo(cliente.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Hibernate generará automáticamente el ID gracias a @GeneratedValue en la entidad.

        // encriptar la contraseña
        String passwordEncriptada = passwordEncoder.encode(cliente.getPassword());
        cliente.setPassword(passwordEncriptada);

        // guardar
        clienteRepository.save(cliente);
    }
}