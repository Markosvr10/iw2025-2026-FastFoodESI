package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo;
import com.ESI.FastFoodESI.repository.TipoRepository; 
import com.ESI.FastFoodESI.service.admin.ProductoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

@SpringComponent
@UIScope
public class ProductoForm extends VerticalLayout {

    private final ProductoService productoService;
    private final TipoRepository tipoRepository;

    private final Binder<Producto> binder = new BeanValidationBinder<>(Producto.class);
    private Producto currentProducto;
    private Dialog parentDialog;
    private Runnable onSaveListener;

    // UI
    private final TextField nombre = new TextField("Nombre");
    private final BigDecimalField precio = new BigDecimalField("Precio"); // Mapea a 'importe'
    private final IntegerField stock = new IntegerField("Stock");
    private final TextField imagenUrl = new TextField("URL Imagen");
    private final TextArea descripcion = new TextArea("Descripción");
    private final ComboBox<Tipo> tipo = new ComboBox<>("Categoría");

    private final Button save = new Button("Guardar");
    private final Button cancel = new Button("Cancelar");

    public ProductoForm(ProductoService productoService, TipoRepository tipoRepository) {
        this.productoService = productoService;
        this.tipoRepository = tipoRepository;
        
        tipo.setItems(tipoRepository.findAll());
        tipo.setItemLabelGenerator(Tipo::getNombre);
        precio.setPrefixComponent(new com.vaadin.flow.component.html.Span("€"));
        
        configureBinder();
        add(createFormLayout(), createButtonsLayout());
    }

    private void configureBinder() {
        binder.forField(nombre)
              .asRequired("Nombre obligatorio")
              .withNullRepresentation("") 
              .bind(Producto::getNombre, Producto::setNombre);

        binder.forField(precio)
              .asRequired("Precio obligatorio")
              .withValidator(p -> p != null && p.compareTo(BigDecimal.ZERO) >= 0, "No puede ser negativo")
              .bind(Producto::getImporte, Producto::setImporte); 

        binder.forField(stock)
              .asRequired("Stock obligatorio")
              .withValidator(s -> s != null && s >= 0, "No puede ser negativo")
              .bind(Producto::getStock, Producto::setStock);

        binder.forField(tipo)
              .asRequired("Categoría obligatoria")
              .bind(Producto::getTipo, Producto::setTipo);

        binder.forField(imagenUrl).withNullRepresentation("").bind(Producto::getImagenUrl, Producto::setImagenUrl);
        binder.forField(descripcion).withNullRepresentation("").bind(Producto::getDescripcion, Producto::setDescripcion);
    }

    public void setProducto(Producto producto, Dialog dialog) {
        this.currentProducto = producto;
        this.parentDialog = dialog;
        binder.readBean(producto);
    }
    
    public void setOnSaveListener(Runnable listener) {
        this.onSaveListener = listener;
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        nombre.setWidthFull();
        imagenUrl.setWidthFull();
        descripcion.setWidthFull();
        descripcion.setHeight("100px");

        formLayout.add(nombre, tipo, precio, stock, imagenUrl, descripcion);
        formLayout.setColspan(nombre, 2);
        formLayout.setColspan(imagenUrl, 2);
        formLayout.setColspan(descripcion, 2);
        return formLayout;
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        cancel.addClickListener(event -> closeForm());

        return new HorizontalLayout(save, cancel);
    }

    private void validateAndSave() {
        if (binder.validate().hasErrors()) {
            Notification.show("Revisa los errores en rojo", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            binder.writeBean(currentProducto);
            
            // Verificación de seguridad
            if (currentProducto.getNegocio() == null) {
                Notification.show("Error Interno: Producto sin negocio asignado.", 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (currentProducto.getId() != null) {
                // MODO EDICIÓN (Safe Save)
                Producto pFresco = productoService.findById(currentProducto.getId());
                if (pFresco == null) {
                     Notification.show("El producto ya no existe", 3000, Notification.Position.MIDDLE);
                     return;
                }
                // Actualizamos campos
                pFresco.setNombre(currentProducto.getNombre());
                pFresco.setImporte(currentProducto.getImporte());
                pFresco.setStock(currentProducto.getStock());
                pFresco.setTipo(currentProducto.getTipo());
                pFresco.setImagenUrl(currentProducto.getImagenUrl());
                pFresco.setDescripcion(currentProducto.getDescripcion());
                
                productoService.save(pFresco);
            } else {
                // MODO CREACIÓN
                productoService.save(currentProducto);
            }
            
            Notification.show("Guardado correctamente", 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            if (onSaveListener != null) onSaveListener.run();
            closeForm();

        } catch (DataIntegrityViolationException e) {
            Notification.show("Error BD: Posible nombre duplicado.", 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void closeForm() {
        if (parentDialog != null) {
            parentDialog.close();
        }
    }
}