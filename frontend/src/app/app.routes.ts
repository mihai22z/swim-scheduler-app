import { Routes } from '@angular/router';
import { ClientListComponent } from './components/client-list/client-list.component';
import { ClientFormComponent } from './components/client-form/client-form.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { WorkdayDetailComponent } from './components/workday-detail/workday-detail.component';

export const routes: Routes = [
  { path: 'clients', component: ClientListComponent },
  { path: 'clients/new', component: ClientFormComponent },
  { path: 'clients/:id', component: ClientFormComponent },
  { path: 'calendar', component: CalendarComponent },
  { path: 'workday/:id', component: WorkdayDetailComponent },
  { path: '', redirectTo: '/clients', pathMatch: 'full' },
];
