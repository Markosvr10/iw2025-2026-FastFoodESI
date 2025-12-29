package com.ESI.FastFoodESI.service.admin;

import com.ESI.FastFoodESI.model.EstadoEmpleado;
import com.ESI.FastFoodESI.repository.EstadoEmpleadoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstadoEmpleadoService {

    private final EstadoEmpleadoRepository estadoRepository;

    public EstadoEmpleadoService(EstadoEmpleadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    public List<EstadoEmpleado> findAll() {
        return estadoRepository.findAll();
    }

}