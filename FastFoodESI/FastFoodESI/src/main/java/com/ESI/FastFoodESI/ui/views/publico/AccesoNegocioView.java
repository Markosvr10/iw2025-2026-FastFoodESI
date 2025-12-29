package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.repository.NegocioRepository;
import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.List;

@Route(value = "acceso-negocio", layout = MainLayout.class)
@PageTitle("Acceso Restaurante | FastFood ESI")
@AnonymousAllowed
public class AccesoNegocioView extends VerticalLayout {

    private final NegocioRepository negocioRepository;
    private final HttpServletRequest request;

    @Autowired
    public AccesoNegocioView(NegocioRepository negocioRepository, HttpServletRequest request) {
        this.negocioRepository = negocioRepository;
        this.request = request;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H2("Introduce el ID del Establecimiento"));

        // Usamos PasswordField para que el ID no se vea mientras se escribe
        PasswordField idField = new PasswordField("ID de Identificación");
        idField.setWidth("300px");
        idField.setHelperText("Solicita este código al administrador");

        Button btnEntrar = new Button("Entrar");
        btnEntrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrar.addClickShortcut(Key.ENTER);

        btnEntrar.addClickListener(e -> validarYEntrar(idField.getValue()));

        add(idField, btnEntrar);
    }

    private void validarYEntrar(String idTexto) {
        try {
            // 1. Validar formato UUID
            UUID idNegocio = UUID.fromString(idTexto.trim());

            // 2. Buscar el negocio
            Negocio negocio = negocioRepository.findById(idNegocio).orElse(null);

            if (negocio != null) {
                // A) Guardamos el negocio en la sesión
                VaadinSession.getCurrent().setAttribute("NEGOCIO_ACTIVO", negocio);

                // B) LOGIN REAL (Usando los datos que veo en tu captura)
                // Usuario: sistema_tpv
                // Pass: pass123 (¡Esta era la clave!)
                try {
                    request.login("sistema_tpv", "pass123");

                    // Si pasa de aquí, es que el login fue éxito
                    UI.getCurrent().navigate("hub-empleados");

                } catch (ServletException ex) {
                    // Si la contraseña estuviera mal, caería aquí
                    Notification.show("Error de credenciales internas: " + ex.getMessage())
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

            } else {
                Notification.show("El ID es válido pero el negocio no existe.",
                        3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (IllegalArgumentException ex) {
            Notification.show("Código incorrecto (Revísalo, debe ser UUID).",
                    3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}