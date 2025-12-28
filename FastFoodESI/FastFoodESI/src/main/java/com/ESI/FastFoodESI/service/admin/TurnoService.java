package com.ESI.FastFoodESI.service.admin;

import com.ESI.FastFoodESI.model.Turno;
import com.ESI.FastFoodESI.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }
}