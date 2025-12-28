package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Propietario; 
import com.ESI.FastFoodESI.repository.PropietarioRepository;
import com.ESI.FastFoodESI.service.admin.NegocioService;
import com.ESI.FastFoodESI.service.admin.ProductoService;
import com.ESI.FastFoodESI.repository.CartaRepository;
import com.ESI.FastFoodESI.ui.layouts.admin.PropietarioMainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Route(value = "admin/negocios", layout = PropietarioMainLayout.class)
@PageTitle("Mis Negocios | Admin")
@RolesAllowed("PROPIETARIO")
@SpringComponent
@UIScope
public class NegociosView extends VerticalLayout {

    private final Grid<Negocio> grid = new Grid<>(Negocio.class);
    private final NegocioService negocioService;
    private final PropietarioRepository propietarioRepository;
    private final NegocioForm form;
    private final Dialog dialog = new Dialog();
    private final ProductoService productoService;
    private final ProductoForm productoForm;
    private final CartaRepository cartaRepository;

    @Autowired
    public NegociosView(NegocioService negocioService, 
                        PropietarioRepository propietarioRepository,
                        NegocioForm form,
                        ProductoService productoService,
                        ProductoForm productoForm,
                        CartaRepository cartaRepository) {
        this.negocioService = negocioService;
        this.propietarioRepository = propietarioRepository;
        this.form = form;
        this.productoService = productoService;
        this.productoForm = productoForm;
        this.cartaRepository = cartaRepository;

        this.form.setOnSaveListener(this::updateList);

        addClassName("negocios-view");
        setSizeFull();

        configureDialog();
        configureGrid();
        
        updateList();

        add(new H2("Gestión de Mis Negocios"), getToolbar(), grid);
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Añadir Negocio", VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addNegocio());
        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassNames("negocio-grid");
        grid.setSizeFull();
        grid.setColumns("nombre", "direccion", "telefono", "correo");
        
        grid.addColumn(n -> n.getnEmpleados() != null ? n.getnEmpleados() : 0).setHeader("Empleados");

        grid.asSingleSelect().addValueChangeListener(event -> editNegocio(event.getValue()));
        grid.addComponentColumn(this::createActionsButton).setHeader("Acciones");
    }

    private Component createActionsButton(Negocio negocio) {
        Button cartaButton = new Button(VaadinIcon.FILE_TEXT.create());
        cartaButton.addThemeName("secondary");
        cartaButton.setTooltipText("Gestionar Carta");
        cartaButton.addClickListener(e -> openCartaDialog(negocio));

        Button editButton = new Button(VaadinIcon.PENCIL.create());
        editButton.addThemeName("small");
        editButton.addClickListener(e -> editNegocio(negocio));

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addThemeNames("small error tertiary");
        deleteButton.addClickListener(e -> deleteNegocio(negocio));

        return new HorizontalLayout(cartaButton, editButton, deleteButton);
    }

    private void openCartaDialog(Negocio negocio) {
        GestionCartaDialog dialog = new GestionCartaDialog(negocio, productoService, productoForm, cartaRepository);
        dialog.open();
    }

    private void configureDialog() {
        dialog.add(form);
        dialog.setWidth("600px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
    }

    public void updateList() {
        Propietario actual = getCurrentPropietario();
        if (actual != null) {
             grid.setItems(negocioService.findAll().stream()
                 .filter(n -> n.getPropietario() != null && n.getPropietario().getId().equals(actual.getId()))
                 .toList());
        } else {
            grid.setItems(); 
        }
    }

    private void addNegocio() {
        grid.asSingleSelect().clear();
        Negocio nuevoNegocio = new Negocio();
        
        Propietario propietarioActual = getCurrentPropietario();
        
        if (propietarioActual != null) {
            nuevoNegocio.setPropietario(propietarioActual);
            form.setNegocio(nuevoNegocio, dialog);
            dialog.open();
        } else {
            Notification.show("Error: No se pudo identificar al usuario logueado", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void editNegocio(Negocio negocio) {
        if (negocio == null) return;
        form.setNegocio(negocio, dialog);
        dialog.open();
    }

    private void deleteNegocio(Negocio negocio) {
        try {
            negocioService.delete(negocio);
            updateList();
            Notification.show("Negocio eliminado.", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error al eliminar: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Propietario getCurrentPropietario() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            String correo = ((UserDetails) principal).getUsername();
            Optional<Propietario> prop = propietarioRepository.findByCorreo(correo);
            return prop.orElse(null);
        }
        return null;
    }
}