package com.ESI.FastFoodESI.ui.layouts.admin;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.ESI.FastFoodESI.ui.views.admin.NegociosView;
import com.ESI.FastFoodESI.ui.views.admin.EmpleadosView;
import com.ESI.FastFoodESI.ui.views.admin.EstadisticasView;
import com.ESI.FastFoodESI.ui.views.admin.ProductosView;

public class PropietarioMainLayout extends AppLayout {
    
    private final AccessAnnotationChecker checker; 

    public PropietarioMainLayout(AccessAnnotationChecker checker) {
        this.checker = checker;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Panel de Propietario");
        logo.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.MEDIUM);

        addToNavbar(new DrawerToggle(), logo);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();
        
        menu.add(
            new RouterLink("🍔 Mis Negocios", NegociosView.class),
            new RouterLink("🧑‍💼 Empleados", EmpleadosView.class),
            new RouterLink("📊 Estadísticas", EstadisticasView.class),
            new RouterLink("📋 Gestión de Carta", ProductosView.class)
        );
        
        menu.add(
            new com.vaadin.flow.component.html.Anchor("/logout", "Cerrar Sesión") 
        );

        addToDrawer(menu);
    }
}
