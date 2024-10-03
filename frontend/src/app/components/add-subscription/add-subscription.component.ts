import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { ClientSelectorComponent } from '../client-selector/client-selector.component';
import { Client } from '../../models/client';

@Component({
  selector: 'app-add-subscription',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    ClientSelectorComponent,
  ],
  templateUrl: './add-subscription.component.html',
  styleUrl: './add-subscription.component.css',
})
export class AddSubscriptionComponent implements OnInit {
  daysOfWeek: { day: string; selected: boolean }[] = [
    { day: 'Monday', selected: false },
    { day: 'Tuesday', selected: false },
    { day: 'Wednesday', selected: false },
    { day: 'Thursday', selected: false },
    { day: 'Friday', selected: false },
    { day: 'Saturday', selected: false },
    { day: 'Sunday', selected: false },
  ];

  selectedTimes: { [day: string]: string } = {};
  timeSlots: string[] = [];

  subscription = {
    totalWeeks: 0,
    startDate: new Date(),
  };

  selectedClients: Client[] = [];

  ngOnInit(): void {
    const startTime = new Date();
    startTime.setHours(9, 0, 0, 0);
    const endTime = new Date();
    endTime.setHours(20, 15, 0, 0);

    while (startTime <= endTime) {
      this.timeSlots.push(this.formatTime(startTime));
      startTime.setMinutes(startTime.getMinutes() + 15);
    }
  }

  formatTime(date: Date): string {
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  toggleDaySelection(day: string): void {
    const dayObj = this.daysOfWeek.find((d) => d.day === day);
    if (dayObj) {
      dayObj.selected = !dayObj.selected;
    }
  }

  selectedDays(): string[] {
    return this.daysOfWeek.filter((d) => d.selected).map((d) => d.day);
  }

  onSelectedClientsChange(clients: Client[]): void {
    this.selectedClients = clients;
  }

  addSubscription(): void {
    const subscriptionDetails = {
      days: this.selectedDays().map((day) => ({
        day,
        time: this.selectedTimes[day] || 'Not selected',
      })),
      totalWeeks: this.subscription.totalWeeks,
      startDate: this.subscription.startDate,
      clients: this.selectedClients,
    };
  }
}
