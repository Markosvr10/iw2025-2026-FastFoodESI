package com.ESI.FastFoodESI.ui.views.empleado;

import com.ESI.FastFoodESI.model.Empleado;
import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.repository.PropietarioRepository;
import com.ESI.FastFoodESI.service.admin.EmpleadoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "seleccion-empleado")
@PageTitle("¿Quién eres? | FastFood ESI")
@RolesAllowed({ "PROPIETARIO", "ADMIN" })
public class SeleccionEmpleadoView extends VerticalLayout implements BeforeEnterObserver {

    private final EmpleadoService empleadoService;
    private final PropietarioRepository propietarioRepository;

    private String rolBuscado; // Ej: "Cocina", "Repartidor"
    private FlexLayout gridEmpleados;

    public SeleccionEmpleadoView(EmpleadoService empleadoService, PropietarioRepository propietarioRepository) {
        this.empleadoService = empleadoService;
        this.propietarioRepository = propietarioRepository;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        add(new Button("← Volver", e -> UI.getCurrent().navigate(HubRolesView.class)));

        gridEmpleados = new FlexLayout();
        gridEmpleados.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        gridEmpleados.setJustifyContentMode(JustifyContentMode.CENTER);
        gridEmpleados.getStyle().set("gap", "20px");
        gridEmpleados.setWidthFull();

        add(new H2("Selecciona tu nombre"), gridEmpleados);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Recuperamos el rol que guardamos en la vista anterior
        rolBuscado = (String) VaadinSession.getCurrent().getAttribute("ROL_SELECCIONADO");

        if (rolBuscado == null) {
            event.forwardTo(HubRolesView.class);
            return;
        }
        cargarEmpleados();
    }

    private void cargarEmpleados() {
        gridEmpleados.removeAll();

        Negocio negocioActivo = (Negocio) VaadinSession.getCurrent().getAttribute("NEGOCIO_ACTIVO");

        if (negocioActivo != null) {

            List<Empleado> todos = empleadoService.findAllByNegocio(negocioActivo);

            // Filtro por Rol (Cocina, Repartidor...)
            List<Empleado> filtrados = todos.stream()
                    .filter(e -> e.getClass().getSimpleName().equalsIgnoreCase(rolBuscado))
                    .collect(Collectors.toList());

            if (filtrados.isEmpty()) {
                add(new H3("No hay empleados de tipo " + rolBuscado));
            }

            for (Empleado e : filtrados) {
                gridEmpleados.add(crearTarjetaEmpleado(e));
            }
        } else {
            // Si no hay negocio en sesión, volvemos al inicio para que metan el ID
            UI.getCurrent().navigate("acceso-negocio");
        }
    }

    private Component crearTarjetaEmpleado(Empleado empleado) {
        Button btn = new Button(empleado.getNombre() + " " + empleado.getApellido());
        btn.setWidth("200px");
        btn.setHeight("100px");
        btn.getStyle().set("font-size", "1.2em");

        // Al hacer clic, pedimos el PIN
        btn.addClickListener(e -> abrirDialogoPin(empleado));

        return btn;
    }

    private void abrirDialogoPin(Empleado empleado) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Hola " + empleado.getNombre());

        VerticalLayout layout = new VerticalLayout();
        PasswordField pinField = new PasswordField("Introduce tu DNI/PIN");
        pinField.focus();

        Button btnEntrar = new Button("Entrar", e -> {
            // Usamos el DNI como contraseña.
            if (empleado.getDni().equalsIgnoreCase(pinField.getValue())) {

                VaadinSession.getCurrent().setAttribute("EMPLEADO_ACTIVO", empleado);

                dialog.close();
                navegarAVistaDeRol();

            } else {
                Notification.show("PIN Incorrecto", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnEntrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEntrar.addClickShortcut(Key.ENTER); // Para poder dar a Enter

        layout.add(pinField, btnEntrar);
        dialog.add(layout);
        dialog.open();
    }

    private void navegarAVistaDeRol() {
        if ("Cocina".equalsIgnoreCase(rolBuscado)) {
            UI.getCurrent().navigate(CocinaView.class);
        } else if ("Repartidor".equalsIgnoreCase(rolBuscado)) {
            UI.getCurrent().navigate(RepartidorView.class);
        } else if ("Camarero".equalsIgnoreCase(rolBuscado)) {
            UI.getCurrent().navigate(CamareroView.class);
        } else {
            UI.getCurrent().navigate(MostradorView.class);
        }
    }
}