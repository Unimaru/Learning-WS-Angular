import { NgModule } from '@angular/core';
import { VirusAppComponent } from './virus-app.component';
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {VirusAppService} from "./virus-app.service";
import {HttpModule} from "@angular/http";

@NgModule({
  declarations:[
    VirusAppComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [VirusAppService],
  bootstrap: [VirusAppComponent]
})
export class VirusAppModule { }
