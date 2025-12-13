package com.ESI.FastFoodESI.service;

import com.ESI.FastFoodESI.model.Negocio;
import com.ESI.FastFoodESI.model.Propietario;
import com.ESI.FastFoodESI.repository.NegocioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NegocioService {

    private final NegocioRepository negocioRepository;

    public NegocioService(NegocioRepository negocioRepository) {
        this.negocioRepository = negocioRepository;
    }

    public Negocio save(Negocio negocio) {
        if (negocio == null) {
            System.err.println("Intento de guardar negocio nulo");
            return null;
        }
        return negocioRepository.save(negocio);
    }

    public List<Negocio> findAll() {
        return negocioRepository.findAll();
    }
    
    public List<Negocio> findAllByPropietario(Propietario propietario) {
        return negocioRepository.findByPropietario(propietario);
    }

    public void delete(Negocio negocio) {
        negocioRepository.delete(negocio);
    }
}