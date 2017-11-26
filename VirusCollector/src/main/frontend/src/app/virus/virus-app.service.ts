import { Injectable } from '@angular/core';
import "rxjs/Rx"
import {Http, Response} from "@angular/http";

@Injectable()
export class VirusAppService {

  constructor(private http: Http) { }

  getVirusList(){
    return this.http.get('/api/virus').map(
      (response: Response) => {
        return response.json();
      }
    );
  }

}
