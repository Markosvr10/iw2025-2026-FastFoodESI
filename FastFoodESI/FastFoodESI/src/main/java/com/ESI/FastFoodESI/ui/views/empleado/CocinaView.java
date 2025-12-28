package com.ESI.FastFoodESI.ui.views.empleado;

import com.ESI.FastFoodESI.model.EstadoPedido;
import com.ESI.FastFoodESI.model.LineaPedido;
import com.ESI.FastFoodESI.model.Pedido;
import com.ESI.FastFoodESI.repository.EstadoPedidoRepository;
import com.ESI.FastFoodESI.service.admin.PedidoService;
import com.ESI.FastFoodESI.ui.layouts.MainLayout; // Asegúrate de que el package sea correcto (ui.layouts o ui.layout)
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

@Route(value = "cocina", layout = MainLayout.class)
@PageTitle("Cocina | FastFood ESI")
@RolesAllowed({ "COCINA", "PROPIETARIO" })
@SpringComponent
@UIScope
public class CocinaView extends VerticalLayout {

    // Servicios
    private final PedidoService pedidoService;
    private final EstadoPedidoRepository estadoPedidoRepository;

    // Componentes UI
    private final FlexLayout contenedorPedidos; // Usamos FlexLayout en vez de Grid para las "tarjetas"

    @Autowired
    public CocinaView(PedidoService pedidoService, EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoService = pedidoService;
        this.estadoPedidoRepository = estadoPedidoRepository;

        // Configuración base de la vista (Igual que EmpleadosView)
        addClassName("cocina-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Título y Toolbar
        add(new H2("Comandas de Cocina"), getToolbar());

        // Contenedor principal de tarjetas
        contenedorPedidos = new FlexLayout();
        contenedorPedidos.setSizeFull();
        contenedorPedidos.setFlexWrap(FlexLayout.FlexWrap.WRAP); // Para que bajen si no caben
        contenedorPedidos.getStyle().set("gap", "20px");
        contenedorPedidos.getStyle().set("overflow-y", "auto"); // Scroll si hay muchos pedidos

        add(contenedorPedidos);

        // Cargar datos iniciales
        updateList();
    }

    private HorizontalLayout getToolbar() {
        Button refreshButton = new Button("Actualizar", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(refreshButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    // Método equivalente al configureGrid pero para tarjetas
    public void updateList() {
        contenedorPedidos.removeAll();

        // Obtenemos pedidos pendientes (RECIBIDO o EN_PREPARACION)
        List<Pedido> pedidosPendientes = pedidoService.findPedidosCocina();

        if (pedidosPendientes.isEmpty()) {
            contenedorPedidos.add(new H3("¡Todo limpio! No hay pedidos pendientes."));
        } else {
            for (Pedido p : pedidosPendientes) {
                contenedorPedidos.add(crearTarjetaComanda(p));
            }
        }
    }

    // Método auxiliar para diseñar cada "Papelito" de la cocina
    private Component crearTarjetaComanda(Pedido p) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("300px");
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle().set("border", "1px solid #ddd");
        card.getStyle().set("border-radius", "8px");
        card.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        card.getStyle().set("background-color", "white");

        // Cabecera: ID y Hora
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span idSpan = new Span("Pedido #" + p.getId().toString().substring(0, 4)); // ID corto visual
        idSpan.getStyle().set("font-weight", "bold");

        String hora = p.getFechaHora() != null ? p.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "--:--";
        Span horaSpan = new Span(hora);

        header.add(idSpan, horaSpan);

        // Estado actual (Color visual)
        Span badgeEstado = new Span(p.getEstado().getNombre());
        badgeEstado.getElement().getThemeList().add("badge " +
                (p.getEstado().getNombre().equals("RECIBIDO") ? "error" : "contrast")); // Rojo si es nuevo

        // Lista de Productos
        VerticalLayout listaProductos = new VerticalLayout();
        listaProductos.setPadding(false);
        listaProductos.setSpacing(false);

        if (p.getLineas() != null) {
            for (LineaPedido linea : p.getLineas()) {
                Span item = new Span(linea.getCantidad() + "x " + linea.getProducto().getNombre());
                item.getStyle().set("font-size", "1.1em");
                listaProductos.add(item);
            }
        }

        // Botón de Acción
        Button btnAccion = new Button("Listo / Marchar");
        btnAccion.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAccion.setWidthFull();
        btnAccion.addClickListener(e -> avanzarPedido(p));

        // Ensamblar tarjeta
        card.add(header, badgeEstado, new Hr(), listaProductos, new Hr(), btnAccion);
        return card;
    }

    private void avanzarPedido(Pedido p) {
        // Lógica: Si está RECIBIDO -> Pasa a EN_PREPARACION. Si está EN_PREPARACION ->
        // Pasa a LISTO
        String estadoActual = p.getEstado().getNombre();
        String siguienteEstado = "LISTO"; // Por defecto

        if (estadoActual.equals("RECIBIDO")) {
            siguienteEstado = "EN_PREPARACION";
        }

        // Buscamos el objeto estado en base de datos
        EstadoPedido nuevoEstadoObj = estadoPedidoRepository.findByNombre(siguienteEstado).orElse(null);

        if (nuevoEstadoObj != null) {
            pedidoService.modificarEstado(p.getId(), nuevoEstadoObj);
            Notification.show("Pedido actualizado a " + siguienteEstado)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            updateList(); // Refrescar pantalla
        } else {
            Notification.show("Error: Estado " + siguienteEstado + " no existe en BD",
                    3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}