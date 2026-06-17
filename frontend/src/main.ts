import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideIonicAngular } from '@ionic/angular/standalone';
import { provideRouter, RouteReuseStrategy, withHashLocation } from '@angular/router';
import { IonicRouteStrategy } from '@ionic/angular/standalone';
import { routes } from './app/app.routes';
import { provideHttpClient } from '@angular/common/http';
import { provideZoneChangeDetection } from '@angular/core';

bootstrapApplication(AppComponent, {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideIonicAngular({}),
    provideRouter(routes),
    provideHttpClient(),
  ]
});