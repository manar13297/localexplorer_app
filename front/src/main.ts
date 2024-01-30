import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { register } from 'swiper/element/bundle';
import { HttpClientModule } from '@angular/common/http';
import {importProvidersFrom} from "@angular/core";

bootstrapApplication(AppComponent , {
...appConfig,
    providers: [
      ...appConfig.providers,
    importProvidersFrom(HttpClientModule),
  ]
})
  .catch((err) => console.error(err));


register();
