import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './client-form.component.html',
  styleUrl: './client-form.component.css',
})
export class ClientFormComponent implements OnInit {
  client: Client = {
    id: null,
    name: '',
    phone: '',
    notes: '',
    clientLessons: [],
  };

  constructor(
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.clientService
        .getClientById(+id)
        .subscribe((client) => (this.client = client));
    }
  }

  saveClient(): void {
    if (this.client.id) {
      this.clientService
        .updateClient(this.client.id, this.client)
        .subscribe(() => this.router.navigate(['/clients']));
    } else {
      this.clientService
        .createClient(this.client)
        .subscribe(() => this.router.navigate(['/clients']));
    }
  }

  cancel(): void {
    this.router.navigate(['/clients']);
  }
}
