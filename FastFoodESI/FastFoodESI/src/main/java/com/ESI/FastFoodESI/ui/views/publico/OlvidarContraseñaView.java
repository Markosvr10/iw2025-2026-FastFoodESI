package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.service.cliente.ClienteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("olvide-password")
@PageTitle("Recuperar Contraseña | FastFood ESI")
@AnonymousAllowed
public class OlvidarContraseñaView extends VerticalLayout {

    public OlvidarContraseñaView(ClienteService clienteService) {
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

        H2 titulo = new H2("Recuperar Contraseña");
        Paragraph info = new Paragraph("Introduce tu correo y te enviaremos un enlace.");

        EmailField email = new EmailField("Correo Electrónico");
        email.setWidthFull();

        Button btnEnviar = new Button("Enviar Enlace");
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEnviar.setWidthFull();

        btnEnviar.addClickListener(e -> {
            if(email.isEmpty() || email.isInvalid()){
                Notification.show("Introduce un correo válido").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // servicio
            clienteService.iniciarRecuperacionPassword(email.getValue());

            Notification.show("Si el correo existe, recibirás un enlace en breve.")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            email.clear();
        });

        RouterLink linkVolver = new RouterLink("Volver al Login", LoginView.class);
        linkVolver.getStyle().set("margin-top", "10px");
        VerticalLayout footer = new VerticalLayout(linkVolver);
        footer.setAlignItems(Alignment.CENTER);
        footer.setPadding(false);

        card.add(titulo, info, email, btnEnviar, footer);
        add(card);
    }
}