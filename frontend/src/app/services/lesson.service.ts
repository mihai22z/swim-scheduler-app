import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Lesson } from '../models/lesson';

const API_URL = 'http://localhost:8080/api/lessons';

@Injectable({
  providedIn: 'root',
})
export class LessonService {
  constructor(private http: HttpClient) {}

  getLessons(): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(API_URL);
  }

  getLessonById(id: number): Observable<Lesson> {
    return this.http.get<Lesson>(`${API_URL}/${id}`);
  }
}
