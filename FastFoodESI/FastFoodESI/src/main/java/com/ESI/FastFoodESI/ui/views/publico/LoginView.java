package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", layout = MainLayout.class)
@PageTitle("Iniciar Sesión | FastFoodESI")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final EmailField email = new EmailField("Correo");
    private final PasswordField password = new PasswordField("Contraseña");

    public LoginView() {
        addClassName("login-view");
        setSizeFull();

        setPadding(false);
        setMargin(false);
        setSpacing(false);

        // 1. FONDO Y ALINEACIÓN
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f5f5");

        // 2. CREAMOS LA TARJETA
        VerticalLayout card = new VerticalLayout();
        card.setWidth("100%");
        card.setMaxWidth("500px"); // Un poco más ancha para que respire
        card.setPadding(true);
        card.setSpacing(true);

        // Estilos de la tarjeta
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        // IMPORTANTE: Esto hace que los campos se estiren al 100% del ancho
        card.setAlignItems(Alignment.STRETCH);

        // 3. TÍTULOS
        H1 title = new H1("¡Hola de nuevo! 👋");
        title.getStyle().set("font-size", "28px"); // Más grande
        title.getStyle().set("margin-bottom", "0");
        title.getStyle().set("text-align", "center");

        H2 subTitle = new H2("Acceso a tu cuenta");
        subTitle.getStyle().set("font-size", "16px");
        subTitle.getStyle().set("color", "gray");
        subTitle.getStyle().set("margin-top", "0");
        subTitle.getStyle().set("text-align", "center");

        // 4. CAMPOS DEL FORMULARIO (Personalizados)
        email.setPlaceholder("nombre@ejemplo.com");
        email.setWidthFull();
        email.setClearButtonVisible(true);
        email.setErrorMessage("Por favor, introduce un correo válido");

        password.setPlaceholder("Tu contraseña");
        password.setWidthFull();

        // Botón de Entrar
        Button btnEntrar = new Button("Entrar");
        btnEntrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrar.setWidthFull();
        btnEntrar.getStyle().set("font-size", "16px"); // Texto más grande
        btnEntrar.getStyle().set("padding", "12px");   // Botón más gordito

        // --- LÓGICA DE LOGIN MANUAL ---
        // Esto permite validar SOLO cuando tú quieras (al hacer click)
        btnEntrar.addClickListener(e -> {
            if (email.isEmpty()) {
                email.setInvalid(true);
                email.setErrorMessage("El correo es obligatorio");
                return;
            }
            if (password.isEmpty()) {
                password.setInvalid(true);
                password.setErrorMessage("La contraseña es obligatoria");
                return;
            }

            // Si todo está relleno, hacemos el submit "trampa" a Spring Security
            // Esto crea un formulario oculto y lo envía, simulando el Login normal
            UI.getCurrent().getPage().executeJs(
                    "const form = document.createElement('form');" +
                            "form.method = 'POST';" +
                            "form.action = 'login';" + // La ruta de login de Spring Security
                            "const u = document.createElement('input'); u.name='username'; u.value=$0;" +
                            "const p = document.createElement('input'); p.name='password'; p.value=$1;" +
                            "form.appendChild(u); form.appendChild(p);" +
                            "document.body.appendChild(form);" +
                            "form.submit();",
                    email.getValue(), password.getValue()
            );
        });

        // Permitir pulsar ENTER para entrar
        btnEntrar.addClickShortcut(com.vaadin.flow.component.Key.ENTER);


        // 5. FOOTER (Links)
        RouterLink forgotLink = new RouterLink("¿Has olvidado tu contraseña?", OlvidarContraseñaView.class);
        forgotLink.getStyle().set("font-size", "0.95em");

        RouterLink registerLink = new RouterLink("¿No tienes cuenta? Regístrate aquí", RegistroView.class);
        registerLink.getStyle().set("font-size", "0.95em");
        registerLink.getStyle().set("font-weight", "bold");

        VerticalLayout footer = new VerticalLayout(forgotLink, registerLink);
        footer.setAlignItems(Alignment.CENTER);
        footer.setSpacing(false);
        footer.setPadding(false);
        footer.getStyle().set("margin-top", "15px");

        // 6. AÑADIR TODO
        card.add(title, subTitle, email, password, btnEntrar, footer);
        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // Capturar errores de Spring Security (ej: contraseña mal)
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            // Mostramos una notificación bonita en vez de ensuciar el formulario
            Notification.show("Usuario o contraseña incorrectos")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);

            // Opcional: poner los campos en rojo
            email.setInvalid(true);
            password.setInvalid(true);
        }
    }
}