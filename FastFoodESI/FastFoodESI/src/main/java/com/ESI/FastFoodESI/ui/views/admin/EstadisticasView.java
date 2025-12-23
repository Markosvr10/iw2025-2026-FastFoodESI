package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.ui.layouts.admin.PropietarioMainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/estadisticas", layout = PropietarioMainLayout.class)
@PageTitle("Estadísticas | Admin")
@RolesAllowed("PROPRIETARIO")
public class EstadisticasView extends VerticalLayout {
    public EstadisticasView() {
        add(new H2("Panel de Estadísticas (En construcción)"));
    }
}