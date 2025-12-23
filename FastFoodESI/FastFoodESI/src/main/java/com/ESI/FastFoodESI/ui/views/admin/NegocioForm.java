package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.service.admin.NegocioService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class NegocioForm extends VerticalLayout {

    private final NegocioService negocioService;

    private final Binder<Negocio> binder = new BeanValidationBinder<>(Negocio.class);
    private Negocio currentNegocio;
    private Dialog dialog;
    
    private Runnable onSaveListener; 

    private final TextField nombre = new TextField("Nombre");
    private final TextField direccion = new TextField("Dirección");
    private final TextField telefono = new TextField("Teléfono");
    private final EmailField correo = new EmailField("Correo");
    private final TextArea descripcion = new TextArea("Descripción");

    private final Button saveButton = new Button("Guardar");
    private final Button cancelButton = new Button("Cancelar");

    public NegocioForm(NegocioService negocioService) {
        this.negocioService = negocioService;

        binder.bindInstanceFields(this);

        add(createFormLayout(), createButtonLayout());

        saveButton.addClickListener(e -> validateAndSave());
        cancelButton.addClickListener(e -> closeForm());
    }
    
    public void setOnSaveListener(Runnable onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        nombre.setWidthFull();
        direccion.setWidthFull();
        descripcion.setWidthFull();
        
        formLayout.add(nombre, direccion, telefono, correo, descripcion);
        formLayout.setColspan(descripcion, 2);
        return formLayout;
    }

    private Component createButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(saveButton, cancelButton);
    }

    public void setNegocio(Negocio negocio, Dialog parentDialog) {
        this.currentNegocio = negocio;
        this.dialog = parentDialog;
        binder.readBean(negocio);
    }

    private void closeForm() {
        if (dialog != null) {
            dialog.close();
        }
    }

    private void validateAndSave() {
        try {
            binder.writeBean(currentNegocio);
            negocioService.save(currentNegocio);

            Notification.show("Negocio guardado exitosamente", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            closeForm();
            
            if (onSaveListener != null) {
                onSaveListener.run();
            }

        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}