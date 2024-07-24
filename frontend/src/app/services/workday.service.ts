import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Workday } from '../models/workday';

const API_URL = 'http://localhost:8080/api/workdays';

@Injectable({
  providedIn: 'root',
})
export class WorkdayService {
  constructor(private http: HttpClient) {}

  getWorkDays(): Observable<Workday[]> {
    return this.http.get<Workday[]>(API_URL);
  }

  getWorkdayById(id: number): Observable<Workday> {
    return this.http.get<Workday>(`${API_URL}/${id}`);
  }
}
