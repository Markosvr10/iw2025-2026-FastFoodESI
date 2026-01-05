package com.ESI.FastFoodESI.service.cliente;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //VERIFICAR CORREO
    public void enviarCorreoVerificacion(String destino, String codigo) {
        MimeMessage message = mailSender.createMimeMessage(); // En vez de SimpleMailMessage, usamos MimeMessage para poder mandar HTML

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); //'true' ->mensaje MULTIPART (para adjuntos o HTML)

            helper.setFrom("noreply@fastfoodesi.com");
            helper.setTo(destino);
            helper.setSubject("Verifica tu cuenta - FastFood ESI");

            String link = "http://localhost:8080/verificar?code=" + codigo;

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; background-color: #edf2f7; padding: 40px; color: #718096;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        
                        <h2 style="color: #3d4852; margin-top: 0;">¡Hola!</h2>
                        <p>Gracias por registrarte en <strong>FastFood ESI</strong>.</p>
                        <p>Por favor, haz clic en el botón de abajo para verificar tu dirección de correo electrónico:</p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #2d3748; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">Verificar Email</a>
                        </div>
                        
                        <p>Si no has creado ninguna cuenta, no es necesaria ninguna acción.</p>
                        
                        <br>
                        <p>Saludos,<br>El equipo de FastFood ESI 🍔</p>
                        
                        <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;">
                        
                        <p style="font-size: 12px; color: #a0aec0;">
                            Si tienes problemas haciendo clic en el botón "Verificar Email", copia y pega la siguiente URL en tu navegador: <br>
                            <a href="%s" style="color: #38b2ac;">%s</a>
                        </p>
                    </div>
                </div>
                """.formatted(link, link, link);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo HTML", e);
        }
    }



    //OLVIDAR CONTRASEÑA
    public void enviarCorreoRecuperacion(String destino, String token) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@fastfoodesi.com");
            helper.setTo(destino);
            helper.setSubject("Restablecer Contraseña - FastFood ESI");

            // Apunta a la vista que crearemos luego para cambiar la pass
            String link = "http://localhost:8080/restablecer-password?token=" + token;

            String htmlContent = """
            <div style="font-family: Arial, sans-serif; background-color: #edf2f7; padding: 40px; color: #718096;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    
                    <h2 style="color: #3d4852; margin-top: 0;">Restablecer Contraseña</h2>
                    <p>Hola,</p>
                    <p>Estás recibiendo este correo porque hemos recibido una solicitud para restablecer la contraseña de tu cuenta.</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #2d3748; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">Restablecer Contraseña</a>
                    </div>
                    
                    <p>Este enlace expirará en 60 minutos.</p>
                    <p>Si no has solicitado un cambio de contraseña, no es necesaria ninguna acción.</p>
                    
                    <br>
                    <p>Saludos,<br>El equipo de FastFood ESI 🍔</p>
                    
                    <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;">
                    
                    <p style="font-size: 12px; color: #a0aec0;">
                        Si tienes problemas con el botón, copia este enlace: <br>
                        <a href="%s" style="color: #38b2ac;">%s</a>
                    </p>
                </div>
            </div>
            """.formatted(link, link, link);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo de recuperación", e);
        }
    }
}