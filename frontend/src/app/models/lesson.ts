import { Client } from './client';
import { ClientLesson } from './client-lesson';
import { Workday } from './workday';

export interface Lesson {
  id: number | null;
  startTime: string;
  endTime: string;
  workday: Workday | null;
  clientLessons: ClientLesson[];
  clients: Client[];
}
