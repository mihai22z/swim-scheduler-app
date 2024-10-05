import {
  Component,
  OnInit,
  ChangeDetectorRef,
  ChangeDetectionStrategy,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WorkdayService } from '../../services/workday.service';
import { LessonService } from '../../services/lesson.service';
import { Workday } from '../../models/workday';
import { CommonModule } from '@angular/common';
import { Lesson } from '../../models/lesson';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatLineModule } from '@angular/material/core';
import { MatRadioModule } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { EditClientsDialogComponent } from '../edit-clients-dialog/edit-clients-dialog.component';
import { ClientLesson } from '../../models/client-lesson';

@Component({
  selector: 'app-workday-detail',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    MatButtonModule,
    MatListModule,
    MatCardModule,
    MatExpansionModule,
    MatToolbarModule,
    MatIconModule,
    MatLineModule,
    MatRadioModule,
    FormsModule,
  ],
  templateUrl: './workday-detail.component.html',
  styleUrl: './workday-detail.component.css',
})
export class WorkdayDetailComponent {
  workday: Workday | undefined;
  clientsLoaded: boolean = false;
  attendanceStatuses: {
    [lessonId: number]: {
      [clientId: number]: 'ATTENDED' | 'ABSENT' | 'PENDING';
    };
  } = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private workdayService: WorkdayService,
    private lessonService: LessonService,
    private datePipe: DatePipe,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getWorkdayDetails();
  }

  getWorkdayDetails(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.workdayService.getWorkdayById(id).subscribe((data: Workday) => {
      this.workday = data;
      this.loadClientData();
    });
  }

  loadClientData(): void {
    if (!this.workday) return;

    for (let lesson of this.workday.lessons) {
      if (lesson.id) {
        this.lessonService
          .getClientsForLesson(lesson.id)
          .subscribe((clients) => {
            lesson.clients = clients;

            // Populate attendanceStatuses based on loaded data
            for (const client of clients) {
              const clientLesson = lesson.clientLessons.find(
                (cl) => cl.id.clientId === client.id
              );
              if (clientLesson) {
                if (!this.attendanceStatuses[lesson.id!]) {
                  this.attendanceStatuses[lesson.id!] = {};
                }
                this.attendanceStatuses[lesson.id!][client.id!] =
                  clientLesson.attendanceStatus || 'PENDING';
              }
            }
          });
      }
    }
  }

  getClientNames(lesson: Lesson): String {
    return lesson.clientLessons
      .map((clientLesson) => clientLesson.client?.name)
      .join(', ');
  }

  formatDate(date: string | undefined): string {
    return this.datePipe.transform(date, 'HH:mm')!;
  }

  markAttendance(lessonId: number, clientId: number): void {
    const attendanceStatus = this.attendanceStatuses[lessonId][clientId];
    const clientLesson = this.findClientLesson(lessonId, clientId);

    if (!clientLesson || attendanceStatus == 'PENDING') {
      return;
    }

    const attendanceData = {
      clientId: clientLesson.id.clientId,
      lessonId: clientLesson.id.lessonId,
      attendanceStatus: attendanceStatus,
    };

    this.lessonService.updateAttendance(attendanceData).subscribe(
      (response) => {
        console.log('Attendance updated successfully');
      },
      (error) => {
        console.error('Error updating attendance', error);
      }
    );
  }

  findClientLesson(
    lessonId: number,
    clientId: number
  ): ClientLesson | undefined {
    const lesson = this.workday?.lessons.find((l) => l.id === lessonId);
    return lesson?.clientLessons.find((cl) => cl.id.clientId === clientId);
  }

  editClients(lesson: Lesson): void {
    const dialogRef = this.dialog.open(EditClientsDialogComponent, {
      width: '400px',
      data: { lesson },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getWorkdayDetails();
      }
    });
  }

  rescheduleLesson(lessonId: number | null): void {
    if (!lessonId || !this.workday) {
      return;
    }
    this.router.navigate(['/add-lesson'], {
      queryParams: {
        lessonId: lessonId,
        editMode: true,
        workday: JSON.stringify(this.workday),
      },
    });
  }

  removeLesson(lessonId: number | null): void {
    if (confirm('Are you sure you want to delete this lesson?')) {
      this.lessonService.deleteLesson(lessonId!).subscribe(
        () => {
          console.log('Lesson deleted successfully');
          this.getWorkdayDetails();
          this.router.navigate(['/calendar']);
        },
        (error) => {
          console.error('Error deleting lesson:', error);
        }
      );
    }
  }
}
