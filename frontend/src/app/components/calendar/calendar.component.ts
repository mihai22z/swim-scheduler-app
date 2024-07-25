import { Component, OnInit } from '@angular/core';
import { WorkdayService } from '../../services/workday.service';
import { Workday } from '../../models/workday';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css',
})
export class CalendarComponent implements OnInit {
  currentMonth: Date = new Date();
  workdays: Workday[] = [];

  constructor(private workdayService: WorkdayService) {}

  ngOnInit(): void {
    this.loadMockWorkdays();
  }

  loadWorkdays(): void {
    this.workdayService.getWorkDays().subscribe((data: Workday[]) => {
      this.workdays = data;
    });
  }

  loadMockWorkdays(): void {
    this.workdays = [
      {
        id: 1,
        date: '2024-07-26',
        startTime: '09:00',
        endTime: '17:00',
        lessons: [],
      },
    ];
  }

  getWorkdayForDate(date: Date): Workday | undefined {
    return this.workdays.find(
      (workday) => new Date(workday.date).toDateString() === date.toDateString()
    );
  }

  generateCalendar(): (Date | null)[][] {
    const firstDayOfMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth(),
      1
    );
    const lastDayOfMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth() + 1,
      0
    );

    const calendar: (Date | null)[][] = [];
    let week: (Date | null)[] = [];

    // Fill the first week with nulls up to the first day of the month
    for (let i = 0; i < firstDayOfMonth.getDay(); i++) {
      week.push(null);
    }

    // Fill the calendar with the actual days of the month
    for (let day = 1; day <= lastDayOfMonth.getDate(); day++) {
      week.push(
        new Date(
          this.currentMonth.getFullYear(),
          this.currentMonth.getMonth(),
          day
        )
      );
      if (week.length === 7) {
        calendar.push(week);
        week = [];
      }
    }

    // Fill the last week with nulls if it's not complete
    if (week.length > 0) {
      while (week.length < 7) {
        week.push(null);
      }
      calendar.push(week);
    }

    return calendar;
  }

  isCurrentDay(day: Date): boolean {
    return day.toDateString() === new Date().toDateString();
  }
}
