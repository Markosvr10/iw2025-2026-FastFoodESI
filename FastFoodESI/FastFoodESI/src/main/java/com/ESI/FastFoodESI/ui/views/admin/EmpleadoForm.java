package com.ESI.FastFoodESI.ui.views.admin;

import com.ESI.FastFoodESI.model.*;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
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
    private final EstadoEmpleadoService estadoService;
    private final TurnoService turnoService;
    
    private final Binder<Empleado> binder = new BeanValidationBinder<>(Empleado.class);
    private Empleado currentEmpleado;
    private Dialog dialog;
    private Runnable onSaveListener; 

    // Campos de Texto
    private final TextField nombre = new TextField("Nombre");
    private final TextField apellido = new TextField("Apellido");
    private final TextField dni = new TextField("DNI");
    private final EmailField correo = new EmailField("Correo");
    private final TextField telefono = new TextField("Teléfono");
    private final TextField salario = new TextField("Salario");
    private final DatePicker fechaNac = new DatePicker("Fecha Nacimiento");

    // Desplegables
    private final Select<String> puestoSelector = new Select<>();
    private final Select<EstadoEmpleado> estado = new Select<>();
    private final Select<Turno> turno = new Select<>();
    
    // Negocio 
    private final ComboBox<Negocio> negocio = new ComboBox<>("Asignar a Negocio");

    private final Button saveButton = new Button("Guardar");
    private final Button cancelButton = new Button("Cancelar");

    public EmpleadoForm(EmpleadoService empleadoService,
                        EstadoEmpleadoService estadoService,
                        TurnoService turnoService) {
        this.empleadoService = empleadoService;
        this.estadoService = estadoService;
        this.turnoService = turnoService;

        // Configuración visual
        estado.setLabel("Estado");
        estado.setItemLabelGenerator(EstadoEmpleado::getNombre);
        
        turno.setLabel("Turno");
        turno.setItemLabelGenerator(Turno::getNombre);
        
        negocio.setItemLabelGenerator(Negocio::getNombre);

        puestoSelector.setLabel("Puesto / Cargo");
        puestoSelector.setItems("Camarero", "Cocinero", "Repartidor"); 
        puestoSelector.setPlaceholder("Selecciona un cargo...");

        // Binding
        binder.forField(nombre).withNullRepresentation("").bind(Empleado::getNombre, Empleado::setNombre);
        binder.forField(apellido).withNullRepresentation("").bind(Empleado::getApellido, Empleado::setApellido);
        binder.forField(dni).withNullRepresentation("").bind(Empleado::getDni, Empleado::setDni);
        binder.forField(correo).withNullRepresentation("").bind(Empleado::getCorreo, Empleado::setCorreo);
        binder.forField(telefono).withNullRepresentation("").bind(Empleado::getTelefono, Empleado::setTelefono);
        binder.forField(fechaNac).bind(Empleado::getFechaNac, Empleado::setFechaNac);
        
        binder.forField(salario)
            .withNullRepresentation("")
            .withConverter(new StringToBigDecimalConverter("Introduce un número válido"))
            .bind(Empleado::getSalario, Empleado::setSalario);
            
        binder.forField(estado).bind(Empleado::getEstado, Empleado::setEstado);
        binder.forField(turno).bind(Empleado::getTurno, Empleado::setTurno);

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
        negocio.setWidthFull(); puestoSelector.setWidthFull();
        estado.setWidthFull(); turno.setWidthFull();

        formLayout.add(nombre, apellido, dni, correo, telefono, salario, fechaNac, 
                       puestoSelector, estado, turno, negocio);
        
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), 
                                      new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(negocio, 2);
        return formLayout;
    }

    private Component createButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(saveButton, cancelButton);
    }

    // --- CARGA DE DATOS  ---
    public void setEmpleado(Empleado empleado, Dialog parentDialog) {
        this.currentEmpleado = empleado;
        this.dialog = parentDialog;
        
       
        estado.setItems(estadoService.findAll());
        turno.setItems(turnoService.findAll());

        if (empleado.getNegocio() != null) {
            negocio.setValue(empleado.getNegocio());
        } else {
            negocio.clear();
        }

        binder.readBean(empleado); 
        
        if (empleado.getId() != null) {
            puestoSelector.setValue(empleado.getPuesto());
            puestoSelector.setReadOnly(true);
        } else {
            puestoSelector.clear();
            puestoSelector.setReadOnly(false);
        }
    }

    private void closeForm() {
        if (dialog != null) dialog.close();
    }

    private void validateAndSave() {
        try {
            if (negocio.getValue() == null) {
                Notification.show("Debes asignar un negocio", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (currentEmpleado.getId() == null && puestoSelector.getValue() == null) {
                 Notification.show("Debes seleccionar un Puesto", 3000, Notification.Position.MIDDLE)
                     .addThemeVariants(NotificationVariant.LUMO_ERROR);
                 return;
            }

            Empleado empleadoAGuardar;
            if (currentEmpleado.getId() != null) {
                empleadoAGuardar = currentEmpleado;
            } else {
                String tipo = puestoSelector.getValue();
                switch (tipo) {
                    case "Cocinero": empleadoAGuardar = new Cocina(); break;
                    case "Camarero": empleadoAGuardar = new Camarero(); break;
                    case "Repartidor": empleadoAGuardar = new Repartidor(); break;
                    default: empleadoAGuardar = new Empleado(); break;
                }
            }

            binder.writeBean(empleadoAGuardar);
            
            empleadoAGuardar.setNegocio(negocio.getValue());
            if (negocio.getValue().getPropietario() != null) {
                empleadoAGuardar.setPropietario(negocio.getValue().getPropietario());
            }

            empleadoService.save(empleadoAGuardar);

            Notification.show("Empleado guardado con éxito", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            closeForm();
            if (onSaveListener != null) onSaveListener.run();

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error al guardar: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}