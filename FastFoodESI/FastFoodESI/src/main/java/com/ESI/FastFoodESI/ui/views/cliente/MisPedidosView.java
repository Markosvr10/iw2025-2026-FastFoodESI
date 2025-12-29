package com.ESI.FastFoodESI.ui.views.cliente;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.model.LineaPedido;
import com.ESI.FastFoodESI.model.Pedido;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.repository.PedidoRepository;
import com.ESI.FastFoodESI.security.SecurityService;
import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Route(value = "mis-pedidos", layout = MainLayout.class)
@PageTitle("Mis Pedidos | FastFood ESI")
@RolesAllowed("CLIENTE")
public class MisPedidosView extends VerticalLayout {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final SecurityService securityService;

    private final VerticalLayout pedidosContainer;

    public MisPedidosView(PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            SecurityService securityService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.securityService = securityService;

        setSizeFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        add(new H2("Tu Historial de Pedidos:"));

        pedidosContainer = new VerticalLayout();
        pedidosContainer.setWidth("100%");
        pedidosContainer.setMaxWidth("800px");
        pedidosContainer.setSpacing(true);

        add(pedidosContainer);

        cargarDatos();
    }

    private void cargarDatos() {
        pedidosContainer.removeAll();

        UserDetails user = securityService.getAuthenticatedUser();
        if (user != null) {
            Optional<Cliente> clienteOpt = clienteRepository.findByCorreo(user.getUsername());

            if (clienteOpt.isPresent()) {
                // Buscamos los pedidos (findByClienteId ordena por fecha descendente)
                List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByFechaHoraDesc(clienteOpt.get().getId());

                if (pedidos.isEmpty()) {
                    pedidosContainer.add(new Span("No has realizado ningún pedido aún."));
                } else {
                    // Por cada pedido -> una tarjeta visual
                    for (Pedido p : pedidos) {
                        pedidosContainer.add(crearTarjetaPedido(p));
                    }
                }
            }
        }
    }

    private Div crearTarjetaPedido(Pedido pedido) {
        Div card = new Div();
        card.setWidthFull();

        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)"); // Sombra suave
        card.getStyle().set("padding", "20px");
        card.getStyle().set("border", "1px solid #e0e0e0");

        // Cabecera Fecha y Estado
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        String fechaFormateada = pedido.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        H4 fecha = new H4("📅 " + fechaFormateada);
        fecha.getStyle().set("margin", "0");

        // ESTADO
        Span estado = new Span(pedido.getEstado() != null ? pedido.getEstado().getNombre() : "Pendiente");
        estado.getElement().getThemeList().add("badge");
        if ("RECIBIDO".equals(estado.getText()))
            estado.getElement().getThemeList().add("success");
        else if ("PENDIENTE".equals(estado.getText()))
            estado.getElement().getThemeList().add("contrast");

        header.add(fecha, estado);

        // Lista de productos
        VerticalLayout detalles = new VerticalLayout();
        detalles.setPadding(false);
        detalles.setSpacing(false);
        detalles.getStyle().set("margin-top", "10px");
        detalles.getStyle().set("color", "#555");

        if (pedido.getLineas() != null) {
            for (LineaPedido linea : pedido.getLineas()) {
                String textoProducto = String.format("• %dx %s",
                        linea.getCantidad(),
                        linea.getProducto().getNombre());
                detalles.add(new Span(textoProducto));
            }
        }

        // Pie Precio Total
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        H4 total = new H4(String.format("Total: %.2f €", pedido.getTotal()));
        total.getStyle().set("color", "var(--lumo-primary-color)");

        footer.add(total);

        card.add(header, detalles, footer); // Añadimos todo a la tarjeta
        return card;
    }
}