package com.umbrella.corporation.VirusCollector.repository;

import com.umbrella.corporation.VirusCollector.domain.Virus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VirusRepositoryInnerImpl implements VirusRepository{

    private List<Virus> virusList = new ArrayList<>();


    @Override
    public Iterable<Virus> findAll() {
        return virusList;
    }

    @Override
    public Virus save(Virus virus) {
        virusList.add(virus);
        return virus;
    }
}
