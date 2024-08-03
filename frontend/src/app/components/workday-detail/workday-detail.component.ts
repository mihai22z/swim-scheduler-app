import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { WorkdayService } from '../../services/workday.service';
import { ClientService } from '../../services/client.service';
import { LessonService } from '../../services/lesson.service';
import { Workday } from '../../models/workday';
import { CommonModule } from '@angular/common';
import { Lesson } from '../../models/lesson';
import { DatePipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { EditClientsDialogComponent } from '../edit-clients-dialog/edit-clients-dialog.component';

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
  ],
  templateUrl: './workday-detail.component.html',
  styleUrl: './workday-detail.component.css',
})
export class WorkdayDetailComponent {
  workday: Workday | undefined;

  constructor(
    private route: ActivatedRoute,
    private workdayService: WorkdayService,
    private clientService: ClientService,
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

    for (var lesson of this.workday.lessons) {
      if (lesson.id) {
        this.lessonService
          .getClientsForLesson(lesson.id)
          .subscribe((clients) => {
            lesson.clients = clients;
            console.log(`Lesson ${lesson.id} clients:`, lesson.clients);
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

  rescheduleLesson(lessonId: number | null): void {}

  removeLesson(lessonId: number | null): void {}
}
