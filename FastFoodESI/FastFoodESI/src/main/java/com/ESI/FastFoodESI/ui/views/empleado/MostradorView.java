package com.ESI.FastFoodESI.ui.views.empleado;

import com.ESI.FastFoodESI.model.*;
import com.ESI.FastFoodESI.repository.*;
import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "mostrador", layout = MainLayout.class)
@PageTitle("TPV Mostrador | FastFood ESI")
@RolesAllowed({ "MOSTRADOR", "PROPIETARIO" })
public class MostradorView extends VerticalLayout { // Ahora heredamos de VerticalLayout

    // Repositorios
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final LineaPedidoRepository lineaPedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final TipoProductoRepository tipoProductoRepository;

    // Estado sesión
    private Negocio negocioActivo;

    // UI General
    private Tabs tabsPrincipales;
    private Div contenedorContenido; // Aquí cargaremos una vista u otra

    // --- VARIABLES PARA PESTAÑA 1: NUEVO PEDIDO ---
    private List<LineaPedido> cestaCompra = new ArrayList<>();
    private Grid<Producto> gridProductos;
    private Grid<LineaPedido> gridTicket;
    private Span totalSpan;
    private TextField campoMesa;

    // --- VARIABLES PARA PESTAÑA 2: ENTREGAS ---
    private Grid<Pedido> gridEntregas;

    public MostradorView(ProductoRepository productoRepository,
            PedidoRepository pedidoRepository,
            LineaPedidoRepository lineaPedidoRepository,
            ClienteRepository clienteRepository,
            EstadoPedidoRepository estadoPedidoRepository,
            TipoProductoRepository tipoProductoRepository) {

        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.lineaPedidoRepository = lineaPedidoRepository;
        this.clienteRepository = clienteRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.tipoProductoRepository = tipoProductoRepository;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        negocioActivo = (Negocio) VaadinSession.getCurrent().getAttribute("NEGOCIO_ACTIVO");
        if (negocioActivo == null) {
            UI.getCurrent().navigate("acceso-negocio");
            return;
        }

        configurarPestañas();
        mostrarPestañaNuevoPedido(); // Cargar la primera por defecto
    }

    private void configurarPestañas() {
        tabsPrincipales = new Tabs();
        tabsPrincipales.setWidthFull();

        Tab tabNuevo = new Tab(VaadinIcon.CART.create(), new Span(" Nuevo Pedido"));
        Tab tabEntregas = new Tab(VaadinIcon.PACKAGE.create(), new Span(" Recogidas / Pagos"));

        tabsPrincipales.add(tabNuevo, tabEntregas);

        contenedorContenido = new Div();
        contenedorContenido.setSizeFull();

        tabsPrincipales.addSelectedChangeListener(event -> {
            if (event.getSelectedTab().equals(tabNuevo)) {
                mostrarPestañaNuevoPedido();
            } else {
                mostrarPestañaEntregas();
            }
        });

        add(tabsPrincipales, contenedorContenido);
    }

    // =========================================================
    // PESTAÑA 1: NUEVO PEDIDO
    // =========================================================
    private void mostrarPestañaNuevoPedido() {
        contenedorContenido.removeAll();

        HorizontalLayout layoutPrincipal = new HorizontalLayout();
        layoutPrincipal.setSizeFull();
        layoutPrincipal.setSpacing(true);
        layoutPrincipal.setPadding(true);

        layoutPrincipal.add(crearZonaProductos(), crearZonaTicket());
        contenedorContenido.add(layoutPrincipal);
    }

    // (Este método es igual que antes, solo lo encuadrado en la nueva estructura)
    private Component crearZonaProductos() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("60%");
        layout.setHeightFull();

        H2 titulo = new H2("Catálogo");

        Tabs tabsCategorias = new Tabs();
        List<TipoProducto> tipos = tipoProductoRepository.findAll();
        Map<Tab, TipoProducto> tabMap = new HashMap<>();

        Tab tabTodo = new Tab("TODO");
        tabsCategorias.add(tabTodo);

        for (TipoProducto tipo : tipos) {
            Tab tab = new Tab(tipo.getNombre());
            tabsCategorias.add(tab);
            tabMap.put(tab, tipo);
        }

        gridProductos = new Grid<>(Producto.class, false);
        gridProductos.addColumn(Producto::getNombre).setHeader("Producto").setAutoWidth(true);
        gridProductos.addColumn(p -> p.getImporte() + " €").setHeader("Precio").setFlexGrow(0);
        gridProductos.addComponentColumn(producto -> {
            Button btnAdd = new Button(VaadinIcon.PLUS.create());
            btnAdd.addClickListener(e -> agregarAlTicket(producto));
            return btnAdd;
        }).setFlexGrow(0);

        cargarProductos(null);

        tabsCategorias.addSelectedChangeListener(event -> {
            if (event.getSelectedTab().equals(tabTodo)) {
                cargarProductos(null);
            } else {
                cargarProductos(tabMap.get(event.getSelectedTab()));
            }
        });

        layout.add(titulo, tabsCategorias, gridProductos);
        return layout;
    }

    private Component crearZonaTicket() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("40%");
        layout.setHeightFull();
        layout.getStyle().set("background-color", "#f5f5f5");
        layout.setPadding(true);

        H4 tituloTicket = new H4("Pedido Actual");

        campoMesa = new TextField("Referencia / Mesa");
        campoMesa.setPlaceholder("Ej: Mesa 4");
        campoMesa.setWidthFull();

        gridTicket = new Grid<>(LineaPedido.class, false);
        gridTicket.addColumn(l -> l.getProducto().getNombre()).setHeader("Prod");
        gridTicket.addColumn(LineaPedido::getCantidad).setHeader("Cant").setWidth("60px").setFlexGrow(0);
        gridTicket.addColumn(l -> {
            BigDecimal precio = l.getPrecioUnitario();
            BigDecimal cantidad = BigDecimal.valueOf(l.getCantidad());
            return precio.multiply(cantidad) + " €";
        }).setHeader("Subtotal");

        gridTicket.addComponentColumn(linea -> {
            Button btnDel = new Button(VaadinIcon.TRASH.create());
            btnDel.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            btnDel.addClickListener(e -> {
                cestaCompra.remove(linea);
                actualizarTicket();
            });
            return btnDel;
        }).setWidth("50px").setFlexGrow(0);

        totalSpan = new Span("Total: 0.00 €");
        totalSpan.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

        Button btnFinalizar = new Button("Enviar a Cocina", e -> finalizarPedido());
        btnFinalizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnFinalizar.setWidthFull();

        layout.add(tituloTicket, campoMesa, gridTicket, totalSpan, btnFinalizar);
        return layout;
    }

    // =========================================================
    // PESTAÑA 2: ENTREGAS Y COBROS
    // =========================================================
    private void mostrarPestañaEntregas() {
        contenedorContenido.removeAll();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(true);

        H2 titulo = new H2("Pedidos Listos para Entregar / Cobrar");

        Button btnRefrescar = new Button("Actualizar", VaadinIcon.REFRESH.create());
        btnRefrescar.addClickListener(e -> cargarPedidosListos());

        HorizontalLayout header = new HorizontalLayout(titulo, btnRefrescar);
        header.setAlignItems(Alignment.CENTER);

        gridEntregas = new Grid<>(Pedido.class, false);
        gridEntregas.setSizeFull();

        // Columnas
        gridEntregas.addColumn(p -> p.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader("Hora").setWidth("100px").setFlexGrow(0);

        gridEntregas.addColumn(Pedido::getTipoEntrega).setHeader("Mesa / Ref");

        // Estado Pagado
        gridEntregas.addComponentColumn(p -> {
            if (Boolean.TRUE.equals(p.isPagado())) {
                Span badge = new Span("PAGADO");
                badge.getElement().getThemeList().add("badge success");
                return badge;
            } else {
                Span badge = new Span("PENDIENTE PAGO");
                badge.getElement().getThemeList().add("badge error");
                return badge;
            }
        }).setHeader("Estado Pago");

        // ACCIONES
        gridEntregas.addColumn(new ComponentRenderer<>(pedido -> {
            HorizontalLayout actions = new HorizontalLayout();

            // Botón COBRAR (Solo si no está pagado)
            if (!Boolean.TRUE.equals(pedido.isPagado())) {
                Button btnCobrar = new Button("Cobrar", VaadinIcon.DOLLAR.create());
                btnCobrar.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
                btnCobrar.addClickListener(e -> cobrarPedido(pedido));
                actions.add(btnCobrar);
            }

            // Botón ENTREGAR
            Button btnEntregar = new Button("Entregar", VaadinIcon.CHECK.create());
            btnEntregar.addClickListener(e -> cambiarEstado(pedido, "ENTREGADO"));

            // Botón CANCELAR
            Button btnCancelar = new Button(VaadinIcon.CLOSE.create());
            btnCancelar.addThemeVariants(ButtonVariant.LUMO_ERROR);
            btnCancelar.addClickListener(e -> cambiarEstado(pedido, "CANCELADO"));

            actions.add(btnEntregar, btnCancelar);
            return actions;
        })).setHeader("Acciones").setWidth("350px");

        layout.add(header, gridEntregas);
        contenedorContenido.add(layout);

        cargarPedidosListos();
    }

    private void cargarPedidosListos() {
        // Obtenemos todos los del negocio y filtramos en memoria los que estén LISTO
        List<Pedido> todos = pedidoRepository.findByNegocio(negocioActivo);

        List<Pedido> listos = todos.stream()
                .filter(p -> p.getEstado() != null && "LISTO".equalsIgnoreCase(p.getEstado().getNombre()))
                .collect(Collectors.toList());

        gridEntregas.setItems(listos);

        if (listos.isEmpty()) {
            Notification.show("No hay pedidos listos para recoger", 2000, Notification.Position.MIDDLE);
        }
    }

    private void cobrarPedido(Pedido pedido) {
        pedido.setPagado(true);
        pedidoRepository.save(pedido);
        Notification.show("Pedido cobrado correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        cargarPedidosListos(); // Refrescar para que cambie el badge
    }

    private void cambiarEstado(Pedido pedido, String nuevoEstadoNombre) {
        EstadoPedido nuevoEstado = estadoPedidoRepository.findByNombre(nuevoEstadoNombre).orElse(null);
        if (nuevoEstado != null) {
            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);
            Notification.show("Pedido marcado como " + nuevoEstadoNombre)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            cargarPedidosListos(); // Desaparecerá de la lista porque ya no es LISTO
        } else {
            Notification.show("Error: Estado " + nuevoEstadoNombre + " no encontrado")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // =========================================================
    // LÓGICA COMÚN (CARGA, GUARDADO...)
    // =========================================================
    private void cargarProductos(TipoProducto filtroTipo) {
        List<Producto> productos = productoRepository.findByNegocio(negocioActivo);
        if (filtroTipo != null) {
            productos = productos.stream()
                    .filter(p -> p.getTipo() != null && p.getTipo().getId().equals(filtroTipo.getId()))
                    .collect(Collectors.toList());
        }
        gridProductos.setItems(productos);
    }

    private void agregarAlTicket(Producto producto) {
        Optional<LineaPedido> existente = cestaCompra.stream()
                .filter(l -> l.getProducto().getId().equals(producto.getId()))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + 1);
        } else {
            LineaPedido nuevaLinea = new LineaPedido();
            nuevaLinea.setProducto(producto);
            nuevaLinea.setCantidad(1);
            nuevaLinea.setPrecioUnitario(producto.getImporte());
            cestaCompra.add(nuevaLinea);
        }
        actualizarTicket();
    }

    private void actualizarTicket() {
        gridTicket.setItems(cestaCompra);
        BigDecimal total = BigDecimal.ZERO;
        for (LineaPedido l : cestaCompra) {
            BigDecimal subtotal = l.getPrecioUnitario().multiply(BigDecimal.valueOf(l.getCantidad()));
            total = total.add(subtotal);
        }
        totalSpan.setText(String.format("Total: %s €", total.toString()));
    }

    private void finalizarPedido() {
        if (cestaCompra.isEmpty())
            return;

        try {
            Pedido pedido = new Pedido();
            pedido.setFechaHora(LocalDateTime.now());
            pedido.setNegocio(negocioActivo);
            pedido.setMetodoPago("MOSTRADOR");
            pedido.setPagado(false); // Por defecto NO pagado hasta que lo cobremos en la pestaña 2

            String infoMesa = campoMesa.getValue();
            if (infoMesa == null || infoMesa.isEmpty())
                infoMesa = "BARRA / LOCAL";
            pedido.setTipoEntrega(infoMesa);

            Cliente clienteGenerico = clienteRepository.findByDni("99999999X").orElse(null);
            if (clienteGenerico == null) {
                Notification.show("Error: Cliente Genérico no encontrado")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            pedido.setCliente(clienteGenerico);

            EstadoPedido estadoRecibido = estadoPedidoRepository.findByNombre("RECIBIDO").orElse(null);
            if (estadoRecibido == null)
                estadoRecibido = estadoPedidoRepository.findByNombre("LISTO").orElse(null);
            pedido.setEstado(estadoRecibido);

            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            for (LineaPedido linea : cestaCompra) {
                linea.setPedido(pedidoGuardado);
                lineaPedidoRepository.save(linea);
            }

            cestaCompra.clear();
            campoMesa.clear();
            actualizarTicket();
            Notification.show("Pedido enviado a cocina").addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}