import { Lesson } from './lesson';

export interface Workday {
  id: number | null;
  date: string;
  startTime: string;
  endTime: string;
  lessons: Lesson[];
}
