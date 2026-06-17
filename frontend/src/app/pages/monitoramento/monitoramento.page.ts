import { Component } from '@angular/core';
import { AlertController, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon, IonContent, IonItem, IonLabel, IonInput, IonToggle } from '@ionic/angular/standalone';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';

import { Router } from '@angular/router';
import { addIcons } from 'ionicons';
import { analyticsOutline, peopleOutline, logOutOutline } from 'ionicons/icons';

@Component({
  selector: 'app-monitoramento',
  templateUrl: './monitoramento.page.html',
  styleUrl: './monitoramento.page.scss',
  standalone: true,
  imports: [FormsModule, RouterLink, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon, IonContent, IonItem, IonLabel, IonInput, IonToggle],
})
export class MonitoramentoPage {
  form = {
    usuarioId: 0,
    dataRegistro: new Date().toISOString(),
    pressaoSistolica: null,
    pressaoDiastolica: null,
    frequenciaCardiaca: null,
    oxigenacao: null,
    peso: null,
    faltaDeAr: false,
    dorNoPeito: false,
    tontura: false,
  };

  constructor(
    private api: ApiService, 
    private alertCtrl: AlertController,
    private router: Router
  ) {
    addIcons({ 'analytics-outline': analyticsOutline, 'people-outline': peopleOutline, 'log-out-outline': logOutOutline });
    const id = localStorage.getItem('usuarioId');
    if (id) this.form.usuarioId = parseInt(id);
  }

  logout() {
    localStorage.removeItem('usuarioId');
    this.router.navigate(['/login']);
  }

  registrar() {
    this.form.dataRegistro = new Date().toISOString();
    this.api.registrarMonitoramento(this.form).subscribe({
      next: async () => {
        const alert = await this.alertCtrl.create({
          header: 'Sucesso', message: 'Registro salvo!', buttons: ['OK']
        });
        alert.present();
      },
      error: async () => {
        const alert = await this.alertCtrl.create({
          header: 'Erro', message: 'Não foi possível salvar.', buttons: ['OK']
        });
        alert.present();
      }
    });
  }
}