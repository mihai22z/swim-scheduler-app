import { ClientLesson } from './client-lesson';

export interface Client {
  id: number | null;
  name: string;
  phone: string;
  notes: string;
  clientLessons: ClientLesson[];
  subscriptionTotalLessons?: number;
  remainingLessons?: number;
  subscriptionStartDate?: string;
}
