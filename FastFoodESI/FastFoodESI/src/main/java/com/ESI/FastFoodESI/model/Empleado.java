package com.ESI.FastFoodESI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.ESI.FastFoodESI.model.Negocio;

@Entity
@Table(name = "empleados")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Mantenemos la herencia para Repartidor, Cocina...
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Column(nullable = false)
    private String apellido;

    @NotBlank(message = "El DNI no puede estar vacío")
    @Size(min = 9, max = 9, message = "El DNI debe tener 9 caracteres")
    @Column(nullable = false, unique = true)
    private String dni;

    @Email(message = "El formato del correo no es válido")
    @Column(unique = true)
    private String correo;

    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNac;

    @NotNull(message = "El salario no puede ser nulo")
    @PositiveOrZero(message = "El salario no puede ser negativo")
    @Column(nullable = false)
    private BigDecimal salario;

    // --- RELACIONES ---

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_empleado_id", nullable = false)
    private EstadoEmpleado estado;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;
    
    /**
     * Relación Directa: Muchos Empleados pertenecen a un Propietario.
     * Esta es la entidad "dueña" de la relación.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;
    // --- CONSTRUCTORES ---

    public Empleado() {
    }

    // --- GETTERS Y SETTERS ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        // Si el correo viene vacío o solo tiene espacios, lo guardamos como NULL
        if (correo != null && correo.trim().isEmpty()) {
            this.correo = null;
        } else {
            this.correo = correo;
        }
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public EstadoEmpleado getEstado() {
        return estado;
    }

    public void setEstado(EstadoEmpleado estado) {
        this.estado = estado;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Negocio getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocio negocio) {
        this.negocio = negocio;
    }
}