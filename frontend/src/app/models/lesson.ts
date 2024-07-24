import { Client } from './client';

export interface Lesson {
  id: number | null;
  startTime: string;
  endTime: string;
  workdayId: number;
  clients: Client[];
}
