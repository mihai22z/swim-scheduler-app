import {
  Component,
  OnInit,
  AfterViewInit,
  ChangeDetectorRef,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LessonService } from '../../services/lesson.service';
import { ClientService } from '../../services/client.service';
import { WorkdayService } from '../../services/workday.service';
import { Lesson } from '../../models/lesson';
import { Client } from '../../models/client';
import { Workday } from '../../models/workday';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { CalendarComponent } from '../calendar/calendar.component';
import { ClientSelectorComponent } from '../client-selector/client-selector.component';

@Component({
  selector: 'app-lesson-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatCardModule,
    CalendarComponent,
    ClientSelectorComponent,
  ],
  templateUrl: './lesson-form.component.html',
  styleUrl: './lesson-form.component.css',
  providers: [DatePipe],
})
export class LessonFormComponent implements OnInit {
  lesson: Lesson = {
    id: null,
    startTime: '',
    endTime: '',
    clients: [],
    workday: null,
    clientLessons: [],
  };
  workdays: Workday[] = [];
  selectedClients: Client[] = [];
  timeSlots: { time: string; occupied: boolean; selected: boolean }[] = [];
  isEditMode: boolean = false;
  selectedDate: Date | null = null;
  lessonsForSelectedDate: Lesson[] = [];
  lessonDuration: number = 45;
  formattedStartTime: string = '';
  formattedEndTime: string = '';

  constructor(
    private lessonService: LessonService,
    private clientService: ClientService,
    private workdayService: WorkdayService,
    private route: ActivatedRoute,
    private router: Router,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.clientService.getClients().subscribe((data: Client[]) => {});

    this.workdayService.loadWorkdays().subscribe((data: Workday[]) => {
      this.workdays = data;
    });

    this.route.queryParams.subscribe((params) => {
      const lessonId = params['lessonId'];
      this.isEditMode = params['editMode'] === 'true';

      if (params['workday']) {
        this.lesson.workday = JSON.parse(params['workday']);
        this.updateTimeline(this.lesson.workday);
      }

      if (this.isEditMode && lessonId) {
        this.lessonService
          .getLessonById(Number(lessonId))
          .subscribe((data: Lesson) => {
            this.lesson = data;
            this.selectedDate = this.extractDateFromDateTime(data.startTime);
            this.lesson.startTime = this.formatTime(data.startTime);
            this.lesson.endTime = this.formatTime(data.endTime);

            this.lessonService
              .getClientsForLesson(Number(lessonId))
              .subscribe((clients: Client[]) => {
                this.selectedClients = clients;
              });

            if (!this.lesson.workday) {
              console.log(
                'Workday missing, fetching by date:',
                this.selectedDate
              );

              const workday = this.workdayService.getWorkdayForDate(
                this.selectedDate
              );
              if (workday) {
                this.lesson.workday = workday;
                this.updateTimeline(workday);
              } else {
                console.error('No workday found for this date');
              }
            } else {
              this.updateTimeline(this.lesson.workday);
            }
          });
      }
    });
  }

  onDaySelected(event: { date: Date; workday: Workday | null }): void {
    this.selectedDate = event.date;
    this.updateTimeline(event.workday);
    this.lesson.startTime = '';
    this.lesson.endTime = '';
  }

  updateTimeline(workday: Workday | null): void {
    if (workday && workday.id) {
      this.workdayService
        .getTimeSlotsForWorkday(workday.id)
        .subscribe((slots) => {
          this.timeSlots = slots.map((slot) => ({
            time: this.formatTime(slot.time),
            occupied: slot.occupied,
            selected: false,
          }));
        });
    } else {
      const startTime = new Date();
      startTime.setHours(9, 0, 0, 0);
      this.timeSlots = [];

      while (startTime.getHours() < 21) {
        this.timeSlots.push({
          time: this.formatTime(startTime.toISOString()),
          occupied: false,
          selected: false,
        });
        startTime.setMinutes(startTime.getMinutes() + 15);
      }
    }
  }

  formatTime(dateTime: string): string {
    const date = new Date(dateTime);
    return this.datePipe.transform(date, 'HH:mm')!;
  }

  formatTimeForBackend(date: Date): string {
    return `${this.datePipe.transform(date, 'yyyy-MM-ddTHH:mm:ss')}`;
  }

  onTimeSlotClick(slotIndex: number): void {
    const selectedSlot = this.timeSlots[slotIndex];

    if (!selectedSlot.occupied && this.canScheduleLessonAt(selectedSlot.time)) {
      const startTime = this.parseTimeFromString(selectedSlot.time);
      this.formattedStartTime = this.datePipe.transform(startTime, 'HH:mm')!;

      const maxEndTime = new Date(startTime);
      maxEndTime.setHours(21, 0, 0, 0);

      const endTime = new Date(startTime.getTime() + 45 * 60 * 1000);
      this.formattedEndTime = this.datePipe.transform(endTime, 'HH:mm')!;

      if (endTime <= maxEndTime) {
        this.lesson.startTime = this.formattedStartTime;
        this.lesson.endTime = this.formattedEndTime;
        this.updateSelectedSlots(startTime, endTime);
      }

      this.cdr.detectChanges();
    }
  }

  parseTimeFromString(timeString: string): Date {
    const [hours, minutes] = timeString.split(':').map(Number);
    const selectedDate = this.selectedDate
      ? new Date(this.selectedDate)
      : new Date();
    selectedDate.setHours(hours, minutes, 0, 0);
    return selectedDate;
  }

  updateSelectedDate(dateString: string): void {
    if (this.selectedDate) {
      const newDate = new Date(dateString);
      this.selectedDate.setFullYear(
        newDate.getFullYear(),
        newDate.getMonth(),
        newDate.getDate()
      );
    }
  }

  canScheduleLessonAt(startTimeStr: string): boolean {
    const startTime = this.parseTimeFromString(startTimeStr);
    const endTime = new Date(startTime.getTime() + 45 * 60 * 1000);

    return !this.timeSlots.some((slot) => {
      if (slot.occupied) {
        const slotTime = this.parseTimeFromString(slot.time);
        return slotTime >= startTime && slotTime < endTime;
      }
      return false;
    });
  }

  updateSelectedSlots(startTime: Date, endTime: Date): void {
    this.timeSlots.forEach((slot) => {
      const slotTime = this.parseTimeFromString(slot.time);
      slot.selected = slotTime >= startTime && slotTime < endTime;
    });
  }

  getSelectedClientsDisplay(): string {
    return this.selectedClients.length
      ? this.selectedClients.map((client) => client.name).join(', ')
      : 'Select clients';
  }

  onSelectedClientsChange(clients: Client[]): void {
    this.selectedClients = clients;
  }

  saveLesson(): void {
    if (!this.selectedDate || !this.lesson.startTime || !this.lesson.endTime) {
      alert('Please select a valid date and time.');
      return;
    }

    const formattedStartTime = this.combineDateAndTime(
      this.selectedDate,
      this.lesson.startTime.split('T').pop() || this.lesson.startTime
    );
    const formattedEndTime = this.combineDateAndTime(
      this.selectedDate,
      this.lesson.endTime.split('T').pop() || this.lesson.endTime
    );

    this.lesson.startTime = formattedStartTime;
    this.lesson.endTime = formattedEndTime;
    this.lesson.clients = this.selectedClients;
    this.lesson.clientLessons = this.selectedClients.map((client) => ({
      id: {
        clientId: client.id!,
        lessonId: this.lesson.id ?? 0,
      },
      client: client,
    }));

    if (this.isEditMode) {
      this.lessonService.updateLesson(this.lesson.id!, this.lesson).subscribe(
        (response) => {
          if (this.lesson.workday) {
            console.log('Workday ID: ', this.lesson.workday.id);
          }
          console.log('Lesson updated successfully:', response);
          this.router.navigate(['/calendar']);
        },
        (error) => {
          console.error('Error updating lesson:', error);
        }
      );
    } else {
      this.lessonService.createLesson(this.lesson).subscribe(
        (response) => {
          console.log('Lesson created successfully:', response);
          this.router.navigate(['/calendar']);
        },
        (error) => {
          console.error('Error creating lesson:', error);
        }
      );
    }
  }

  combineDateAndTime(date: Date, time: string): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');

    const [hours, minutes] = time.split(':');
    const seconds = '00';

    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }

  extractDate(dateTime: string): string {
    if (dateTime && !isNaN(Date.parse(dateTime))) {
      const date = new Date(dateTime);
      return date.toISOString().split('T')[0];
    } else {
      return new Date().toDateString();
    }
  }

  extractDateFromDateTime(dateTime: string): Date {
    const [datePart, timePart] = dateTime.split('T');
    const [year, month, day] = datePart.split('-').map(Number);
    const [hours, minutes] = timePart.split(':').map(Number);

    return new Date(year, month - 1, day, hours, minutes);
  }

  getWorkdayForDate(date: Date): Workday | undefined {
    return this.workdayService.getWorkdayForDate(date);
  }

  cancel(): void {
    this.router.navigate(['/calendar']);
  }
}
