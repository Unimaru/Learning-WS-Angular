package com.umbrella.corporation.VirusCollector.service;

import com.umbrella.corporation.VirusCollector.domain.Virus;
import com.umbrella.corporation.VirusCollector.repository.VirusRepository;
import org.springframework.stereotype.Service;

@Service
public class VirusCollectorServiceImpl implements VirusCollectorService{

    private VirusRepository virusRepository;

    public VirusCollectorServiceImpl(VirusRepository virusRepository) {
        this.virusRepository = virusRepository;
    }

    @Override
    public Iterable<Virus> list() {
        return virusRepository.findAll();
    }

    @Override
    public Virus save(Virus virus) {
        return virusRepository.save(virus);
    }
}
