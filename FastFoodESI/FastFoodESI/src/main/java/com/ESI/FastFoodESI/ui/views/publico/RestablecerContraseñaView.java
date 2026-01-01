package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.service.cliente.ClienteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Map;

@Route("restablecer-password")
@PageTitle("Nueva Contraseña | FastFood ESI")
@AnonymousAllowed
public class RestablecerContraseñaView extends VerticalLayout implements BeforeEnterObserver {

    private final ClienteService clienteService;
    private String token; // token que viene de la URL

    private final PasswordField pass1 = new PasswordField("Nueva Contraseña");
    private final PasswordField pass2 = new PasswordField("Confirmar Contraseña");
    private final Button btnGuardar = new Button("Cambiar Contraseña");

    public RestablecerContraseñaView(ClienteService clienteService) {
        this.clienteService = clienteService;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f5f5");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("100%");
        card.setMaxWidth("400px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        H2 titulo = new H2("Restablecer Clave");
        titulo.getStyle().set("text-align", "center");
        titulo.setWidthFull();

        pass1.setWidthFull();
        pass2.setWidthFull();

        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.setWidthFull();

        // Lógica del botón
        btnGuardar.addClickListener(e -> {
            if (pass1.isEmpty() || pass1.getValue().length() < 8) {
                Notification.show("La contraseña debe tener al menos 8 caracteres").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (!pass1.getValue().equals(pass2.getValue())) {
                Notification.show("Las contraseñas no coinciden").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (token == null || token.isEmpty()) {
                Notification.show("Error: Token no válido").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            // servicio
            boolean exito = clienteService.restablecerPassword(token, pass1.getValue());

            if (exito) {
                Notification.show("¡Contraseña actualizada! Inicia sesión.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                getUI().ifPresent(ui -> ui.navigate("login"));
            } else {
                Notification.show("El enlace ha caducado o no es válido.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        card.add(titulo, pass1, pass2, btnGuardar);
        add(card);
    }

    // Capturar token de la URL
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();

        if (params.containsKey("token")) {
            this.token = params.get("token").get(0);
        } else {
            btnGuardar.setEnabled(false);
            Notification.show("Enlace inválido. Falta el token.").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}