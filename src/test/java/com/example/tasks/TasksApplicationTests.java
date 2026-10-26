package com.example.tasks;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import static org.hamcrest.Matchers.hasItem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;



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
	void getTasks() throws Exception{
		mockMvc.perform(get("/tasks"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(4))
				.andExpect(jsonPath("$[*].id", hasItem(1)));
	}
	@Test
	@Order(3)
	void getTaskSuccess() throws Exception{
		mockMvc.perform(get("/tasks/3"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.ALL))
				.andExpect(jsonPath("id").value(3L))
				.andExpect(jsonPath("title").value("task_3"));
	}
	@Test
	@Order(4)
	void getTaskFail() throws Exception{
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
		//not title
		mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":7,"done":false}
								"""))
				.andExpect(status().is(400));
		//title is blank
		mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"id":7,"title":"","done":false}
								"""))
				.andExpect(status().is(400));

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

}
