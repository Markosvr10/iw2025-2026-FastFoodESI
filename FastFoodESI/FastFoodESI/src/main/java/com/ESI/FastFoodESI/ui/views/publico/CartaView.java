package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo;
import com.ESI.FastFoodESI.service.cliente.CarritoService;
import com.ESI.FastFoodESI.service.cliente.MenuService;
import com.ESI.FastFoodESI.ui.layout.MainLayout;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Carta | FastFood ESI")
@AnonymousAllowed
public class CartaView extends VerticalLayout {

    private final MenuService menuService;
    private final CarritoService carritoService;
    private List<Producto> todosLosProductos;

    //Componentes ui
    private final FlexLayout contenedorTarjetas;
    private final TextField buscador;
    private final Tabs tabsCategorias;

    //******************************************************************************************************************

    public CartaView(MenuService menuService, CarritoService carritoService) {
        this.menuService = menuService;
        this.carritoService = carritoService;

        this.todosLosProductos = menuService.obtenerTodosLosProductos();
        List<Tipo> categorias = menuService.obtenerTodosLosTipos();

        // configuración visual base
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setMaxWidth("1200px");
        getStyle().set("margin", "0 auto");

        // --- COMPONENTES ---

        // Buscador
        buscador = new TextField();
        buscador.setPlaceholder("Buscar...");
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

        // Pestañas
        tabsCategorias = new Tabs();
        tabsCategorias.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabsCategorias.setWidthFull();
        tabsCategorias.add(new Tab("Todos"));

        for (Tipo tipo : categorias) {
            Tab tab = new Tab(tipo.getNombre());
            tab.setId(tipo.getNombre());
            tabsCategorias.add(tab);
        }
        tabsCategorias.addSelectedChangeListener(e -> filtrarProductos());

        // Contenedor Tarjetas
        contenedorTarjetas = new FlexLayout();
        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        contenedorTarjetas.getStyle().set("gap", "20px");
        contenedorTarjetas.setJustifyContentMode(JustifyContentMode.CENTER);

        // Carga inicial
        filtrarProductos();

        add(topBar, tabsCategorias, contenedorTarjetas);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void filtrarProductos() {
        contenedorTarjetas.removeAll();

        String textoBusqueda = buscador.getValue().toLowerCase();
        Tab tabSeleccionado = tabsCategorias.getSelectedTab();
        String categoriaSeleccionada = (tabSeleccionado != null) ? tabSeleccionado.getLabel() : "Todos";

        List<Producto> productosFiltrados = todosLosProductos.stream()
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
            contenedorTarjetas.add(new H3("Vaya, no hemos encontrado nada... 😢"));
        } else {
            for (Producto p : productosFiltrados) {
                contenedorTarjetas.add(crearTarjetaProducto(p));
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private Component crearTarjetaProducto(Producto p) {
        Div imagenDiv = new Div();
        imagenDiv.setWidthFull();
        imagenDiv.setHeight("140px");
        imagenDiv.getStyle().set("background-color", "#f3f3f3");
        imagenDiv.getStyle().set("display", "flex");
        imagenDiv.getStyle().set("align-items", "center");
        imagenDiv.getStyle().set("justify-content", "center");
        imagenDiv.getStyle().set("border-radius", "12px");

        if (p.getImagenUrl() != null && p.getImagenUrl().startsWith("http")) {
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

        // --- AQUÍ ESTABA EL ERROR ---
        // Fíjate que AHORA creamos el botón antes de usarlo
        Button btnAdd = new Button("Añadir", VaadinIcon.PLUS.create());
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAdd.setWidthFull();
        btnAdd.addClickListener(e -> {
            carritoService.anadirProducto(p);
            Notification.show(p.getNombre() + " añadido al carrito")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        // ----------------------------

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