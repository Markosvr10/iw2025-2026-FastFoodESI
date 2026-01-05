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

@SpringComponent
@UIScope
public class ProductoForm extends VerticalLayout {

    private final ProductoService productoService;
    private final TipoRepository tipoRepository; 

    private final Binder<Producto> binder = new BeanValidationBinder<>(Producto.class);
    private Producto currentProducto;
    private Dialog parentDialog;

    private Runnable onSaveListener; 

    TextField nombre = new TextField("Nombre");

    TextField imagenUrl = new TextField("URL de la Imagen"); 
    
    TextArea descripcion = new TextArea("Descripción");
    BigDecimalField importe = new BigDecimalField("Precio (€)");
    IntegerField stock = new IntegerField("Stock");
    ComboBox<Tipo> tipo = new ComboBox<>("Tipo de Producto");

    Button save = new Button("Guardar");
    Button cancel = new Button("Cancelar");
    Button close = new Button("Cerrar");

    public ProductoForm(ProductoService productoService, TipoRepository tipoRepository) {
        this.productoService = productoService;
        this.tipoRepository = tipoRepository;
        
        addClassName("producto-form");

        tipo.setItems(tipoRepository.findAll());
        tipo.setItemLabelGenerator(Tipo::getNombre);
        
        imagenUrl.setPlaceholder("https://ejemplo.com/foto.jpg");
        imagenUrl.setClearButtonVisible(true);

        binder.bindInstanceFields(this);

        add(createFormLayout(), createButtonsLayout());
    }

    public void setOnSaveListener(Runnable onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public void setProducto(Producto producto, Dialog dialog) {
        this.currentProducto = producto;
        this.parentDialog = dialog;
        
        tipo.setItems(tipoRepository.findAll());
        
        binder.readBean(producto);
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        
        nombre.setWidthFull();
        imagenUrl.setWidthFull();
        descripcion.setWidthFull();
        
        formLayout.add(
            nombre, 
            tipo, 
            importe, 
            stock, 
            imagenUrl,
            descripcion
        );


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
        try {
            binder.writeBean(currentProducto);
            
            productoService.save(currentProducto);
            
            Notification.show("Producto guardado correctamente")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            if (onSaveListener != null) {
                onSaveListener.run();
            }
            
            closeForm(); 

        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void closeForm() {
        if (parentDialog != null) {
            parentDialog.close();
        } else {
            this.setVisible(false);
        }
    }
}