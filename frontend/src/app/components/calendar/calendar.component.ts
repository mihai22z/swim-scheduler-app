import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { WorkdayService } from '../../services/workday.service';
import { Workday } from '../../models/workday';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css',
})
export class CalendarComponent implements OnInit {
  @Input() mode: 'overview' | 'lesson-scheduler' = 'overview';
  @Output() daySelected = new EventEmitter<{
    date: Date;
    workday: Workday | null;
  }>();

  currentMonth: Date = new Date();
  workdays: Workday[] = [];
  selectedDate: Date | null = null;
  private workdaysSubscription: Subscription | undefined;

  constructor(
    private workdayService: WorkdayService,
    private router: Router,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.loadWorkdaysForMonth(this.currentMonth);
  }

  ngOnDestroy(): void {
    if (this.workdaysSubscription) {
      this.workdaysSubscription.unsubscribe();
    }
  }

  loadWorkdaysForMonth(date: Date): void {
    this.workdayService
      .loadWorkdaysForMonth(date)
      .subscribe((data: Workday[]) => {
        this.workdays = data;
        this.generateCalendar();
      });
  }

  nextMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.setMonth(this.currentMonth.getMonth() + 1)
    );
    this.loadWorkdaysForMonth(this.currentMonth);
  }

  previousMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.setMonth(this.currentMonth.getMonth() - 1)
    );
    this.loadWorkdaysForMonth(this.currentMonth);
  }

  getWorkdayForDate(date: Date): Workday | undefined {
    if (!this.workdays) {
      return undefined;
    }

    const workday = this.workdays.find(
      (workday) => new Date(workday.date).toDateString() === date.toDateString()
    );
    return workday;
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

  selectDay(day: Date): void {
    this.selectedDate = day;
    console.log('Selected Date:', this.selectedDate);
    if (this.mode === 'lesson-scheduler') {
      const workday = this.getWorkdayForDate(day);
      this.daySelected.emit({ date: day, workday: workday || null });
    } else {
      const workdayId = this.getWorkdayForDate(day)?.id;
      if (workdayId != undefined) {
        this.router.navigate(['/workday', workdayId]);
      }
    }
  }

  viewDayDetails(workdayId: number | undefined): void {
    if (workdayId !== undefined) {
      this.router.navigate(['/workday', workdayId]);
    }
  }

  formatDate(date: string | undefined): string {
    return this.datePipe.transform(date, 'HH:mm')!;
  }
}
