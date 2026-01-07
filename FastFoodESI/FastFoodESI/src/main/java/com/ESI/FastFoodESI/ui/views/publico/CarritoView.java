package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.dto.LineaCarrito;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.security.SecurityService;
import com.ESI.FastFoodESI.service.cliente.CarritoService;
import com.ESI.FastFoodESI.service.pedido.PedidoClienteService;
import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Route(value = "carrito", layout = MainLayout.class)
@PageTitle("Mi Pedido | FastFood ESI")
@AnonymousAllowed
public class CarritoView extends VerticalLayout {

    private final CarritoService carritoService;
    private final PedidoClienteService pedidoService;
    private final ClienteRepository clienteRepository;
    private final SecurityService securityService;

    private final Grid<LineaCarrito> grid;
    private final H3 totalLabel;

    public CarritoView(CarritoService carritoService, PedidoClienteService pedidoService,
            ClienteRepository clienteRepository, SecurityService securityService) {
        this.carritoService = carritoService;
        this.pedidoService = pedidoService;
        this.clienteRepository = clienteRepository;
        this.securityService = securityService;

        setSizeFull();
        setPadding(true);

        H2 titulo = new H2("🛒 Tu Pedido");

        // lista con lo q ha pedidio
        // ------------------------------------------------------------------------------------
        grid = new Grid<>(LineaCarrito.class, false);

        // col nombre prod y precio induvidual
        grid.addColumn(linea -> linea.getProducto().getNombre()).setHeader("Producto").setAutoWidth(true);
        grid.addColumn(linea -> String.format("%.2f €", linea.getProducto().getImporte())).setHeader("Precio Unit.");

        // col cantidad -> ineractiva ã> ajustar la cantidad
        grid.addComponentColumn(linea -> {
            Button btnMenos = new Button(VaadinIcon.MINUS.create());
            btnMenos.addThemeVariants(ButtonVariant.LUMO_SMALL);
            btnMenos.addClickListener(e -> {
                carritoService.restarProducto(linea.getProducto());
                actualizarVista();
            });

            Span cantidad = new Span(String.valueOf(linea.getCantidad()));
            cantidad.getStyle().set("font-weight", "bold");
            cantidad.getStyle().set("margin", "0 10px");

            Button btnMas = new Button(VaadinIcon.PLUS.create());
            btnMas.addThemeVariants(ButtonVariant.LUMO_SMALL);
            btnMas.addClickListener(e -> {
                carritoService.anadirProducto(linea.getProducto());
                actualizarVista();
            });

            return new HorizontalLayout(btnMenos, cantidad, btnMas);
        }).setHeader("Cantidad");

        // precio total producto
        grid.addColumn(linea -> String.format("%.2f €", linea.getTotalLinea()))
                .setHeader("Subtotal");

        // papelera
        grid.addComponentColumn(linea -> {
            Button btnTrash = new Button(VaadinIcon.TRASH.create());
            btnTrash.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            btnTrash.addClickListener(e -> {
                carritoService.eliminarProductoDelTodo(linea.getProducto());
                actualizarVista();
                Notification.show("Producto eliminado");
            });
            return btnTrash;
        });
        // --------------------------------------------------------------------------------------------------------------

        // TOTAL Y BOTONES
        totalLabel = new H3();
        actualizarVista();

        Button btnSeguir = new Button("Seguir Pidiendo");
        btnSeguir.addClickListener(e -> {
            // Recuperamos la memoria de sesión
            String ultimoNegocio = (String) VaadinSession.getCurrent().getAttribute("ULTIMO_NEGOCIO");

            if (ultimoNegocio != null && !ultimoNegocio.isEmpty()) {
                // Volvemos a la pizzería/hamburguesería donde estábamos
                UI.getCurrent().navigate("carta/" + ultimoNegocio);
            } else {
                // Si no hay memoria, vamos a la general
                UI.getCurrent().navigate("carta");
            }
        });
        btnSeguir.getStyle().set("margin-left", "auto");

        Button btnConfirmar = new Button("Confirmar Pedido", VaadinIcon.CHECK.create());
        btnConfirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // LÓGICA BOTÓN CONFIRMAR
        btnConfirmar.addClickListener(e -> {
            if (carritoService.getLineas().isEmpty()) {
                Notification.show("Carrito vacío");
                return;
            }

            // comprobar q este logueado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                Notification.show("Debes iniciar sesión para pedir").addThemeVariants(NotificationVariant.LUMO_ERROR);
                UI.getCurrent().navigate("login");
                return;
            }
            Cliente clienteReal = null;
            UserDetails userDetails = securityService.getAuthenticatedUser();
            if (userDetails != null) {
                Optional<Cliente> c = clienteRepository.findByCorreo(userDetails.getUsername());
                if (c.isPresent()) {
                    clienteReal = c.get();
                }
            }
            if (clienteReal == null) {
                Notification.show("Error: No se encontró tu usuario").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // VENTANITA QUE SALE AL DARLE A CONFIRMAR
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("Finalizar y Pagar");
            dialog.setWidth("500px");

            VerticalLayout dialogLayout = new VerticalLayout();
            dialogLayout.setSpacing(true);

            // ENTREGA
            Select<String> selectTipo = new Select<>();
            selectTipo.setLabel("Tipo de Entrega");
            selectTipo.setItems("Para llevar (Recoger)", "Comer aquí (Mesa)", "A Domicilio");
            selectTipo.setValue("Para llevar (Recoger)");
            selectTipo.setWidthFull();

            // CAMPO DIRECCIÓN
            TextField txtDireccion = new TextField("Dirección de Entrega");
            txtDireccion.setWidthFull();
            txtDireccion.setPlaceholder("Calle, Número, Piso...");
            txtDireccion.setVisible(false); // Oculto por defecto

            // Si el cliente ya tiene una dirección guardada, se la ponemos
            if (clienteReal.getDireccion() != null && !clienteReal.getDireccion().isEmpty()) {
                txtDireccion.setValue(clienteReal.getDireccion());
            }

            // Lógica: Si selecciona "A Domicilio", mostramos el campo. Si no, lo ocultamos.
            selectTipo.addValueChangeListener(ev -> {
                if ("A Domicilio".equals(ev.getValue())) {
                    txtDireccion.setVisible(true);
                } else {
                    txtDireccion.setVisible(false);
                }
            });

            // MÉTODO DE PAGO
            Select<String> selectPago = new Select<>();
            selectPago.setLabel("Método de Pago");
            selectPago.setItems("Tarjeta Bancaria", "PayPal", "Efectivo");
            selectPago.setValue("Tarjeta Bancaria");
            selectPago.setWidthFull();

            // TARJETA**************************************************************************************************
            VerticalLayout layoutTarjeta = new VerticalLayout();
            layoutTarjeta.setPadding(false);

            TextField txtNumTarjeta = new TextField("Número de Tarjeta");
            txtNumTarjeta.setPlaceholder("1234567812345678");
            txtNumTarjeta.setMaxLength(16);
            txtNumTarjeta.setWidthFull();
            txtNumTarjeta.setHelperText("Introduce 16 dígitos sin espacios");

            HorizontalLayout rowTarjeta = new HorizontalLayout();
            rowTarjeta.setWidthFull();

            TextField txtFecha = new TextField("Caducidad");
            txtFecha.setPlaceholder("MM/YY");
            txtFecha.setMaxLength(5);

            TextField txtCvv = new TextField("CVV");
            txtCvv.setPlaceholder("123");
            txtCvv.setMaxLength(3);

            rowTarjeta.add(txtFecha, txtCvv);
            layoutTarjeta.add(txtNumTarjeta, rowTarjeta);

            // PAYPAL***************************************************************************************************
            VerticalLayout layoutPaypal = new VerticalLayout();
            layoutPaypal.setPadding(false);
            layoutPaypal.setVisible(false);

            EmailField emailPaypal = new EmailField("Correo PayPal");
            emailPaypal.setPlaceholder("usuario@ejemplo.com");
            emailPaypal.setWidthFull();
            emailPaypal.setErrorMessage("Introduce un email válido");

            PasswordField passPaypal = new PasswordField("Contraseña");
            passPaypal.setWidthFull();
            layoutPaypal.add(emailPaypal, passPaypal);

            // EFECTIVO*************************************************************************************************
            VerticalLayout layoutEfectivo = new VerticalLayout();
            layoutEfectivo.setPadding(false);
            layoutEfectivo.setVisible(false);

            Span infoEfectivo = new Span("ℹ️ Pagarás el total en el momento de la entrega/recogida.");
            infoEfectivo.getStyle().set("color", "gray");
            infoEfectivo.getStyle().set("font-size", "0.9em");
            layoutEfectivo.add(infoEfectivo);

            // LÓGICA DE CAMBIO DE PESTAÑA
            selectPago.addValueChangeListener(ev -> {
                String val = ev.getValue();
                layoutTarjeta.setVisible("Tarjeta Bancaria".equals(val));
                layoutPaypal.setVisible("PayPal".equals(val));
                layoutEfectivo.setVisible("Efectivo".equals(val));
            });

            dialogLayout.add(selectTipo, txtDireccion, selectPago, layoutTarjeta, layoutPaypal, layoutEfectivo);
            dialog.add(dialogLayout);

            // boton cancelar
            Button cancelar = new Button("Cancelar", event -> dialog.close());
            Cliente finalClienteReal = clienteReal;

            // boton confirmar y pagar
            Button btnPagar = new Button("Confirmar y Pagar " + String.format("%.2f €", carritoService.calcularTotal()),
                    event -> {

                        String metodo = selectPago.getValue();
                        String tipo = selectTipo.getValue();
                        boolean validacionCorrecta = true;

                        // --- VALIDACIONES
                        // ------------------------------------------------------------------------------------
                        // --- VALIDAR DIRECCIÓN ---
                        String direccionFinal = ""; // Por defecto vacía
                        if ("A Domicilio".equals(tipo)) {
                            if (txtDireccion.isEmpty()) {
                                Notification.show("Debes escribir una dirección de entrega")
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                return;
                            }
                            direccionFinal = txtDireccion.getValue();
                        }

                        if ("Tarjeta Bancaria".equals(metodo)) {
                            // Validar Tarjeta (16 dígitos y que no esté vacía)
                            if (txtNumTarjeta.isEmpty() || !txtNumTarjeta.getValue().matches("\\d{16}")) {
                                Notification.show("Error: El número de tarjeta debe tener 16 dígitos numéricos")
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                validacionCorrecta = false;
                            }
                            // Validar Fecha (Formato simple MM/YY)
                            else if (txtFecha.isEmpty() || !txtFecha.getValue().matches("\\d{2}/\\d{2}")) {
                                Notification.show("Error: La fecha debe ser MM/YY")
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                validacionCorrecta = false;
                            }
                            // Validar CVV (3 dígitos)
                            else if (txtCvv.isEmpty() || !txtCvv.getValue().matches("\\d{3}")) {
                                Notification.show("Error: El CVV debe tener 3 dígitos")
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                validacionCorrecta = false;
                            }

                        } else if ("PayPal".equals(metodo)) {
                            // Validar Email y Password
                            if (emailPaypal.isEmpty() || emailPaypal.isInvalid()) {
                                Notification.show("Error: Email de PayPal inválido")
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                validacionCorrecta = false;
                            } else if (passPaypal.isEmpty()) {
                                Notification.show("Error: Falta la contraseña de PayPal")
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                validacionCorrecta = false;
                            }
                        }

                        if (!validacionCorrecta)
                            return;

                        try {
                            pedidoService.confirmarPedido(
                                    carritoService,
                                    finalClienteReal,
                                    selectTipo.getValue(),
                                    metodo,
                                    direccionFinal);

                            Notification.show("¡Pedido confirmado! (" + metodo + ") 🍔")
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                            carritoService.vaciarCarrito();
                            actualizarVista();
                            dialog.close();

                            // REDIRECCIÓN A "MIS PEDIDOS" (DEL MAIN)
                            UI.getCurrent().navigate("mis-pedidos");

                        } catch (Exception ex) {
                            Notification.show("Error: " + ex.getMessage())
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    });
            btnPagar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            dialog.getFooter().add(cancelar, btnPagar);
            dialog.open();
        });

        HorizontalLayout footer = new HorizontalLayout(totalLabel, btnSeguir, btnConfirmar);
        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);

        add(titulo, grid, footer);
    }

    private void actualizarVista() {
        grid.setItems(carritoService.getLineas());
        grid.getDataProvider().refreshAll();
        totalLabel.setText(String.format("Total: %.2f €", carritoService.calcularTotal()));
    }
}