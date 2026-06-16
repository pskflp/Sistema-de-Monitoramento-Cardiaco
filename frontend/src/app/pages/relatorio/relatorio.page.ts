import { Component } from '@angular/core';
import { IonHeader, IonToolbar, IonButtons, IonBackButton, IonTitle, IonContent, IonSpinner, IonIcon } from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api';

import { analyticsOutline, documentTextOutline, warning } from 'ionicons/icons';
import { addIcons } from 'ionicons';

@Component({
  selector: 'app-relatorio',
  templateUrl: './relatorio.page.html',
  styleUrl: './relatorio.page.scss',
  standalone: true,
  imports: [CommonModule, RouterLink, IonHeader, IonToolbar, IonButtons, IonBackButton, IonTitle, IonContent, IonSpinner, IonIcon, IonButton],
})
export class RelatorioPage {
  relatorio: any = null;
  carregando = true;
  erro = false;

  constructor(private api: ApiService) {
    addIcons({ 'document-text-outline': documentTextOutline, 'warning': warning, 'analytics-outline': analyticsOutline });
  }

  ionViewWillEnter() {
    this.carregar();
  }

  carregar() {
    this.carregando = true;
    this.erro = false;
    const id = parseInt(localStorage.getItem('usuarioId') || '0');
    
    if (id === 0) {
      this.carregando = false;
      this.erro = true;
      return;
    }

    this.api.gerarRelatorio(id).subscribe({
      next: (data) => {
        this.relatorio = data;
        this.carregando = false;
      },
      error: (err) => {
        console.error('Erro ao carregar relatório', err);
        this.carregando = false;
        this.erro = true;
      }
    });
  }
}