import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {

  private BASE_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  cadastrar(dados: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/usuarios/cadastro`, dados);
  }

  login(dados: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/usuarios/login`, dados);
  }

  registrarMonitoramento(dados: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/monitoramentos`, dados);
  }

  listarMonitoramentos(usuarioId: number): Observable<any> {
    return this.http.get(`${this.BASE_URL}/monitoramentos/usuario/${usuarioId}`);
  }

  gerarRelatorio(usuarioId: number): Observable<any> {
    return this.http.get(`${this.BASE_URL}/relatorios/usuario/${usuarioId}`);
  }
}