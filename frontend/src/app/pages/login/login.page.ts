import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { IonicModule, AlertController } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  standalone: true,
  imports: [IonicModule, FormsModule, RouterLink],
})
export class LoginPage {
  email = '';
  senha = '';

  constructor(
    private api: ApiService,
    private router: Router,
    private alertCtrl: AlertController
  ) {}

  login() {
    this.api.login({ email: this.email, senha: this.senha }).subscribe({
      next: (usuario) => {
        localStorage.setItem('usuarioId', usuario.id);
        this.router.navigate(['/monitoramento']);
      },
      error: async () => {
        const alert = await this.alertCtrl.create({
          header: 'Erro',
          message: 'E-mail ou senha incorretos.',
          buttons: ['OK'],
        });
        await alert.present();
      },
    });
  }
}