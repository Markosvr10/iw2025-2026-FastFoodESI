package com.ESI.FastFoodESI.ui.layouts;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.security.SecurityService;
import com.ESI.FastFoodESI.ui.views.admin.NegociosView;
import com.ESI.FastFoodESI.ui.views.publico.CartaView;
import com.ESI.FastFoodESI.ui.views.cliente.PerfilView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession; // IMPORTANTE: Importar VaadinSession
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    private final ClienteRepository clienteRepository;

    public MainLayout(SecurityService securityService, @Autowired ClienteRepository clienteRepository) {

        this.securityService = securityService;
        this.clienteRepository = clienteRepository;

        createHeader();
    }

    private void createHeader() {
        // --- LOGO INTELIGENTE ---
        Image logo = new Image("images/LogoFastFoodESI.png", "FastFood ESI");
        logo.setHeight("100px");
        logo.getStyle().set("margin-right", "10px");
        logo.getStyle().set("cursor", "pointer"); // Hacemos que parezca clickeable

        // Lógica de redirección inteligente:
        logo.addClickListener(e -> {
            // Recuperamos el negocio guardado en la sesión
            String ultimoNegocio = (String) VaadinSession.getCurrent().getAttribute("ULTIMO_NEGOCIO");

            if (ultimoNegocio != null && !ultimoNegocio.isEmpty()) {
                // Si venimos de un negocio, volvemos a ÉL
                UI.getCurrent().navigate("carta/" + ultimoNegocio);
            } else {
                // Si no, vamos a la general
                UI.getCurrent().navigate("carta");
            }
        });

        // Envolvemos el logo en un layout simple
        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(logoWrapper);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setPadding(true);

        // --- AUTHENTICATION CHECK ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {

            // empujar el menú a la derecha
            Div spacer = new Div();
            header.add(spacer);
            header.expand(spacer);

            String rol = auth.getAuthorities().iterator().next().getAuthority();

            // --- MENÚ CENTRAL SEGÚN ROL ---
            switch (rol) {
                case "ROLE_PROPIETARIO":
                    header.add(new RouterLink("Panel Admin", NegociosView.class));
                    break;
                case "ROLE_CLIENTE":
                    // Cliente no tiene enlaces extra en la barra, todo en su perfil
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

            // mostrar el nombre real
            String nombreMostrar = auth.getName(); // Por defecto el correo/username
            // Buscamos si existe en la tabla clientes para coger su nombre de pila
            Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(auth.getName());
            if (clienteOpt.isPresent()) {
                nombreMostrar = clienteOpt.get().getNombre();
            }

            // Despegable en el perfil
            MenuBar userMenu = new MenuBar();
            userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

            MenuItem userItem = userMenu.addItem(createAvatar(nombreMostrar)); // para que muestre el nombre
            SubMenu subMenu = userItem.getSubMenu();

            // Mi Perfil
            subMenu.addItem("Mi Perfil", e -> {
                UI.getCurrent().navigate(PerfilView.class);
            });

            // Mis Pedidos
            subMenu.addItem("Mis Pedidos", e -> {
                UI.getCurrent().navigate("mis-pedidos");
            });

            // separador visual
            subMenu.add(new Div());

            // CERRAR SESIÓN
            HorizontalLayout logoutLayout = new HorizontalLayout();
            logoutLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            logoutLayout.setSpacing(true);

            Icon iconLogout = VaadinIcon.SIGN_OUT.create();
            iconLogout.setSize("16px");
            Span textLogout = new Span("Cerrar Sesión");
            logoutLayout.getStyle().set("color", "var(--lumo-error-text-color)");
            logoutLayout.add(iconLogout, textLogout);

            subMenu.addItem(logoutLayout, e ->
            // ESTO FUNCIONARÁ AHORA PORQUE HEMOS ACTIVADO EL GET EN SECURITYCONFIG
            UI.getCurrent().getPage().setLocation("/logout"));

            header.add(userMenu);

        } else {
            // --- USUARIO NO LOGUEADO ---
            Div spacer = new Div();
            header.add(spacer);
            header.expand(spacer);

            Button btnLogin = new Button("Entrar", VaadinIcon.SIGN_IN.create());
            btnLogin.addClickListener(e -> UI.getCurrent().navigate("login"));
            header.add(btnLogin);
        }

        addToNavbar(header);
    }

    // icono + nombre
    private HorizontalLayout createAvatar(String nombre) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setAlignItems(FlexComponent.Alignment.CENTER);
        hl.setSpacing(true);
        hl.add(VaadinIcon.USER.create(), new Span(nombre));
        return hl;
    }
}