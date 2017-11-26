import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.css']
})
export class FilesComponent {

  files: Array<FileEvent> = [];

  private ws = new WebSocket('ws://localhost:8080/ws/files')

  constructor() {
    this.ws.onmessage = (me: MessageEvent) => {
      const data = JSON.parse(me.data) as FileEvent;
      this.files.push(data);
    }
  }

}

export interface FileEvent{
  path: string;
  sessionId: string;
}
