import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AlertController, IonContent, IonItem, IonLabel, IonInput, IonSelect, IonSelectOption, IonButton } from '@ionic/angular/standalone';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-cadastro',
  templateUrl: './cadastro.page.html',
  styleUrl: './cadastro.page.scss',
  standalone: true,
  imports: [FormsModule, RouterLink, IonContent, IonItem, IonLabel, IonInput, IonSelect, IonSelectOption, IonButton],
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
    // Validações básicas no front-end
    if (!this.form.nome || !this.form.email || !this.form.senha || !this.form.dataNascimento) {
      const alert = await this.alertCtrl.create({
        header: 'Atenção', message: 'Preencha todos os campos obrigatórios.', buttons: ['OK']
      });
      return alert.present();
    }

    if (this.form.senha.length < 8) {
      const alert = await this.alertCtrl.create({
        header: 'Senha Curta', message: 'A senha deve ter pelo menos 8 caracteres.', buttons: ['OK']
      });
      return alert.present();
    }

    if (this.form.senha !== this.form.confirmarSenha) {
      const alert = await this.alertCtrl.create({
        header: 'Erro', message: 'As senhas não coincidem.', buttons: ['OK']
      });
      return alert.present();
    }

    this.api.cadastrar(this.form).subscribe({
      next: async () => {
        const alert = await this.alertCtrl.create({
          header: 'Sucesso', message: 'Conta criada com sucesso! Você já pode fazer login.', buttons: ['OK']
        });
        await alert.present();
        this.router.navigate(['/login']);
      },
      error: async (err) => {
        let msg = 'Não foi possível criar a conta. Verifique os dados.';
        if (err.error && err.error.mensagem) msg = err.error.mensagem;
        
        const alert = await this.alertCtrl.create({
          header: 'Erro no Cadastro', message: msg, buttons: ['OK']
        });
        alert.present();
      }
    });
  }
}