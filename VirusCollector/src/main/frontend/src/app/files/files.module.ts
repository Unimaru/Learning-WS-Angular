import { NgModule } from '@angular/core';
import {FilesComponent} from "./files.component";
import {BrowserModule} from "@angular/platform-browser";

@NgModule({
  declarations: [FilesComponent],
  imports: [
    BrowserModule
  ],
  bootstrap: [FilesComponent]
})
export class FilesModule { }
