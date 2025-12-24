package com.ESI.FastFoodESI.ui.layout;

import com.ESI.FastFoodESI.ui.views.publico.CartaView;
import com.ESI.FastFoodESI.ui.views.admin.NegociosView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
    }

    private void createHeader() {
        // Logo y Título
        H1 logo = new H1("FastFood ESI");
        logo.getStyle().set("font-size", "20px");
        logo.getStyle().set("margin", "0");

        // Enlace Carta
        RouterLink linkCarta = new RouterLink("Carta", CartaView.class);
        linkCarta.getStyle().set("margin-right", "auto"); // Empuja resto derecha

        // Layout Base
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, linkCarta);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setPadding(true);

        // Detectar Usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si logueado
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {

            String rol = auth.getAuthorities().iterator().next().getAuthority(); // Coger Rol

            // MENÚ SEGÚN ROL
            switch (rol) {
                case "ROLE_PROPIETARIO":
                    header.add(new RouterLink("Panel Admin", com.ESI.FastFoodESI.ui.views.admin.NegociosView.class));
                    break;
                case "ROLE_CLIENTE":
                    header.add(new RouterLink("Mis Pedidos", CartaView.class));
                    break;
                case "ROLE_MOSTRADOR":
                    header.add(new RouterLink("Mostrador", CartaView.class));
                    break;
                case "ROLE_CAMARERO":
                    header.add(new RouterLink("Comandas", CartaView.class));
                    break;
                case "ROLE_COCINA":
                    header.add(new RouterLink("Cocina", CartaView.class));
                    break;
                case "ROLE_REPARTIDOR":
                    header.add(new RouterLink("Reparto", CartaView.class));
                    break;
            }

            // Botón Usuario
            Button btnUser = new Button(auth.getName(), VaadinIcon.USER.create());
            btnUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            // Botón Salir
            Button btnLogout = new Button(VaadinIcon.SIGN_OUT.create(), e ->
                    UI.getCurrent().getPage().setLocation("/logout"));
            btnLogout.addThemeVariants(ButtonVariant.LUMO_ERROR);

            header.add(btnUser, btnLogout);

        } else {
            // Usuario Visitante
            Button btnLogin = new Button("Entrar", VaadinIcon.SIGN_IN.create());
            btnLogin.addClickListener(e -> UI.getCurrent().navigate("login"));
            header.add(btnLogin);
        }

        addToNavbar(header);
    }
}
