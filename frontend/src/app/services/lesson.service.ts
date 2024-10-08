import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map } from 'rxjs';
import { Lesson } from '../models/lesson';
import { Client } from '../models/client';

const API_URL = 'http://localhost:8080/api/lessons';

@Injectable({
  providedIn: 'root',
})
export class LessonService {
  private clientsSubject = new BehaviorSubject<Client[]>([]);
  public clients$ = this.clientsSubject.asObservable();

  constructor(private http: HttpClient) {}

  getLessons(): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(API_URL);
  }

  getLessonById(id: number): Observable<Lesson> {
    return this.http.get<Lesson>(`${API_URL}/${id}`);
  }

  getClientsForLesson(lessonId: number): Observable<Client[]> {
    return this.http.get<Client[]>(`${API_URL}/${lessonId}/clients`).pipe(
      map((clients) => {
        this.clientsSubject.next(clients);
        return clients;
      })
    );
  }

  updateLesson(id: number, lesson: Lesson): Observable<Lesson> {
    return this.http.put<Lesson>(`${API_URL}/${id}`, lesson);
  }

  updateAttendance(attendanceData: {
    clientId: number;
    lessonId: number;
    attendanceStatus: string;
  }): Observable<any> {
    return this.http.put(`${API_URL}/updateAttendance`, attendanceData);
  }

  createLesson(lesson: Lesson): Observable<Lesson> {
    return this.http.post<Lesson>(API_URL, lesson);
  }

  createSubscription(subscriptionDetails: any): Observable<any> {
    return this.http.post(`${API_URL}/subscriptions`, subscriptionDetails);
  }

  deleteLesson(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
