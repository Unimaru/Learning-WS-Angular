package com.umbrella.corporation.VirusCollector.service;

import com.umbrella.corporation.VirusCollector.domain.Virus;

public interface VirusCollectorService {

    Iterable<Virus> list();

    Virus save(Virus virus);
}
