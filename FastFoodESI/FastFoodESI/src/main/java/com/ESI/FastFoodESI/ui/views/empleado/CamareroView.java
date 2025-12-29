package com.ESI.FastFoodESI.ui.views.empleado;

import com.ESI.FastFoodESI.model.*;
import com.ESI.FastFoodESI.repository.*;
import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "camarero", layout = MainLayout.class)
@PageTitle("Comandero | FastFood ESI")
@RolesAllowed({ "CAMARERO", "PROPIETARIO" })
public class CamareroView extends HorizontalLayout {

    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final LineaPedidoRepository lineaPedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    // CAMBIO: Usamos TipoRepository
    private final TipoRepository tipoRepository;

    private Negocio negocioActivo;

    private List<LineaPedido> cestaCompra = new ArrayList<>();

    private Grid<Producto> gridProductos;
    private Grid<LineaPedido> gridTicket;
    private Span totalSpan;
    private TextField campoMesa;

    public CamareroView(ProductoRepository productoRepository,
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
        setSpacing(true);
        setPadding(true);

        negocioActivo = (Negocio) VaadinSession.getCurrent().getAttribute("NEGOCIO_ACTIVO");
        if (negocioActivo == null) {
            UI.getCurrent().navigate("acceso-negocio");
            return;
        }

        add(crearZonaProductos(), crearZonaTicket());
    }

    private Component crearZonaProductos() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("60%");
        layout.setHeightFull();

        H2 titulo = new H2("Carta / Menú");

        Tabs tabs = new Tabs();

        List<Tipo> tipos = tipoRepository.findAll();
        Map<Tab, Tipo> tabMap = new HashMap<>();

        Tab tabTodo = new Tab("TODO");
        tabs.add(tabTodo);

        for (Tipo tipo : tipos) {
            Tab tab = new Tab(tipo.getNombre());
            tabs.add(tab);
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

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab().equals(tabTodo)) {
                cargarProductos(null);
            } else {
                cargarProductos(tabMap.get(event.getSelectedTab()));
            }
        });

        layout.add(titulo, tabs, gridProductos);
        return layout;
    }

    private Component crearZonaTicket() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("40%");
        layout.setHeightFull();
        layout.getStyle().set("background-color", "#f0f0f0");
        layout.setPadding(true);

        H4 tituloTicket = new H4("Comanda Actual");

        campoMesa = new TextField("Nº Mesa");
        campoMesa.setPlaceholder("Ej: Mesa 10");
        campoMesa.setAutofocus(true);
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

        Button btnFinalizar = new Button("Marchar a Cocina", e -> finalizarPedido());
        btnFinalizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnFinalizar.setIcon(VaadinIcon.CHECK.create());
        btnFinalizar.setWidthFull();

        layout.add(tituloTicket, campoMesa, gridTicket, totalSpan, btnFinalizar);
        return layout;
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
        if (cestaCompra.isEmpty()) {
            Notification.show("La comanda está vacía").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        String infoMesa = campoMesa.getValue();
        if (infoMesa == null || infoMesa.trim().isEmpty()) {
            Notification.show("¡Falta el número de Mesa!").addThemeVariants(NotificationVariant.LUMO_ERROR);
            campoMesa.focus();
            return;
        }

        try {
            Pedido pedido = new Pedido();
            pedido.setFechaHora(LocalDateTime.now());
            pedido.setNegocio(negocioActivo);
            pedido.setMetodoPago("EFECTIVO/TARJETA");
            pedido.setPagado(false);
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

            // --- CORRECCIÓN CLAVE ---
            Set<LineaPedido> lineasParaGuardar = new HashSet<>();

            for (LineaPedido itemCesta : cestaCompra) {
                LineaPedido lineaBD = new LineaPedido();
                lineaBD.setProducto(itemCesta.getProducto());
                lineaBD.setCantidad(itemCesta.getCantidad());
                lineaBD.setPrecioUnitario(itemCesta.getPrecioUnitario());

                // VINCULAR
                lineaBD.setPedido(pedido);

                lineasParaGuardar.add(lineaBD);
            }

            pedido.setLineas(lineasParaGuardar);

            // GUARDAR PADRE (y cascada hijos)
            pedidoRepository.save(pedido);

            cestaCompra.clear();
            campoMesa.clear();
            actualizarTicket();
            Notification.show("Comanda enviada a cocina (Mesa: " + infoMesa + ")")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error al guardar: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}