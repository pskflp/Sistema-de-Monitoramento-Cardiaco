import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { IonicModule, AlertController } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-cadastro',
  templateUrl: './cadastro.page.html',
  standalone: true,
  imports: [IonicModule, FormsModule, RouterLink],
})
export class CadastroPage {
  form = {
    nome: '', sobrenome: '', email: '', telefone: '',
    senha: '', confirmarSenha: '', dataNascimento: '', sexo: '', pais: ''
  };

  constructor(
    private api: ApiService,
    private router: Router,
    private alertCtrl: AlertController
  ) {}

  async cadastrar() {
    if (this.form.senha !== this.form.confirmarSenha) {
      const alert = await this.alertCtrl.create({
        header: 'Erro', message: 'As senhas não coincidem.', buttons: ['OK']
      });
      return alert.present();
    }

    this.api.cadastrar(this.form).subscribe({
      next: async () => {
        const alert = await this.alertCtrl.create({
          header: 'Sucesso', message: 'Conta criada! Faça login.', buttons: ['OK']
        });
        await alert.present();
        this.router.navigate(['/login']);
      },
      error: async () => {
        const alert = await this.alertCtrl.create({
          header: 'Erro', message: 'Não foi possível criar a conta.', buttons: ['OK']
        });
        alert.present();
      }
    });
  }
}