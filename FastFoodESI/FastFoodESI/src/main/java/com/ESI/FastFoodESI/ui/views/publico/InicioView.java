package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "", layout = MainLayout.class) // Ruta raíz
@PageTitle("Bienvenido | FastFood ESI")
@AnonymousAllowed // Todo el mundo puede ver esto sin loguearse
public class InicioView extends VerticalLayout {

    public InicioView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1("Bienvenido a FastFood ESI"));
        add(new H3("Selecciona tu perfil de acceso"));

        FlexLayout container = new FlexLayout();
        container.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        container.setJustifyContentMode(JustifyContentMode.CENTER);
        container.getStyle().set("gap", "30px");

        // 2. Recuadro DUEÑO (Lleva al Login normal de Spring Security)
        container.add(crearCard("Soy Dueño", VaadinIcon.BRIEFCASE, "login"));

        // 3. Recuadro ESTABLECIMIENTO (Lleva a la validación de ID)
        container.add(crearCard("Soy Restaurante", VaadinIcon.SHOP, "acceso-negocio"));

        add(container);
    }

    private Component crearCard(String titulo, VaadinIcon icon, String rutaNavegacion) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.setHeight("250px");
        card.setAlignItems(Alignment.CENTER);
        card.setJustifyContentMode(JustifyContentMode.CENTER);
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "15px");
        card.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");
        card.getStyle().set("cursor", "pointer");

        // Efecto visual al pasar ratón
        card.getElement().addEventListener("mouseover", e -> card.getStyle().set("transform", "scale(1.05)"));
        card.getElement().addEventListener("mouseout", e -> card.getStyle().set("transform", "scale(1.0)"));

        Icon i = icon.create();
        i.setSize("60px");
        i.setColor("var(--lumo-primary-color)");

        card.add(i, new Span(titulo));

        card.addClickListener(e -> UI.getCurrent().navigate(rutaNavegacion));

        return card;
    }
}