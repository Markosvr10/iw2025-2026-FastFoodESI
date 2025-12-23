package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Alergeno;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.model.Tipo;
import com.ESI.FastFoodESI.repository.AlergenoRepository;
import com.ESI.FastFoodESI.repository.TipoRepository;
import com.ESI.FastFoodESI.service.admin.ProductoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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

    private final ProductoService service;
    private final ProductosView parentView;

    private final Binder<Producto> binder = new BeanValidationBinder<>(Producto.class);
    private Producto currentProducto;
    private Dialog dialog;

    // Campos
    private final TextField nombre = new TextField("Nombre del Producto");
    private final TextArea descripcion = new TextArea("Descripción");
    private final BigDecimalField importe = new BigDecimalField("Precio (€)");
    private final IntegerField stock = new IntegerField("Stock Disponible");
    
    // Selectores
    private final ComboBox<Tipo> tipo = new ComboBox<>("Categoría");
    private final MultiSelectComboBox<Alergeno> alergenos = new MultiSelectComboBox<>("Alérgenos");

    // Botones
    private final Button saveButton = new Button("Guardar");
    private final Button cancelButton = new Button("Cancelar");

    public ProductoForm(ProductoService service, 
                        ProductosView parentView,
                        TipoRepository tipoRepo, 
                        AlergenoRepository alergenoRepo) {
        this.service = service;
        this.parentView = parentView;

        // Configurar selectores
        tipo.setItems(tipoRepo.findAll());
        tipo.setItemLabelGenerator(Tipo::getNombre);
        
        alergenos.setItems(alergenoRepo.findAll());
        alergenos.setItemLabelGenerator(Alergeno::getNombre);

        // Configurar campos numéricos
        importe.addThemeVariants(com.vaadin.flow.component.textfield.TextFieldVariant.LUMO_ALIGN_RIGHT);
        importe.setPrefixComponent(new com.vaadin.flow.component.html.Span("€"));
        
        // Binder
        binder.bindInstanceFields(this);

        // Layout
        add(createFormLayout(), createButtonsLayout());
        
        saveButton.addClickListener(e -> validateAndSave());
        cancelButton.addClickListener(e -> closeForm());
    }

    private Component createFormLayout() {
        FormLayout form = new FormLayout();
        nombre.setWidthFull();
        descripcion.setWidthFull();
        alergenos.setWidthFull();
        
        form.add(nombre, tipo, importe, stock, descripcion, alergenos);
        // Hacemos que la descripción y alérgenos ocupen 2 columnas si hay espacio
        form.setColspan(descripcion, 2);
        form.setColspan(alergenos, 2);
        return form;
    }

    private Component createButtonsLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(saveButton, cancelButton);
    }

    public void setProducto(Producto producto, Dialog parentDialog) {
        this.currentProducto = producto;
        this.dialog = parentDialog;
        binder.readBean(producto);
    }

    private void closeForm() {
        if (dialog != null) dialog.close();
    }

    private void validateAndSave() {
        try {
            binder.writeBean(currentProducto);
            service.save(currentProducto);
            
            Notification.show("Producto guardado", 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            parentView.updateList();
            closeForm();
        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}