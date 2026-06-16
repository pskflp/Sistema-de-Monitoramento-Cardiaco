import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./pages/login/login.page').then(m => m.LoginPage) },
  { path: 'cadastro', loadComponent: () => import('./pages/cadastro/cadastro.page').then(m => m.CadastroPage) },
  { path: 'monitoramento', loadComponent: () => import('./pages/monitoramento/monitoramento.page').then(m => m.MonitoramentoPage) },
  { path: 'relatorio', loadComponent: () => import('./pages/relatorio/relatorio.page').then(m => m.RelatorioPage) },
  { path: 'integrantes', loadComponent: () => import('./pages/integrantes/integrantes.page').then(m => m.IntegrantesPage) },
];