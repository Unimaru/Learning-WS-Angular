package com.umbrella.corporation.VirusCollector.controller;

import com.umbrella.corporation.VirusCollector.domain.Virus;
import com.umbrella.corporation.VirusCollector.service.VirusCollectorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/virus")
public class VirusController {

    private VirusCollectorService virusCollectorService;

    public VirusController(VirusCollectorService virusCollectorService) {
        this.virusCollectorService = virusCollectorService;
    }

    @GetMapping(value = {"","/"})
    public Iterable<Virus> listVirus(){
        return virusCollectorService.list();
    }

    @PostMapping("/save")
    public Virus saveVirus(@RequestBody Virus virus){
        return virusCollectorService.save(virus);
    }

}
