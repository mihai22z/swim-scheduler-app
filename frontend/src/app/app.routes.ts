import { Routes } from '@angular/router';
import { ClientListComponent } from './components/client-list/client-list.component';
import { ClientFormComponent } from './components/client-form/client-form.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { WorkdayDetailComponent } from './components/workday-detail/workday-detail.component';
import { LessonFormComponent } from './components/lesson-form/lesson-form.component';
import { AddSubscriptionComponent } from './components/add-subscription/add-subscription.component';

export const routes: Routes = [
  { path: 'clients', component: ClientListComponent },
  { path: 'clients/new', component: ClientFormComponent },
  { path: 'clients/:id', component: ClientFormComponent },
  { path: 'calendar', component: CalendarComponent },
  { path: 'workday/:id', component: WorkdayDetailComponent },
  { path: 'add-lesson', component: LessonFormComponent },
  { path: 'reschedule-lesson/:id', component: LessonFormComponent },
  { path: 'add-subscription', component: AddSubscriptionComponent },
  { path: '', redirectTo: '/clients', pathMatch: 'full' },
];
