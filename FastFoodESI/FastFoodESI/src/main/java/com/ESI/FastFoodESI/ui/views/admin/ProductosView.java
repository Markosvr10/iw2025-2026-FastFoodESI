package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.repository.CartaRepository;
import com.ESI.FastFoodESI.service.admin.NegocioService;
import com.ESI.FastFoodESI.service.admin.ProductoService;
import com.ESI.FastFoodESI.ui.layouts.admin.PropietarioMainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Lazy;

import java.util.UUID;

@Route(value = "admin/productos", layout = PropietarioMainLayout.class)
@PageTitle("Gestión de Carta | Admin")
@RolesAllowed("PROPIETARIO")
public class ProductosView extends VerticalLayout implements HasUrlParameter<UUID> {

    private final ProductoService service;
    private final NegocioService negocioService;
    // Ya no necesitamos CartaRepository aquí directamente porque lo usa el servicio
    private final ProductoForm form;
    private final Grid<Producto> grid = new Grid<>(Producto.class);
    private final Dialog dialog = new Dialog();
    private Negocio negocioActual;

    public ProductosView(ProductoService service, NegocioService negocioService, @Lazy ProductoForm form) {
        this.service = service;
        this.negocioService = negocioService;
        this.form = form;

        setSizeFull();
        configureGrid();
        configureDialog();
        
        this.form.setOnSaveListener(this::updateList);

        add(new H2("Gestión de Productos"), getToolbar(), grid);
    }

    @Override
    public void setParameter(BeforeEvent event, UUID negocioId) {
        this.negocioActual = negocioService.findById(negocioId);
        if (negocioActual != null) {
            updateList();
        } else {
            Notification.show("Negocio no encontrado").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns("nombre", "descripcion", "stock");
        grid.addColumn(p -> p.getImporte() + " €").setHeader("Precio");
        grid.addComponentColumn(this::createActions).setHeader("Acciones");
    }
    
    private Component createActions(Producto producto) {
        Button edit = new Button(VaadinIcon.PENCIL.create(), e -> editProducto(producto));
        Button delete = new Button(VaadinIcon.TRASH.create(), e -> deleteProducto(producto));
        delete.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
        return new HorizontalLayout(edit, delete);
    }

    private Component getToolbar() {
        Button addBtn = new Button("Añadir Producto", VaadinIcon.PLUS.create());
        addBtn.addClickListener(e -> addProducto());
        addBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);
        return new HorizontalLayout(addBtn);
    }

    private void configureDialog() {
        dialog.add(form);
        dialog.setWidth("600px");
    }

    public void updateList() {
        if (negocioActual != null) {
            grid.setItems(service.findAllByNegocio(negocioActual));
        }
    }

    private void addProducto() {
        if (negocioActual == null) {
            Notification.show("Error: No hay negocio seleccionado").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        grid.asSingleSelect().clear();
        Producto nuevo = new Producto();
        nuevo.setNegocio(this.negocioActual);
        
        form.setProducto(nuevo, dialog);
        dialog.open();
    }

    private void editProducto(Producto producto) {
        form.setProducto(producto, dialog);
        dialog.open();
    }

    // --- ARREGLO: USAR EL MÉTODO SEGURO DEL SERVICIO ---
    private void deleteProducto(Producto producto) {
        try {
            // Llamamos al método transaccional que creamos en el Paso 1
            service.deleteProductoSeguro(producto.getId());
            
            updateList();
            Notification.show("Eliminado correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        } catch (Exception e) {
             e.printStackTrace();
             // Si falla, suele ser porque hay pedidos vinculados
             Notification.show("No se puede eliminar: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}