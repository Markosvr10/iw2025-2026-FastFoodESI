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
public class MostradorView extends VerticalLayout {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final LineaPedidoRepository lineaPedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    // CAMBIO: Usamos el repositorio original TipoRepository
    private final TipoRepository tipoRepository;

    private Negocio negocioActivo;

    // UI General
    private Tabs tabsPrincipales;
    private Div contenedorContenido;

    // Pestaña 1
    private List<LineaPedido> cestaCompra = new ArrayList<>();
    private Grid<Producto> gridProductos;
    private Grid<LineaPedido> gridTicket;
    private Span totalSpan;
    private TextField campoMesa;

    // Pestaña 2
    private Grid<Pedido> gridEntregas;

    public MostradorView(ProductoRepository productoRepository,
            PedidoRepository pedidoRepository,
            LineaPedidoRepository lineaPedidoRepository,
            ClienteRepository clienteRepository,
            EstadoPedidoRepository estadoPedidoRepository,
            TipoRepository tipoRepository) { // Inyectamos TipoRepository

        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.lineaPedidoRepository = lineaPedidoRepository;
        this.clienteRepository = clienteRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.tipoRepository = tipoRepository; // Guardamos

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        negocioActivo = (Negocio) VaadinSession.getCurrent().getAttribute("NEGOCIO_ACTIVO");
        if (negocioActivo == null) {
            UI.getCurrent().navigate("acceso-negocio");
            return;
        }

        configurarPestañas();
        mostrarPestañaNuevoPedido();
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

    private void mostrarPestañaNuevoPedido() {
        contenedorContenido.removeAll();

        HorizontalLayout layoutPrincipal = new HorizontalLayout();
        layoutPrincipal.setSizeFull();
        layoutPrincipal.setSpacing(true);
        layoutPrincipal.setPadding(true);

        layoutPrincipal.add(crearZonaProductos(), crearZonaTicket());
        contenedorContenido.add(layoutPrincipal);
    }

    private Component crearZonaProductos() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("60%");
        layout.setHeightFull();

        H2 titulo = new H2("Catálogo");

        Tabs tabsCategorias = new Tabs();

        List<Tipo> tipos = tipoRepository.findAll();
        Map<Tab, Tipo> tabMap = new HashMap<>();

        Tab tabTodo = new Tab("TODO");
        tabsCategorias.add(tabTodo);

        for (Tipo tipo : tipos) {
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

        gridEntregas.addColumn(p -> p.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader("Hora").setWidth("100px").setFlexGrow(0);

        gridEntregas.addColumn(Pedido::getTipoEntrega).setHeader("Mesa / Ref");

        gridEntregas.addComponentColumn(p -> {
            if (p.isPagado()) {
                Span badge = new Span("PAGADO");
                badge.getElement().getThemeList().add("badge success");
                return badge;
            } else {
                Span badge = new Span("PENDIENTE PAGO");
                badge.getElement().getThemeList().add("badge error");
                return badge;
            }
        }).setHeader("Estado Pago");

        gridEntregas.addColumn(new ComponentRenderer<>(pedido -> {
            HorizontalLayout actions = new HorizontalLayout();

            String nombreEstado = pedido.getEstado() != null ? pedido.getEstado().getNombre() : "";

            if (!pedido.isPagado()) {
                Button btnCobrar = new Button("Cobrar", VaadinIcon.DOLLAR.create());
                btnCobrar.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
                btnCobrar.addClickListener(e -> cobrarPedido(pedido));
                actions.add(btnCobrar);
            }

            if (!"ENTREGADO".equalsIgnoreCase(nombreEstado)) {
                Button btnEntregar = new Button("Entregar", VaadinIcon.CHECK.create());
                btnEntregar.addClickListener(e -> cambiarEstado(pedido, "ENTREGADO"));
                actions.add(btnEntregar);
            }

            if (!"ENTREGADO".equalsIgnoreCase(nombreEstado) && !pedido.isPagado()) {
                Button btnCancelar = new Button(VaadinIcon.CLOSE.create());
                btnCancelar.addThemeVariants(ButtonVariant.LUMO_ERROR);
                btnCancelar.addClickListener(e -> cambiarEstado(pedido, "CANCELADO"));
                actions.add(btnCancelar);
            }

            return actions;
        })).setHeader("Acciones").setWidth("380px");

        layout.add(header, gridEntregas);
        contenedorContenido.add(layout);

        cargarPedidosListos();
    }

    private void cargarPedidosListos() {
        List<Pedido> todos = pedidoRepository.findByNegocio(negocioActivo);

        List<Pedido> visibles = todos.stream()
                .filter(p -> {
                    if (p.getEstado() == null)
                        return false;
                    String estado = p.getEstado().getNombre();
                    boolean pagado = p.isPagado(); // Usamos isPagado()

                    boolean esListo = "LISTO".equalsIgnoreCase(estado);

                    boolean esEntregadoMoroso = "ENTREGADO".equalsIgnoreCase(estado) && !pagado;

                    return esListo || esEntregadoMoroso;
                })
                .sorted(Comparator.comparing(Pedido::getFechaHora)) // Ordenar por hora
                .collect(Collectors.toList());

        gridEntregas.setItems(visibles);

        if (visibles.isEmpty()) {
            Notification.show("Todo limpio: No hay entregas ni cobros pendientes", 3000, Notification.Position.MIDDLE);
        }
    }

    private void cobrarPedido(Pedido pedido) {
        pedido.setPagado(true);
        pedidoRepository.save(pedido);
        Notification.show("Pedido cobrado correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        cargarPedidosListos();
    }

    private void cambiarEstado(Pedido pedido, String nuevoEstadoNombre) {
        EstadoPedido nuevoEstado = estadoPedidoRepository.findByNombre(nuevoEstadoNombre).orElse(null);
        if (nuevoEstado != null) {
            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);
            Notification.show("Pedido marcado como " + nuevoEstadoNombre)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            cargarPedidosListos();
        } else {
            Notification.show("Error: Estado " + nuevoEstadoNombre + " no encontrado")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void cargarProductos(Tipo filtroTipo) {
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
            pedido.setPagado(false);

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

            // --- CORRECCIÓN CLAVE AQUÍ ---
            // 1. Creamos un Set nuevo para las líneas
            Set<LineaPedido> lineasParaGuardar = new HashSet<>();

            // 2. Recorremos la cesta y creamos COPIAS de las líneas vinculándolas al pedido
            for (LineaPedido itemCesta : cestaCompra) {
                LineaPedido lineaBD = new LineaPedido();
                lineaBD.setProducto(itemCesta.getProducto());
                lineaBD.setCantidad(itemCesta.getCantidad());
                lineaBD.setPrecioUnitario(itemCesta.getPrecioUnitario());

                // VINCULACIÓN BIDIRECCIONAL IMPORTANTE
                lineaBD.setPedido(pedido);

                lineasParaGuardar.add(lineaBD);
            }

            // 3. Asignamos las líneas preparadas al pedido
            pedido.setLineas(lineasParaGuardar);

            // 4. Guardamos SOLO el pedido. Hibernate guardará las líneas automáticamente
            // por el CascadeType.ALL
            pedidoRepository.save(pedido);

            // Limpieza
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