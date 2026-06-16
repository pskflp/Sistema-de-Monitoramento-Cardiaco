import { Component } from '@angular/core';
import { IonicModule, AlertController } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-monitoramento',
  templateUrl: './monitoramento.page.html',
  standalone: true,
  imports: [IonicModule, FormsModule, RouterLink],
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

  constructor(private api: ApiService, private alertCtrl: AlertController) {
    const id = localStorage.getItem('usuarioId');
    if (id) this.form.usuarioId = parseInt(id);
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