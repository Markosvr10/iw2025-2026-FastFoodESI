package com.ESI.FastFoodESI.service;
import com.ESI.FastFoodESI.model.Empleado;
import com.ESI.FastFoodESI.model.Propietario;
import com.ESI.FastFoodESI.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpleadoService {
    
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado> findAll() { return empleadoRepository.findAll(); }

    public List<Empleado> findAllByPropietario(Propietario propietario) {
        return empleadoRepository.findByPropietario(propietario);
    }

    public Empleado save(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public void delete(Empleado empleado) {
        empleadoRepository.delete(empleado);
    }
}