import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {environment} from './environments/environment';
import {VirusAppModule} from "./app/virus/virus-app.module";
import {FilesModule} from "./app/files/files.module";

if (environment.production) {
  enableProdMode();
}
//
// platformBrowserDynamic().bootstrapModule(VirusAppModule)
//   .catch(err => console.log(err));

platformBrowserDynamic().bootstrapModule(FilesModule)
  .catch(err => console.log(err))
