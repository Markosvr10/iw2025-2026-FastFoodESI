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
import com.vaadin.flow.data.validator.EmailValidator; // Importante importar esto
import com.vaadin.flow.data.validator.StringLengthValidator;
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

    // Componentes visuales
    private final TextField nombre = new TextField("Nombre");
    private final TextField direccion = new TextField("Dirección");
    private final TextField telefono = new TextField("Teléfono");
    private final EmailField correo = new EmailField("Correo");
    private final TextArea descripcion = new TextArea("Descripción");

    private final Button saveButton = new Button("Guardar");
    private final Button cancelButton = new Button("Cancelar");

    public NegocioForm(NegocioService negocioService) {
        this.negocioService = negocioService;

        // Configuramos el binder con validaciones y protección contra nulos
        configureBinder();

        add(createFormLayout(), createButtonLayout());

        saveButton.addClickListener(e -> validateAndSave());
        cancelButton.addClickListener(e -> closeForm());
    }

    private void configureBinder() {
        // 1. NOMBRE: Obligatorio
        binder.forField(nombre)
              .withNullRepresentation("") // Evita el crash si viene null de BD
              .asRequired("El nombre del negocio es obligatorio") // Muestra error si se deja vacío
              .withValidator(name -> name.length() >= 2, "Debe tener al menos 2 letras")
              .bind(Negocio::getNombre, Negocio::setNombre);

        // 2. DIRECCIÓN: Obligatoria
        binder.forField(direccion)
              .withNullRepresentation("")
              .asRequired("La dirección es obligatoria")
              .withValidator(new StringLengthValidator(
              "La dirección debe tener entre 5 y 255 caracteres", 5, 255))
              .bind(Negocio::getDireccion, Negocio::setDireccion);

        // 3. TELÉFONO: Opcional o Obligatorio 
        binder.forField(telefono)
              .withNullRepresentation("")
              .asRequired("El teléfono es obligatorio")
              .withValidator(t -> t == null || t.isEmpty() || t.matches("^[0-9]{9}$"), "El teléfono debe tener 9 dígitos")
              .bind(Negocio::getTelefono, Negocio::setTelefono);

        // 4. CORREO: Validación de formato + Obligatorio
        binder.forField(correo)
              .withNullRepresentation("")
              .asRequired("El correo es obligatorio")
              .withValidator(new EmailValidator("El formato del correo no es válido")) // Valida que tenga @ y .
              .bind(Negocio::getCorreo, Negocio::setCorreo);

        // 5. DESCRIPCIÓN: Opcional (solo protegemos el null)
        binder.forField(descripcion)
              .withNullRepresentation("")
              .bind(Negocio::getDescripcion, Negocio::setDescripcion);
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
            // Intenta escribir los datos del formulario en el objeto
            binder.writeBean(currentNegocio);
            
            // Intenta guardar en base de datos
            negocioService.save(currentNegocio);

            Notification.show("Negocio guardado exitosamente", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            closeForm();
            
            if (onSaveListener != null) {
                onSaveListener.run();
            }

        } catch (com.vaadin.flow.data.binder.ValidationException e) {
            // Error de validación del formulario (campos vacíos o incorrectos)
            Notification.show("Revisa los campos del formulario", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            
        } catch (Exception e) {
            //Imprimir el error real en la consola
            e.printStackTrace(); 
            
            Notification.show("Error al guardar ", 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}