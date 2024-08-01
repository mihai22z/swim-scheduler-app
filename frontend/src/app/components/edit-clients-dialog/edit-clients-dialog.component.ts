import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { Client } from '../../models/client';
import { ClientService } from '../../services/client.service';
import { LessonService } from '../../services/lesson.service';

@Component({
  selector: 'app-edit-clients-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatListModule,
    MatIconModule,
  ],
  templateUrl: './edit-clients-dialog.component.html',
  styleUrl: './edit-clients-dialog.component.css',
})
export class EditClientsDialogComponent implements OnInit {
  clients: Client[] = [];
  lessonId: number;

  constructor(
    public dialogRef: MatDialogRef<EditClientsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private clientService: ClientService,
    private lessonService: LessonService
  ) {
    this.lessonId = data.lessonId;
  }

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.lessonService.getLessonById(this.lessonId).subscribe((lesson) => {
      this.clients = lesson.clientLessons.map(
        (clientLesson) => clientLesson.client!
      );
    });
  }

  removeClient(client: Client): void {
    this.clients = this.clients.filter((c) => c.id !== client.id);
  }

  addClient(): void {}

  save(): void {}
}
