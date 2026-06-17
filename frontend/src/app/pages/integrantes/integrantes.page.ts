import { Component } from '@angular/core';
import { IonHeader, IonToolbar, IonButtons, IonBackButton, IonTitle, IonContent, IonList, IonItem, IonIcon, IonLabel } from '@ionic/angular/standalone';
import { RouterLink } from '@angular/router';
import { addIcons } from 'ionicons';
import { personCircleOutline } from 'ionicons/icons';

@Component({
  selector: 'app-integrantes',
  templateUrl: './integrantes.page.html',
  styleUrl: './integrantes.page.scss',
  standalone: true,
  imports: [IonHeader, IonToolbar, IonButtons, IonBackButton, IonTitle, IonContent, IonList, IonItem, IonIcon, IonLabel],
})
export class IntegrantesPage {
  constructor() {
    addIcons({ 'person-circle-outline': personCircleOutline });
  }
}