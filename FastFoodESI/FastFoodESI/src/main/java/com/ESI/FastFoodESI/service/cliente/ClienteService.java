package com.ESI.FastFoodESI.service.cliente;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.service.EmailService; // Asegúrate de que este import sea correcto según dónde creaste el EmailService
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ClienteService(ClienteRepository clienteRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    //REGISTRO
    @Transactional
    public void registrarCliente(Cliente cliente) {
        // comprobar si el email ya existe
        if (clienteRepository.findByCorreo(cliente.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // encriptar la contraseña
        String passwordEncriptada = passwordEncoder.encode(cliente.getPassword());
        cliente.setPassword(passwordEncriptada);

        // generar código de verificación
        String codigo = UUID.randomUUID().toString();
        cliente.setCodigoVerificacion(codigo);
        cliente.setVerificado(false); // Nace bloqueado

        // guardar en BD
        clienteRepository.save(cliente);

        // enviar correo
        emailService.enviarCorreoVerificacion(cliente.getCorreo(), codigo);
    }
}