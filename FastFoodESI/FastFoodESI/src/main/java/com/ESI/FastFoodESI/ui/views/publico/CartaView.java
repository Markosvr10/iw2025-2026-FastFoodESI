package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo;
import com.ESI.FastFoodESI.service.cliente.CarritoService;
import com.ESI.FastFoodESI.service.cliente.MenuService;
import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// La ruta base es "carta". La URL final será localhost:8080/carta/NombreDelNegocio
@Route(value = "carta", layout = MainLayout.class)
@PageTitle("Carta | FastFood ESI")
@AnonymousAllowed
public class CartaView extends VerticalLayout implements HasUrlParameter<String> {

    private final MenuService menuService;
    private final CarritoService carritoService;

    private List<Producto> productosDelNegocio;
    private String nombreNegocioActual = "Todos";

    // Componentes ui
    private final FlexLayout contenedorTarjetas;
    private final TextField buscador;
    private final Tabs tabsCategorias;
    private final H2 tituloCarta;

    public CartaView(MenuService menuService, CarritoService carritoService) {
        this.menuService = menuService;
        this.carritoService = carritoService;
        this.productosDelNegocio = new ArrayList<>();

        // configuración visual base
        setSizeFull();
        setPadding(false);
        setSpacing(true);
        setMaxWidth("1200px");
        getStyle().set("margin", "0 auto");

        // --- COMPONENTES ---

        // Título dinámico del negocio
        tituloCarta = new H2("Cargando carta...");
        tituloCarta.getStyle().set("margin-left", "20px");

        // Buscador
        buscador = new TextField();
        buscador.setPlaceholder("Buscar en esta carta...");
        buscador.setPrefixComponent(VaadinIcon.SEARCH.create());
        buscador.setClearButtonVisible(true);
        buscador.setWidthFull();
        buscador.setValueChangeMode(ValueChangeMode.LAZY);
        buscador.addValueChangeListener(e -> filtrarProductos());

        // Botón Carrito
        Button btnCarrito = new Button("Ver Carrito", VaadinIcon.CART.create());
        btnCarrito.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCarrito.addClickListener(e -> UI.getCurrent().navigate("carrito"));

        // Barra Superior
        HorizontalLayout topBar = new HorizontalLayout(buscador, btnCarrito);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);
        topBar.expand(buscador);
        topBar.setPadding(true);

        // Pestañas (Se llenarán dinámicamente en setParameter)
        tabsCategorias = new Tabs();
        tabsCategorias.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabsCategorias.setWidthFull();
        tabsCategorias.addSelectedChangeListener(e -> filtrarProductos());

        // Contenedor Tarjetas
        contenedorTarjetas = new FlexLayout();
        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        contenedorTarjetas.getStyle().set("gap", "20px");
        contenedorTarjetas.setJustifyContentMode(JustifyContentMode.CENTER);
        contenedorTarjetas.getStyle().set("padding", "20px");

        // Scroll
        Scroller scroller = new Scroller(contenedorTarjetas);
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

        add(tituloCarta, topBar, tabsCategorias, scroller);
        expand(scroller);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        tabsCategorias.removeAll();

        // --- CAMBIO: LÓGICA DE MEMORIA DE SESIÓN ---
        // Guardamos el parámetro "crudo" (ej: FastFoodESI_Cádiz) en la sesión
        // para que el MainLayout sepa a dónde volver.
        if (parameter == null || parameter.isEmpty()) {
            // Si estamos en la general, borramos la memoria
            VaadinSession.getCurrent().setAttribute("ULTIMO_NEGOCIO", null);

            this.nombreNegocioActual = "Todos los Negocios";
            this.productosDelNegocio = menuService.obtenerTodosLosProductos();
            tituloCarta.setText("Carta de " + nombreNegocioActual);
        } else {
            // Si estamos en un negocio, lo recordamos
            VaadinSession.getCurrent().setAttribute("ULTIMO_NEGOCIO", parameter);

            String nombreParaBusqueda = parameter.replace("_", " ");
            this.productosDelNegocio = menuService.obtenerProductosPorNegocio(nombreParaBusqueda);

            this.nombreNegocioActual = nombreParaBusqueda;
            tituloCarta.setText("Carta de " + nombreNegocioActual);

            if (productosDelNegocio.isEmpty()) {
                Notification.show("No se encontraron productos para: " + nombreParaBusqueda)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
        // -------------------------------------------

        generarPestañasDinamicas();
        filtrarProductos();
    }

    private void generarPestañasDinamicas() {
        tabsCategorias.removeAll();

        // Pestaña por defecto
        Tab tabTodos = new Tab("Todos");
        tabsCategorias.add(tabTodos);

        // Extraemos las categorías únicas de los productos que acabamos de cargar
        List<String> categoriasDisponibles = productosDelNegocio.stream()
                .map(p -> p.getTipo().getNombre())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (String nombreCat : categoriasDisponibles) {
            Tab tab = new Tab(nombreCat);
            // Usamos el label del tab o un ID para filtrar después
            tabsCategorias.add(tab);
        }
    }

    private void filtrarProductos() {
        contenedorTarjetas.removeAll();

        String textoBusqueda = buscador.getValue().toLowerCase();
        Tab tabSeleccionado = tabsCategorias.getSelectedTab();
        String categoriaSeleccionada = (tabSeleccionado != null) ? tabSeleccionado.getLabel() : "Todos";

        List<Producto> productosFiltrados = productosDelNegocio.stream()
                .filter(p -> {
                    boolean coincideTexto = p.getNombre().toLowerCase().contains(textoBusqueda);
                    boolean coincideCategoria = true;
                    if (!"Todos".equals(categoriaSeleccionada)) {
                        coincideCategoria = p.getTipo().getNombre().equalsIgnoreCase(categoriaSeleccionada);
                    }
                    return coincideTexto && coincideCategoria;
                })
                .collect(Collectors.toList());

        if (productosFiltrados.isEmpty()) {
            contenedorTarjetas.add(new H3("Vaya, no hay productos aquí... 😢"));
        } else {
            for (Producto p : productosFiltrados) {
                contenedorTarjetas.add(crearTarjetaProducto(p));
            }
        }
    }

    private Component crearTarjetaProducto(Producto p) {
        Div imagenDiv = new Div();
        imagenDiv.setWidthFull();
        imagenDiv.setHeight("140px");
        imagenDiv.getStyle().set("background-color", "#f3f3f3");
        imagenDiv.getStyle().set("display", "flex");
        imagenDiv.getStyle().set("align-items", "center");
        imagenDiv.getStyle().set("justify-content", "center");
        imagenDiv.getStyle().set("border-radius", "12px");

        if (p.getImagenUrl() != null && !p.getImagenUrl().isEmpty()) {
            Image img = new Image(p.getImagenUrl(), p.getNombre());
            img.setWidthFull();
            img.setHeight("100%");
            img.getStyle().set("object-fit", "cover");
            img.getStyle().set("border-radius", "12px");
            imagenDiv.add(img);
        } else {
            var icono = VaadinIcon.CUTLERY.create();
            icono.setSize("50px");
            icono.setColor("gray");
            imagenDiv.add(icono);
        }

        Span nombre = new Span(p.getNombre());
        nombre.getStyle().set("font-weight", "bold");
        nombre.getStyle().set("font-size", "1.1em");

        Span precio = new Span(String.format("%.2f €", p.getImporte()));
        precio.getStyle().set("color", "var(--lumo-primary-color)");
        precio.getStyle().set("font-weight", "bold");

        Span descripcion = new Span(p.getDescripcion() != null ? p.getDescripcion() : "");
        descripcion.getStyle().set("font-size", "0.9em");
        descripcion.getStyle().set("color", "gray");
        descripcion.getStyle().set("text-overflow", "ellipsis");
        descripcion.getStyle().set("white-space", "nowrap");
        descripcion.getStyle().set("overflow", "hidden");
        descripcion.setWidth("100%");

        Button btnAdd = new Button("Añadir", VaadinIcon.PLUS.create());
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAdd.setWidthFull();
        btnAdd.addClickListener(e -> {
            carritoService.anadirProducto(p);
            Notification.show(p.getNombre() + " añadido al carrito")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        VerticalLayout card = new VerticalLayout(imagenDiv, nombre, descripcion, precio, btnAdd);
        card.setPadding(true);
        card.setSpacing(true);
        card.setWidth("250px");
        card.getStyle().set("border", "1px solid #e0e0e0");
        card.getStyle().set("border-radius", "16px");
        card.getStyle().set("box-shadow", "0 4px 12px rgba(0,0,0,0.05)");
        card.getStyle().set("background", "white");

        return card;
    }
}