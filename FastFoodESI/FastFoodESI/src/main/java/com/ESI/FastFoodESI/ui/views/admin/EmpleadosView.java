package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Empleado;
import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Propietario;
import com.ESI.FastFoodESI.repository.PropietarioRepository;
import com.ESI.FastFoodESI.service.admin.EmpleadoService;
import com.ESI.FastFoodESI.service.admin.NegocioService;
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

import java.util.List;
import java.util.Optional;

@Route(value = "admin/empleados", layout = PropietarioMainLayout.class)
@PageTitle("Gestión de Empleados | Admin")
@RolesAllowed("PROPRIETARIO")
@SpringComponent
@UIScope
public class EmpleadosView extends VerticalLayout {

    private final Grid<Empleado> grid = new Grid<>(Empleado.class);
    private final EmpleadoService empleadoService;
    private final NegocioService negocioService;
    private final PropietarioRepository propietarioRepository;
    private final EmpleadoForm form;
    private final Dialog dialog = new Dialog();

    @Autowired
    public EmpleadosView(EmpleadoService empleadoService,
                         NegocioService negocioService,
                         PropietarioRepository propietarioRepository,
                         EmpleadoForm form) {
        this.empleadoService = empleadoService;
        this.negocioService = negocioService;
        this.propietarioRepository = propietarioRepository;
        this.form = form;

        this.form.setOnSaveListener(this::updateList);

        addClassName("empleados-view");
        setSizeFull();

        configureDialog();
        configureGrid();
        updateList();

        add(new H2("Gestión de Empleados"), getToolbar(), grid);
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Añadir Empleado", VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addEmpleado());
        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassNames("empleado-grid");
        grid.setSizeFull();
        grid.setColumns("nombre", "apellido", "dni", "salario");
        
        grid.addColumn(e -> e.getNegocio() != null ? e.getNegocio().getNombre() : "Sin Asignar")
            .setHeader("Lugar de Trabajo");
            
        grid.addColumn(e -> e.getTurno() != null ? e.getTurno().getNombre() : "-")
            .setHeader("Turno");

        grid.addComponentColumn(this::createActionsButton).setHeader("Acciones");
        
        grid.asSingleSelect().addValueChangeListener(event -> editEmpleado(event.getValue()));
    }

    private Component createActionsButton(Empleado empleado) {
        Button edit = new Button(VaadinIcon.PENCIL.create(), e -> editEmpleado(empleado));
        Button delete = new Button(VaadinIcon.TRASH.create(), e -> deleteEmpleado(empleado));
        delete.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private void configureDialog() {
        dialog.add(form);
        dialog.setWidth("700px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
    }

    public void updateList() {
        Propietario actual = getCurrentPropietario();
        if (actual != null) {
            grid.setItems(empleadoService.findAllByPropietario(actual));
        }
    }

    private void addEmpleado() {
        Propietario actual = getCurrentPropietario();
        if (actual != null) {
            grid.asSingleSelect().clear();
            
            List<Negocio> misNegocios = negocioService.findAllByPropietario(actual);
            form.setNegociosDisponibles(misNegocios);
            
            Empleado nuevoEmpleado = new Empleado(); 
            
            form.setEmpleado(nuevoEmpleado, dialog);
            dialog.open();
        }
    }

    private void editEmpleado(Empleado empleado) {
        if (empleado == null) return;
        
        Propietario actual = getCurrentPropietario();
        if (actual != null) {
            List<Negocio> misNegocios = negocioService.findAllByPropietario(actual);
            form.setNegociosDisponibles(misNegocios);
            
            form.setEmpleado(empleado, dialog);
            dialog.open();
        }
    }

    private void deleteEmpleado(Empleado empleado) {
        empleadoService.delete(empleado);
        updateList();
        Notification.show("Empleado eliminado", 3000, Notification.Position.BOTTOM_START)
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private Propietario getCurrentPropietario() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String correo = ((UserDetails) principal).getUsername();
            return propietarioRepository.findByCorreo(correo).orElse(null);
        }
        return null;
    }
}