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
import { Lesson } from '../../models/lesson';

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
  lesson: Lesson;

  constructor(
    public dialogRef: MatDialogRef<EditClientsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private clientService: ClientService,
    private lessonService: LessonService
  ) {
    this.lesson = data.lesson;
  }

  ngOnInit(): void {}

  removeClient(client: Client): void {
    this.lesson.clients = this.lesson.clients.filter((c) => c.id !== client.id);
  }

  addClient(): void {}

  save(): void {}
}
