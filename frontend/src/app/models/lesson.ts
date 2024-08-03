import { Client } from './client';
import { ClientLesson } from './client-lesson';

export interface Lesson {
  id: number | null;
  startTime: string;
  endTime: string;
  workdayId: number;
  clientLessons: ClientLesson[];
  clients: Client[];
}
