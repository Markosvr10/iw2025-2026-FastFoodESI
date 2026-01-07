package com.ESI.FastFoodESI.ui.views.empleado;

import com.ESI.FastFoodESI.model.EstadoPedido;
import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Pedido;
import com.ESI.FastFoodESI.repository.EstadoPedidoRepository;
import com.ESI.FastFoodESI.repository.PedidoRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "repartidor")
@PageTitle("Reparto | FastFood ESI")
@RolesAllowed({ "REPARTIDOR", "PROPIETARIO" })
public class RepartidorView extends VerticalLayout {

    private final PedidoRepository pedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final Grid<Pedido> gridPedidos = new Grid<>(Pedido.class, false);

    public RepartidorView(PedidoRepository pedidoRepository, EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // --- HEADER CON BOTÓN VOLVER ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        H2 titulo = new H2("Pedidos a Domicilio Pendientes");

        // Botón para cambiar de usuario
        Button btnVolver = new Button("Cambiar Repartidor", VaadinIcon.USER.create());
        btnVolver.addClickListener(e -> UI.getCurrent().navigate(SeleccionEmpleadoView.class));

        header.add(titulo, btnVolver);
        add(header);
        // -------------------------------

        configurarGrid();
        cargarPedidos();

        add(gridPedidos);
    }

    private void configurarGrid() {
        gridPedidos.setSizeFull();

        // 1. Protección en FECHA
        gridPedidos.addColumn(p -> {
            if (p.getFechaHora() == null)
                return "--:--";
            return p.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm"));
        }).setHeader("Hora").setWidth("100px").setFlexGrow(0);

        // 2. Protección en CLIENTE
        gridPedidos.addColumn(p -> {
            if (p.getCliente() == null)
                return "Cliente Anónimo";
            return p.getCliente().getNombre();
        }).setHeader("Cliente");

        // 3. Dirección simulada
        gridPedidos.addColumn(p -> "Calle Ejemplo, 123 (Simulado)")
                .setHeader("Dirección Entrega").setAutoWidth(true);

        // 4. Protección en ESTADO
        gridPedidos.addColumn(p -> {
            if (p.getEstado() == null)
                return "Sin Estado";
            return p.getEstado().getNombre();
        }).setHeader("Estado Actual");

        // Columna de Botones
        gridPedidos.addColumn(new ComponentRenderer<>(pedido -> {
            HorizontalLayout buttons = new HorizontalLayout();

            Button btnEntregado = new Button("Entregado");
            btnEntregado.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            btnEntregado.addClickListener(e -> actualizarEstado(pedido, "ENTREGADO"));

            Button btnCancelado = new Button("Fallido/Cancelado");
            btnCancelado.addThemeVariants(ButtonVariant.LUMO_ERROR);
            btnCancelado.addClickListener(e -> actualizarEstado(pedido, "CANCELADO"));

            buttons.add(btnEntregado, btnCancelado);
            return buttons;
        })).setHeader("Acciones").setWidth("300px");
    }

    private void cargarPedidos() {
        Negocio negocioActivo = (Negocio) VaadinSession.getCurrent().getAttribute("NEGOCIO_ACTIVO");

        if (negocioActivo != null) {
            List<Pedido> pedidos = pedidoRepository.findByNegocio(negocioActivo);

            List<Pedido> filtrados = pedidos.stream()
                    .filter(p -> "A Domicilio".equalsIgnoreCase(p.getTipoEntrega()))
                    .filter(p -> p.getEstado() != null && !p.getEstado().getNombre().equals("ENTREGADO"))
                    .filter(p -> p.getEstado() != null && !p.getEstado().getNombre().equals("CANCELADO"))
                    .collect(Collectors.toList());

            gridPedidos.setItems(filtrados);

            if (filtrados.isEmpty()) {
                Notification.show("No hay repartos pendientes", 3000, Notification.Position.MIDDLE);
            }
        } else {
            UI.getCurrent().navigate("acceso-negocio");
        }
    }

    private void actualizarEstado(Pedido pedido, String nuevoEstadoNombre) {
        EstadoPedido nuevoEstado = estadoPedidoRepository.findByNombre(nuevoEstadoNombre).orElse(null);

        if (nuevoEstado != null) {
            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);

            Notification.show("Pedido marcado como " + nuevoEstadoNombre)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            cargarPedidos();
        } else {
            Notification.show("Error: Estado " + nuevoEstadoNombre + " no encontrado en BD",
                    3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}