package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo;
import com.ESI.FastFoodESI.service.cliente.MenuService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
@PageTitle("Carta | FastFood ESI")
@AnonymousAllowed
public class CartaView extends VerticalLayout {

    private final MenuService menuService;
    private List<Producto> todosLosProductos; // almacenamso todos aqui para no estar yendo todo le rato a la BD

    private final FlexLayout contenedorTarjetas;
    private final TextField buscador;
    private final Tabs tabsCategorias;

    public CartaView(MenuService menuService) {
        this.menuService = menuService;
        this.todosLosProductos = menuService.obtenerTodosLosProductos();
        List<Tipo> categorias = menuService.obtenerTodosLosTipos();

        //Conf layout
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setMaxWidth("1200px");
        getStyle().set("margin", "0 auto");

        // --- CABECERA ---
        H2 titulo = new H2("🍔 FastFoodESI");
        Button btnCarrito = new Button("Ver Carrito", VaadinIcon.CART.create());
        btnCarrito.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCarrito.addClickListener(e -> UI.getCurrent().navigate("carrito"));

        Button btnLogin = new Button(VaadinIcon.USER.create());
        btnLogin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnLogin.addClickListener(e -> UI.getCurrent().navigate("login"));

        HorizontalLayout header = new HorizontalLayout(titulo, btnLogin, btnCarrito);
        header.setWidthFull();
        header.expand(titulo);
        header.setAlignItems(Alignment.CENTER);

        // --- BUSCADOR ---
        buscador = new TextField();
        buscador.setPlaceholder("Buscar...");
        buscador.setPrefixComponent(VaadinIcon.SEARCH.create());
        buscador.setClearButtonVisible(true);
        buscador.setWidthFull();
        buscador.setValueChangeMode(ValueChangeMode.LAZY);
        buscador.addValueChangeListener(e -> filtrarProductos());

        // --- PESTAÑAS DE CATEGORÍAS ---
        tabsCategorias = new Tabs();
        tabsCategorias.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabsCategorias.setWidthFull();

        // Pestaña "Todos" (Fija)
        Tab tabTodos = new Tab("Todos");
        tabsCategorias.add(tabTodos);

        // Pestañas Dinámicas (Desde la BBDD: Hamburguesas, Bebidas...)
        for (Tipo tipo : categorias) {
            Tab tab = new Tab(tipo.getNombre());
            // Guardamos el ID o el Nombre en el componente para saber cual es cual
            tab.setId(tipo.getNombre());
            tabsCategorias.add(tab);
        }

        // Listener: Cuando cambias de pestaña, filtramos
        tabsCategorias.addSelectedChangeListener(e -> filtrarProductos());

        // --- CONTENEDOR DE PRODUCTOS ---
        contenedorTarjetas = new FlexLayout();
        contenedorTarjetas.setWidthFull();
        contenedorTarjetas.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        contenedorTarjetas.getStyle().set("gap", "20px");
        contenedorTarjetas.setJustifyContentMode(JustifyContentMode.CENTER);

        // Carga inicial
        filtrarProductos();

        add(header, buscador, tabsCategorias, contenedorTarjetas);
    }

    // --- LÓGICA DE FILTRADO (Buscador + Pestañas) ---
    private void filtrarProductos() {
        contenedorTarjetas.removeAll();

        String textoBusqueda = buscador.getValue().toLowerCase();
        Tab tabSeleccionado = tabsCategorias.getSelectedTab();
        String categoriaSeleccionada = tabSeleccionado.getLabel();

        List<Producto> productosFiltrados = todosLosProductos.stream()
                .filter(p -> {
                    // 1. Filtro de Texto
                    boolean coincideTexto = p.getNombre().toLowerCase().contains(textoBusqueda);

                    // 2. Filtro de Categoría
                    boolean coincideCategoria = true;
                    if (!"Todos".equals(categoriaSeleccionada)) {
                        // Si no estamos en "Todos", miramos si el Tipo del producto coincide con la pestaña
                        coincideCategoria = p.getTipo().getNombre().equalsIgnoreCase(categoriaSeleccionada);
                    }

                    return coincideTexto && coincideCategoria;
                })
                .collect(Collectors.toList());

        // Pintar resultados
        if (productosFiltrados.isEmpty()) {
            contenedorTarjetas.add(new H3("Vaya, no hemos encontrado nada... 😢"));
        } else {
            for (Producto p : productosFiltrados) {
                contenedorTarjetas.add(crearTarjetaProducto(p));
            }
        }
    }

    // --- DISEÑO DE TARJETA ---
    private Component crearTarjetaProducto(Producto p) {
        Div imagenDiv = new Div();
        imagenDiv.setWidthFull();
        imagenDiv.setHeight("140px");
        imagenDiv.getStyle().set("background-color", "#f3f3f3");
        imagenDiv.getStyle().set("display", "flex");
        imagenDiv.getStyle().set("align-items", "center");
        imagenDiv.getStyle().set("justify-content", "center");
        imagenDiv.getStyle().set("border-radius", "12px");

        // Intentamos cargar imagen si es una URL válida, si no, icono
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

        Button btnAdd = new Button("Añadir", VaadinIcon.PLUS.create());
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAdd.setWidthFull();
        btnAdd.addClickListener(e -> {
            Notification.show("Añadido: " + p.getNombre())
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