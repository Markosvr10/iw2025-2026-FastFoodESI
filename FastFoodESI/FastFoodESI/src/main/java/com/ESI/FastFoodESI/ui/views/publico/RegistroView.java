package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.service.cliente.ClienteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("registro")
@PageTitle("Registro | FastFood ESI")
@AnonymousAllowed // q sea publica
public class RegistroView extends VerticalLayout {

    private final ClienteService clienteService;

    // vincular los campos del formulario con el objeto Cliente
    private final BeanValidationBinder<Cliente> binder = new BeanValidationBinder<>(Cliente.class);

    public RegistroView(ClienteService clienteService) {
        this.clienteService = clienteService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        getStyle().set("background-color", "#f5f5f5");

        // 1. Crear la tarjeta (contenedor blanco)
        VerticalLayout card = new VerticalLayout();
        card.setWidth("100%");
        card.setMaxWidth("500px");
        card.setPadding(true);
        card.setSpacing(true);

        // Estilos CSS de la tarjeta
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        // 2. Título dentro de la tarjeta
        H2 titulo = new H2("Crea tu cuenta");
        titulo.getStyle().set("text-align", "center");
        titulo.setWidthFull();

        // 3. Añadir todo A LA TARJETA
        card.add(titulo, createForm());

        // 4. Añadir SOLO LA TARJETA a la vista principal
        add(card);
    }

    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("500px");

        TextField nombre = new TextField("Nombre");
        TextField apellido = new TextField("Apellido");
        TextField dni = new TextField("DNI");
        TextField telefono = new TextField("Teléfono");
        EmailField correo = new EmailField("Correo Electrónico");
        PasswordField password = new PasswordField("Contraseña");
        PasswordField confirmPassword = new PasswordField("Confirmar Contraseña");

        // enlazamos campos con propiedades del modelo Cliente
        binder.bind(nombre, "nombre");
        binder.bind(apellido, "apellido");
        binder.bind(dni, "dni");
        binder.bind(telefono, "telefono");
        binder.bind(correo, "correo");

        // min 8 caracteres
        binder.forField(password)
                .asRequired("La contraseña es obligatoria")
                .withValidator(p -> p.length() >= 8, "Debe tener al menos 8 caracteres")
                .bind("password");

        Button btnRegistrar = new Button("Registrarse");
        btnRegistrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRegistrar.setWidthFull();

        btnRegistrar.addClickListener(e -> {
            // q coincidan
            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Las contraseñas no coinciden")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // ejecutar validación del Binder y guardar
            Cliente nuevoCliente = new Cliente();
            if (binder.writeBeanIfValid(nuevoCliente)) {
                try {
                    clienteService.registrarCliente(nuevoCliente);
                    Notification.show("Registro exitoso. 📧 Revisa tu email para activar la cuenta.")
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    // redirigir al login
                    getUI().ifPresent(ui -> ui.navigate("login"));

                } catch (Exception ex) {
                    Notification.show("Error: " + ex.getMessage())
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });


        formLayout.add(nombre, apellido, dni, telefono, correo, password, confirmPassword, btnRegistrar);

        formLayout.setColspan(correo, 2);
        formLayout.setColspan(btnRegistrar, 2);

        return formLayout;
    }
}