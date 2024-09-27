import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { Workday } from '../models/workday';
import { tap, map } from 'rxjs/operators';

const API_URL = 'http://localhost:8080/api/workdays';

@Injectable({
  providedIn: 'root',
})
export class WorkdayService {
  private workdaysSubject = new BehaviorSubject<Workday[]>([]);
  private workdays$ = this.workdaysSubject.asObservable();

  constructor(private http: HttpClient) {}

  getWorkdayById(id: number): Observable<Workday> {
    return this.http.get<Workday>(`${API_URL}/${id}`);
  }

  createWorkday(workday: Workday): Observable<Workday> {
    return this.http
      .post<Workday>(API_URL, workday)
      .pipe(tap(() => this.loadWorkdays().subscribe()));
  }

  updateWorkday(workday: Workday): Observable<Workday> {
    return this.http
      .put<Workday>(`${API_URL}/${workday.id}`, workday)
      .pipe(tap(() => this.loadWorkdays().subscribe()));
  }

  getTimeSlotsForWorkday(
    workdayId: number
  ): Observable<{ time: string; occupied: boolean }[]> {
    return this.http.get<{ time: string; occupied: boolean }[]>(
      `${API_URL}/${workdayId}/timeslots`
    );
  }

  loadWorkdays(): Observable<Workday[]> {
    return this.http.get<Workday[]>(API_URL).pipe(
      tap((data: Workday[]) => {
        this.workdaysSubject.next(data);
      })
    );
  }

  getWorkdayForDate(date: Date): Workday | undefined {
    return this.workdaysSubject.value.find((workday) => {
      const workdayDate = new Date(workday.date).setHours(0, 0, 0, 0);
      const inputDate = date.setHours(0, 0, 0, 0);
      return workdayDate === inputDate;
    });
  }

  getWorkdaysObservable(): Observable<Workday[]> {
    return this.workdays$;
  }
}
