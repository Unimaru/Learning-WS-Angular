package com.umbrella.corporation.VirusCollector.repository;

import com.umbrella.corporation.VirusCollector.domain.Virus;

public interface VirusRepository {

    Iterable<Virus> findAll();

    Virus save(Virus virus);
}
