import { Client } from './client';
import { Lesson } from './lesson';

export interface ClientLesson {
  id: {
    clientId: number;
    lessonId: number;
  };
  client: Client;
  lesson: Lesson;
}
