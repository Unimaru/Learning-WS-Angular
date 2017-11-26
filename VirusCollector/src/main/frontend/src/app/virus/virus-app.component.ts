import { Component, OnInit } from '@angular/core';
import {VirusAppService} from "./virus-app.service";

interface virus{
  id: number,
  name: string,
  isIsolated: boolean
}

@Component({
  selector: 'virus-app',
  templateUrl: './virus-app.component.html',
  styleUrls: ['./virus-app.component.css']
})
export class VirusAppComponent implements OnInit {

  viruses: Array<virus> = [];

  constructor(private virusService: VirusAppService) { }

  ngOnInit() {
    this.virusService.getVirusList().subscribe(
      (viruses: any[]) => {
        this.viruses = viruses;
      },
      (error) => console.log(error)
    );
  }

}
