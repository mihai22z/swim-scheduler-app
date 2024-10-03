import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client';

@Component({
  selector: 'app-client-selector',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, FormsModule],
  templateUrl: './client-selector.component.html',
  styleUrl: './client-selector.component.css',
})
export class ClientSelectorComponent {
  clients: Client[] = [];
  availableClients: Client[] = [];
  filteredClients: Client[] = [];
  searchQuery: string = '';

  @Input() selectedClients: Client[] = [];
  @Output() selectedClientsChange = new EventEmitter<Client[]>();

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {
    this.clientService.getClients().subscribe((data: Client[]) => {
      this.loadClients();
    });
  }

  loadClients(): void {
    this.clientService.getClients().subscribe((data: Client[]) => {
      this.clients = data;
      this.availableClients = [...data];
      this.filteredClients = [...data];

      if (this.selectedClients && this.selectedClients.length > 0) {
        this.filterClients();
      }
    });
  }

  filterClients(): void {
    const selectedClientsIds = this.selectedClients.map((client) => client.id);

    this.filteredClients = this.availableClients.filter(
      (client) =>
        !selectedClientsIds.includes(client.id) &&
        (!this.searchQuery ||
          client.name.toLowerCase().includes(this.searchQuery.toLowerCase()))
    );
  }

  addClient(client: Client): void {
    if (
      !this.selectedClients.some(
        (selectedClient) => selectedClient.id === client.id
      )
    ) {
      this.selectedClients.push(client);
      this.filterClients();
      this.selectedClientsChange.emit(this.selectedClients);
    }
  }

  removeClient(client: Client): void {
    this.selectedClients = this.selectedClients.filter(
      (selectedClient) => selectedClient.id !== client.id
    );
    this.filterClients();
    this.selectedClientsChange.emit(this.selectedClients);
  }
}
