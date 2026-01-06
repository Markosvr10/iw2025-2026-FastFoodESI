package com.ESI.FastFoodESI.ui.views.empleado;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "hub-empleados")
@PageTitle("Selección de Puesto | FastFood ESI")
@RolesAllowed({ "PROPIETARIO", "ADMIN" }) // La cuenta del local es un Propietario
public class HubRolesView extends VerticalLayout {

    public HubRolesView() {
        addClassName("hub-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button btnLogout = new Button("Cerrar Sesión", VaadinIcon.SIGN_OUT.create());
        btnLogout.addThemeVariants(ButtonVariant.LUMO_ERROR);

        btnLogout.getStyle().set("position", "absolute");
        btnLogout.getStyle().set("top", "20px");
        btnLogout.getStyle().set("right", "20px");
        btnLogout.getStyle().set("cursor", "pointer");

        btnLogout.addClickListener(e -> {
            UI.getCurrent().getPage().setLocation("/logout");
        });

        add(btnLogout);

        add(new H2("¿En qué puesto vas a trabajar hoy?"));

        // Contenedor de los 4 cuadrados
        FlexLayout gridRoles = new FlexLayout();
        gridRoles.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        gridRoles.setJustifyContentMode(JustifyContentMode.CENTER);
        gridRoles.getStyle().set("gap", "30px");

        // Creamos los 4 botones gigantes
        gridRoles.add(crearBotonRol("Cocina", VaadinIcon.CUTLERY, "Cocina"));
        gridRoles.add(crearBotonRol("Barra / Mostrador", VaadinIcon.SHOP, "Mostrador"));
        gridRoles.add(crearBotonRol("Reparto", VaadinIcon.CAR, "Repartidor"));
        gridRoles.add(crearBotonRol("Sala / Camarero", VaadinIcon.USER_CARD, "Camarero"));

        add(gridRoles);
    }

    private Component crearBotonRol(String titulo, VaadinIcon icono, String tipoClase) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("200px");
        card.setHeight("200px");
        card.setAlignItems(Alignment.CENTER);
        card.setJustifyContentMode(JustifyContentMode.CENTER);

        // Estilos "Tarjeta"
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "15px");
        card.getStyle().set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("transition", "transform 0.2s");

        // Efecto Hover (simple)
        card.getElement().addEventListener("mouseover", e -> card.getStyle().set("transform", "scale(1.05)"));
        card.getElement().addEventListener("mouseout", e -> card.getStyle().set("transform", "scale(1.0)"));

        // Icono y Texto
        Icon icon = icono.create();
        icon.setSize("60px");
        icon.setColor("var(--lumo-primary-color)");

        Span text = new Span(titulo);
        text.getStyle().set("font-weight", "bold");
        text.getStyle().set("font-size", "1.2em");
        text.getStyle().set("text-align", "center");

        card.add(icon, text);

        // Al hacer clic
        card.addClickListener(e -> {
            // Guardamos qué rol se ha elegido para filtrar en la siguiente pantalla
            VaadinSession.getCurrent().setAttribute("ROL_SELECCIONADO", tipoClase);
            UI.getCurrent().navigate(SeleccionEmpleadoView.class);
        });

        return card;
    }
}