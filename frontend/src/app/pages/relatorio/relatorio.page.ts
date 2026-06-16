import { Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-relatorio',
  templateUrl: './relatorio.page.html',
  standalone: true,
  imports: [IonicModule, CommonModule, RouterLink],
})
export class RelatorioPage {
  relatorio: any = null;

  constructor(private api: ApiService) {}

  carregar() {
    const id = parseInt(localStorage.getItem('usuarioId') || '0');
    this.api.gerarRelatorio(id).subscribe({
      next: (data) => this.relatorio = data,
      error: () => console.error('Erro ao carregar relatório')
    });
  }
}