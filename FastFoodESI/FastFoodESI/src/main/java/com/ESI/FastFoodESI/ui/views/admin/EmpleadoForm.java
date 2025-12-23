package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.Empleado;
import com.ESI.FastFoodESI.model.EstadoEmpleado;
import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Turno;
import com.ESI.FastFoodESI.service.admin.EmpleadoService;
import com.ESI.FastFoodESI.service.admin.EstadoEmpleadoService;
import com.ESI.FastFoodESI.service.admin.TurnoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.List;

@SpringComponent
@UIScope
public class EmpleadoForm extends VerticalLayout {

    private final EmpleadoService empleadoService;
    private final Binder<Empleado> binder = new BeanValidationBinder<>(Empleado.class);
    private Empleado currentEmpleado;
    private Dialog dialog;
    private Runnable onSaveListener; 
    private final TextField nombre = new TextField("Nombre");
    private final TextField apellido = new TextField("Apellido");
    private final TextField dni = new TextField("DNI");
    private final EmailField correo = new EmailField("Correo");
    private final TextField telefono = new TextField("Teléfono");
    private final TextField salario = new TextField("Salario");
    private final DatePicker fechaNac = new DatePicker("Fecha Nacimiento");

    private final ComboBox<EstadoEmpleado> estado = new ComboBox<>("Estado");
    private final ComboBox<Turno> turno = new ComboBox<>("Turno");
    private final ComboBox<Negocio> negocio = new ComboBox<>("Asignar a Negocio");

    private final Button saveButton = new Button("Guardar");
    private final Button cancelButton = new Button("Cancelar");

    public EmpleadoForm(EmpleadoService empleadoService,
                        EstadoEmpleadoService estadoService,
                        TurnoService turnoService) {
        this.empleadoService = empleadoService;

        // Configurar combos auxiliares
        estado.setItems(estadoService.findAll());
        estado.setItemLabelGenerator(EstadoEmpleado::getNombre);

        turno.setItems(turnoService.findAll());
        turno.setItemLabelGenerator(Turno::getNombre);
        
        negocio.setItemLabelGenerator(Negocio::getNombre);

        binder.bindInstanceFields(this);
        
        binder.forField(salario)
            .withConverter(new StringToBigDecimalConverter("Introduce un número válido"))
            .bind(Empleado::getSalario, Empleado::setSalario);

        add(createFormLayout(), createButtonLayout());

        saveButton.addClickListener(e -> validateAndSave());
        cancelButton.addClickListener(e -> closeForm());
    }

    public void setNegociosDisponibles(List<Negocio> negocios) {
        this.negocio.setItems(negocios);
    }

    public void setOnSaveListener(Runnable onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        nombre.setWidthFull(); apellido.setWidthFull(); dni.setWidthFull();
        correo.setWidthFull(); telefono.setWidthFull(); salario.setWidthFull();
        negocio.setWidthFull();

        formLayout.add(nombre, apellido, dni, correo, telefono, salario, fechaNac, estado, turno, negocio);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(negocio, 2);
        return formLayout;
    }

    private Component createButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(saveButton, cancelButton);
    }

    public void setEmpleado(Empleado empleado, Dialog parentDialog) {
        this.currentEmpleado = empleado;
        this.dialog = parentDialog;
        binder.readBean(empleado);
    }

    private void closeForm() {
        if (dialog != null) dialog.close();
    }

    private void validateAndSave() {
        try {
            if (negocio.getValue() == null) {
                Notification.show("Debes asignar un negocio al empleado", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            binder.writeBean(currentEmpleado);
            currentEmpleado.setNegocio(negocio.getValue()); 
            
            currentEmpleado.setPropietario(negocio.getValue().getPropietario());

            empleadoService.save(currentEmpleado);

            Notification.show("Empleado guardado", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            closeForm();
            if (onSaveListener != null) onSaveListener.run();

        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}