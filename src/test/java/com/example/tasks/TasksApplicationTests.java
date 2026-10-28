package com.example.tasks;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TasksApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	@Order(2)
	void getTasks() throws Exception {
		mockMvc.perform(get("/tasks"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(4))
				.andExpect(jsonPath("$[*].id", hasItem(1)));
	}

	@Test
	@Order(3)
	void getTaskSuccess() throws Exception {
		mockMvc.perform(get("/tasks/3"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.ALL))
				.andExpect(jsonPath("id").value(3L))
				.andExpect(jsonPath("title").value("task_3"));
	}

	@Test
	@Order(4)
	void getTaskFail() throws Exception {
		mockMvc.perform(get("/tasks/999"))
				.andExpect(status().is(404));
	}

	@Test
	@Order(1)
	void createTaskSuccess() throws Exception {
		mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":5,"title":"task_5","done":false}
								"""))
				.andExpect(status().is(201));
	}

	@Test
	@Order(5)
	void createTaskFailure() throws Exception {
		// missing title
		mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":7,"done":false}
								"""))
				.andExpect(status().is(400));
		// empty title
		mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":7,"title":"","done":false}
								"""))
				.andExpect(status().is(400));
		// whitespace title
		mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":7,"title":" ","done":false}
								"""))
				.andExpect(status().is(400));
	}

	@Test
	@Order(6)
	void DeleteTaskSuccess() throws Exception {
		mockMvc.perform(delete("/tasks/3"))
				.andExpect(status().is(204));
	}

	/**
	 * Tests GET /tasks?done=true when no tasks match the filter.
	 * Edge cases:
	 * - Returns 204 No Content to notify that no relevant tasks match.
	 */
	@Test
	@Order(7)
	void getTasksFilterDoneTrueNoMatch() throws Exception {
		mockMvc.perform(get("/tasks").param("done", "true"))
				.andExpect(status().isNoContent());
	}

	/**
	 * Tests GET /tasks?done=false when all existing tasks are pending.
	 * Edge cases:
	 * - Returns 200 OK with matching task array where all items have done = false.
	 */
	@Test
	@Order(8)
	void getTasksFilterDoneFalse() throws Exception {
		mockMvc.perform(get("/tasks").param("done", "false"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[*].done", everyItem(is(false))));
	}

	/**
	 * Tests PUT /tasks/{id} updating an existing task's title and done status.
	 * Edge cases:
	 * - Returns 200 OK with the updated Task payload.
	 */
	@Test
	@Order(9)
	void updateTaskSuccess() throws Exception {
		mockMvc.perform(put("/tasks/1").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"updated_task_1","done":true}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("updated_task_1"))
				.andExpect(jsonPath("$.done").value(true));
	}

	/**
	 * Tests GET /tasks?done=true after a task has been updated to done = true.
	 * Edge cases:
	 * - Returns 200 OK with only the completed tasks.
	 */
	@Test
	@Order(10)
	void getTasksFilterDoneTrueAfterUpdate() throws Exception {
		mockMvc.perform(get("/tasks").param("done", "true"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].done").value(true));
	}

	/**
	 * Tests PUT /tasks/{id} ensuring the task ID cannot be updated even if specified in request body.
	 * Edge cases:
	 * - Request body specifies id: 9999, but path variable is 1.
	 * - ID remains 1 and no task with id: 9999 is created.
	 */
	@Test
	@Order(11)
	void updateTaskIdCannotBeChanged() throws Exception {
		mockMvc.perform(put("/tasks/1").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":9999,"title":"id_should_not_change","done":true}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("id_should_not_change"))
				.andExpect(jsonPath("$.done").value(true));

		mockMvc.perform(get("/tasks/9999"))
				.andExpect(status().isNotFound());
	}

	/**
	 * Tests PUT /tasks/{id} when the task ID does not exist.
	 * Edge cases:
	 * - Returns 404 Not Found.
	 */
	@Test
	@Order(12)
	void updateTaskNotFound() throws Exception {
		mockMvc.perform(put("/tasks/999").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"non_existent","done":true}
								"""))
				.andExpect(status().isNotFound());
	}

	/**
	 * Tests PUT /tasks/{id} with missing, empty, or whitespace-only title.
	 * Edge cases:
	 * - All return 400 Bad Request.
	 */
	@Test
	@Order(13)
	void updateTaskValidationFailures() throws Exception {
		// Missing title
		mockMvc.perform(put("/tasks/1").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"done":true}
								"""))
				.andExpect(status().isBadRequest());

		// Empty title
		mockMvc.perform(put("/tasks/1").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"","done":true}
								"""))
				.andExpect(status().isBadRequest());

		// Whitespace title
		mockMvc.perform(put("/tasks/1").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"   ","done":true}
								"""))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Tests GET /tasks with an invalid boolean filter value.
	 * Edge cases:
	 * - Spring MVC type conversion failure results in 400 Bad Request.
	 */
	@Test
	@Order(14)
	void getTasksFilterInvalidParameter() throws Exception {
		mockMvc.perform(get("/tasks").param("done", "not_a_boolean"))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Tests DELETE /tasks/{id} when the task ID does not exist.
	 * Edge cases:
	 * - Returns 404 Not Found.
	 */
	@Test
	@Order(15)
	void deleteTaskNotFound() throws Exception {
		mockMvc.perform(delete("/tasks/999"))
				.andExpect(status().isNotFound());
	}
}
