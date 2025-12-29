package com.ESI.FastFoodESI.ui.views.cliente;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.security.SecurityService;
import com.ESI.FastFoodESI.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Route(value = "perfil", layout = MainLayout.class)
@PageTitle("Mi Perfil | FastFood ESI")
@RolesAllowed("CLIENTE")
public class PerfilView extends VerticalLayout {

    private final ClienteRepository clienteRepository;
    private final SecurityService securityService;

    // Campos
    private final TextField nombre = new TextField("Nombre");
    private final TextField apellido = new TextField("Apellido");
    private final TextField telefono = new TextField("Teléfono");
    private final TextField dni = new TextField("DNI");
    private final EmailField correo = new EmailField("Correo Electrónico");
    private final PasswordField password = new PasswordField("Nueva Contraseña");
    private final PasswordField passwordConfirm = new PasswordField("Repetir Contraseña");

    private Cliente clienteActual;

    public PerfilView(ClienteRepository clienteRepository, SecurityService securityService) {
        this.clienteRepository = clienteRepository;
        this.securityService = securityService;

        setSizeFull();

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout tarjetaCentral = new VerticalLayout();
        tarjetaCentral.setWidth("800px");
        tarjetaCentral.setMaxWidth("90%");
        tarjetaCentral.setPadding(true);
        tarjetaCentral.setSpacing(true);
        // tarjetaCentral.getStyle().set("box-shadow", "0 0 10px rgba(0,0,0,0.1)").set("border-radius", "10px").set("background", "white");

        H2 titulo = new H2("👤 Mis Datos");
        titulo.getStyle().set("text-align", "center");
        titulo.setWidthFull();

        configurarCampos();

        //Formulario
        FormLayout formLayout = new FormLayout();
        formLayout.add(dni, correo, nombre, apellido, telefono, password, passwordConfirm);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),      // Si es pantalla pequeña -> 1 columna
                new FormLayout.ResponsiveStep("500px", 2)   // Si es pantalla normal -> 2 columnas
        );

        //Botones
        Button btnGuardar = new Button("Guardar Cambios", VaadinIcon.CHECK.create());
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.setWidthFull();
        btnGuardar.addClickListener(e -> guardarDatos());

        Button btnLogout = new Button("Cerrar Sesión", VaadinIcon.SIGN_OUT.create());
        btnLogout.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnLogout.setWidthFull();
        btnLogout.addClickListener(e -> cerrarSesion());

        tarjetaCentral.add(titulo, formLayout, btnGuardar, btnLogout);

        add(tarjetaCentral);
        cargarDatosCliente();
    }

    private void configurarCampos() {
        dni.setReadOnly(true);
        correo.setReadOnly(true);
        password.setPlaceholder("Escribe solo si quieres cambiarla");
        password.setRevealButtonVisible(true);
        passwordConfirm.setPlaceholder("Repítela para confirmar");
        passwordConfirm.setRevealButtonVisible(true);
    }

    private void cargarDatosCliente() {
        UserDetails user = securityService.getAuthenticatedUser();
        if (user != null) {
            Optional<Cliente> c = clienteRepository.findByCorreo(user.getUsername());
            if (c.isPresent()) {
                clienteActual = c.get();
                nombre.setValue(clienteActual.getNombre());
                apellido.setValue(clienteActual.getApellido());
                dni.setValue(clienteActual.getDni());
                correo.setValue(clienteActual.getCorreo());
                if (clienteActual.getTelefono() != null) telefono.setValue(clienteActual.getTelefono());
            }
        }
    }

    private void guardarDatos() {
        if (clienteActual == null) return;
        if (nombre.isEmpty() || apellido.isEmpty()) {
            Notification.show("Nombre y Apellido obligatorios").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        String pass1 = password.getValue();
        String pass2 = passwordConfirm.getValue();

        if (!pass1.isEmpty() || !pass2.isEmpty()) {
            if (!pass1.equals(pass2)) {
                Notification.show("Las contraseñas no coinciden").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            clienteActual.setPassword(pass1);
        }

        clienteActual.setNombre(nombre.getValue());
        clienteActual.setApellido(apellido.getValue());
        clienteActual.setTelefono(telefono.getValue());

        try {
            clienteRepository.save(clienteActual);
            Notification.show("¡Perfil actualizado!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            password.clear();
            passwordConfirm.clear();
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void cerrarSesion() {
        UI.getCurrent().getPage().setLocation("/logout");
        VaadinSession.getCurrent().getSession().invalidate();
    }
}