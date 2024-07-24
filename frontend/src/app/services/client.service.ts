import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../models/client';

const API_URL = 'http://localhost:8080/api/clients';

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  constructor(private http: HttpClient) {}

  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(API_URL);
  }

  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${API_URL}/${id}`);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(API_URL, client);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${API_URL}/${id}`, client);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
