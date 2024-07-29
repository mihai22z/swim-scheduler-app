package com.mike.swim_scheduler_app;

import com.mike.swim_scheduler_app.model.*;
import com.mike.swim_scheduler_app.repository.ClientLessonRepository;
import com.mike.swim_scheduler_app.repository.ClientRepository;
import com.mike.swim_scheduler_app.repository.LessonRepository;
import com.mike.swim_scheduler_app.repository.WorkdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

@SpringBootApplication
public class SwimSchedulerAppApplication implements CommandLineRunner {

	@Autowired
	WorkdayRepository workdayRepository;

	@Autowired
	LessonRepository lessonRepository;

	@Autowired
	ClientRepository clientRepository;

	@Autowired
	ClientLessonRepository clientLessonRepository;

	public static void main(String[] args) {
		SpringApplication.run(SwimSchedulerAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (clientRepository.count() == 0 && lessonRepository.count() == 0 && workdayRepository.count() == 0) {
			Client client1 = new Client();
			client1.setName("Mike Doe");
			client1.setPhone("1234");

			Client client2 = new Client();
			client2.setName("Mikaela Dudette");
			client2.setPhone("5678");

			Client client3 = new Client();
			client3.setName("Mikus Smithus");
			client3.setPhone("8989");

			client1 = clientRepository.save(client1);
			client2 = clientRepository.save(client2);
			client3 = clientRepository.save(client3);

			Workday workday1 = new Workday();
			workday1.setDate(LocalDate.of(2024, 7, 15));
			workday1.setStartTime(LocalDateTime.of(2024, 7, 15, 9, 0));
			workday1.setEndTime(LocalDateTime.of(2024, 7, 15, 15, 0));
			workdayRepository.save(workday1);

			Lesson lesson1 = new Lesson();
			lesson1.setStartTime(LocalDateTime.of(2024, 7, 15, 9, 0));
			lesson1.setEndTime(LocalDateTime.of(2024, 7, 15, 10, 0));
			lesson1.setWorkday(workday1);

			Lesson lesson2 = new Lesson();
			lesson2.setStartTime(LocalDateTime.of(2024, 7, 15, 10, 30));
			lesson2.setEndTime(LocalDateTime.of(2024, 7, 15, 11, 30));
			lesson2.setWorkday(workday1);

			Lesson lesson3 = new Lesson();
			lesson3.setStartTime(LocalDateTime.of(2024, 7, 15, 14, 0));
			lesson3.setEndTime(LocalDateTime.of(2024, 7, 15, 15, 0));
			lesson3.setWorkday(workday1);

			lesson1 = lessonRepository.save(lesson1);
			lesson2 = lessonRepository.save(lesson2);
			lesson3 = lessonRepository.save(lesson3);

			workday1.setLessons(Set.of(lesson1, lesson2, lesson3));
			workdayRepository.save(workday1);

			ClientLesson cl1 = new ClientLesson(new ClientLessonId(client1.getId(), lesson1.getId()), client1, lesson1);
			ClientLesson cl2 = new ClientLesson(new ClientLessonId(client2.getId(), lesson1.getId()), client2, lesson1);
			ClientLesson cl3 = new ClientLesson(new ClientLessonId(client1.getId(), lesson2.getId()), client1, lesson2);
			ClientLesson cl4 = new ClientLesson(new ClientLessonId(client2.getId(), lesson3.getId()), client2, lesson3);

			clientLessonRepository.saveAll(Arrays.asList(cl1, cl2, cl3, cl4));
		}
	}

}
