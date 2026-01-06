package com.ESI.FastFoodESI.ui.views.empleado;

import com.ESI.FastFoodESI.model.EstadoPedido;
import com.ESI.FastFoodESI.model.LineaPedido;
import com.ESI.FastFoodESI.model.Pedido;
import com.ESI.FastFoodESI.repository.EstadoPedidoRepository;
import com.ESI.FastFoodESI.service.admin.PedidoService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "cocina")
@PageTitle("Cocina | FastFood ESI")
@RolesAllowed({ "COCINA", "PROPIETARIO" })
@SpringComponent
@UIScope
public class CocinaView extends VerticalLayout {

    private final PedidoService pedidoService;
    private final EstadoPedidoRepository estadoPedidoRepository;

    private final VerticalLayout columnaRecibidos;
    private final VerticalLayout columnaEnCocina;

    private final FlexLayout contenedorTarjetasRecibidos;
    private final FlexLayout contenedorTarjetasCocina;

    @Autowired
    public CocinaView(PedidoService pedidoService, EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoService = pedidoService;
        this.estadoPedidoRepository = estadoPedidoRepository;

        addClassName("cocina-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // --- BARRA SUPERIOR ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // 1. Botón Volver
        Button btnVolver = new Button("Cambiar Cocinero", VaadinIcon.USER.create());
        btnVolver.addClickListener(click -> UI.getCurrent().navigate(SeleccionEmpleadoView.class));

        // 2. Botón Refrescar
        Button refreshButton = new Button("Actualizar Tablero", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> updateList());

        HorizontalLayout botonesDerecha = new HorizontalLayout(btnVolver, refreshButton);
        header.add(new H2("Monitor de Cocina (KDS)"), botonesDerecha);

        add(header);

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);

        // Columna Izquierda (RECIBIDOS)
        columnaRecibidos = new VerticalLayout();
        columnaRecibidos.addClassName("columna-recibidos");
        columnaRecibidos.getStyle().set("background-color", "#fff0f0");
        columnaRecibidos.setHeightFull();
        columnaRecibidos.setWidth("50%");

        contenedorTarjetasRecibidos = crearContenedorTarjetas();
        columnaRecibidos.add(new H3("🔔 NUEVOS / RECIBIDOS"), new Hr(), contenedorTarjetasRecibidos);

        // Columna Derecha (EN COCINA)
        columnaEnCocina = new VerticalLayout();
        columnaEnCocina.addClassName("columna-cocina");
        columnaEnCocina.getStyle().set("background-color", "#f0f8ff");
        columnaEnCocina.setHeightFull();
        columnaEnCocina.setWidth("50%");

        contenedorTarjetasCocina = crearContenedorTarjetas();
        columnaEnCocina.add(new H3("🔥 EN PREPARACIÓN"), new Hr(), contenedorTarjetasCocina);

        mainLayout.add(columnaRecibidos, columnaEnCocina);
        add(mainLayout);

        updateList();
    }

    private FlexLayout crearContenedorTarjetas() {
        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        layout.setWidthFull();
        layout.getStyle().set("overflow-y", "auto");
        layout.getStyle().set("gap", "15px");
        layout.getStyle().set("padding", "10px");
        return layout;
    }

    public void updateList() {
        contenedorTarjetasRecibidos.removeAll();
        contenedorTarjetasCocina.removeAll();

        List<Pedido> pedidos = pedidoService.findPedidosCocina();

        if (pedidos.isEmpty()) {
            Notification.show("No hay comandas activas", 2000, Notification.Position.MIDDLE);
            return;
        }

        for (Pedido p : pedidos) {
            String estado = p.getEstado().getNombre();

            if ("RECIBIDO".equalsIgnoreCase(estado)) {
                contenedorTarjetasRecibidos.add(crearTarjetaComanda(p, true));
            } else if ("EN_COCINA".equalsIgnoreCase(estado)) {
                contenedorTarjetasCocina.add(crearTarjetaComanda(p, false));
            }
        }
    }

    private Component crearTarjetaComanda(Pedido p, boolean esNuevo) {
        VerticalLayout card = new VerticalLayout();
        card.setWidthFull();
        card.setPadding(true);
        card.setSpacing(false);

        card.getStyle().set("border", esNuevo ? "2px solid #ffcccc" : "2px solid #cce5ff");
        card.getStyle().set("border-radius", "8px");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span idSpan = new Span("#" + p.getId().toString().substring(0, 4));
        idSpan.getStyle().set("font-weight", "bold").set("font-size", "1.2em");

        String hora = p.getFechaHora() != null ? p.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "--:--";
        Span horaSpan = new Span(hora);

        header.add(idSpan, horaSpan);

        String infoMesa = p.getTipoEntrega();
        if (infoMesa == null || infoMesa.isEmpty())
            infoMesa = "Mostrador";
        Span mesaSpan = new Span(infoMesa);
        mesaSpan.getStyle().set("font-weight", "bold").set("color", esNuevo ? "#d32f2f" : "#1976d2");
        mesaSpan.getStyle().set("font-size", "1.1em");

        VerticalLayout listaProductos = new VerticalLayout();
        listaProductos.setPadding(false);
        listaProductos.setSpacing(false);
        listaProductos.getStyle().set("margin-top", "10px");

        if (p.getLineas() != null) {
            for (LineaPedido linea : p.getLineas()) {
                Span item = new Span(linea.getCantidad() + "x " + linea.getProducto().getNombre());
                item.getStyle().set("border-bottom", "1px dashed #eee");
                listaProductos.add(item);
            }
        }

        Button btnAccion;
        if (esNuevo) {
            btnAccion = new Button("MARCHAR A COCINA", VaadinIcon.FIRE.create());
            btnAccion.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        } else {
            btnAccion = new Button("TERMINAR / LISTO", VaadinIcon.CHECK.create());
            btnAccion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
        btnAccion.setWidthFull();
        btnAccion.addClickListener(e -> avanzarPedido(p));

        card.add(header, mesaSpan, new Hr(), listaProductos, new Hr(), btnAccion);
        return card;
    }

    private void avanzarPedido(Pedido p) {
        String estadoActual = p.getEstado().getNombre();
        String siguienteEstado = null;

        if ("RECIBIDO".equalsIgnoreCase(estadoActual)) {
            siguienteEstado = "EN_COCINA";
        } else if ("EN_COCINA".equalsIgnoreCase(estadoActual)) {
            siguienteEstado = "LISTO";
        }

        if (siguienteEstado != null) {
            EstadoPedido nuevoEstado = estadoPedidoRepository.findByNombre(siguienteEstado).orElse(null);
            if (nuevoEstado != null) {
                pedidoService.modificarEstado(p.getId(), nuevoEstado);
                Notification.show("Pedido actualizado").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                updateList();
            } else {
                Notification.show("Error: Estado " + siguienteEstado + " no existe")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }
}