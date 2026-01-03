package com.ESI.FastFoodESI.service.cliente;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.service.cliente.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

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

        //***************************************************
        //PONER EN FALSEE -> SIMPLEMENTE ES PARA Q NO TENGAN QUE VERIFICAR EL CORRO CON EL MAILTRAP PARA LAS PRUEBAS
        cliente.setVerificado(true);// Nace bloqueado

        // guardar en BD
        clienteRepository.save(cliente);

        // enviar correo
        emailService.enviarCorreoVerificacion(cliente.getCorreo(), codigo);
    }

    //OLVIDAR CONTRASEÑA
    @Transactional
    public void iniciarRecuperacionPassword(String correo) {
        Optional<Cliente> c = clienteRepository.findByCorreo(correo);

        // Si el usuario existe, generamos token y enviamos correo
        if (c.isPresent()) {
            Cliente cliente = c.get();
            String token = UUID.randomUUID().toString();
            cliente.setTokenRecuperacion(token);
            clienteRepository.save(cliente);

            emailService.enviarCorreoRecuperacion(cliente.getCorreo(), token);
        }
        // Si no existe, NO lanzamos error para no dar pistas a hackers (seguridad)
    }


    //PONER UNA NUEVA CONTRASEÑA
    @Transactional
    public boolean restablecerPassword(String token, String nuevaPassword) {
        // buscamos al cliente por el token

        // podríamos añadir 'Optional<Cliente> findByTokenRecuperacion(String token)' en el repository tmb
        Optional<Cliente> clienteOpt = clienteRepository.findAll().stream()
                .filter(c -> token.equals(c.getTokenRecuperacion()))
                .findFirst();

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            // actualizamos la contraseña (encriptada)
            String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
            cliente.setPassword(passwordEncriptada);

            // borramos el token para que no se pueda usar dos veces
            cliente.setTokenRecuperacion(null);

            clienteRepository.save(cliente);
            return true;
        }
        return false;
    }

    public void eliminarCliente(UUID id) {
        clienteRepository.deleteById(id);
    }
}