package com.example.tasks;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller handling HTTP endpoints for task management.
 * Business logic and persistence are delegated to TaskService.
 */
@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Retrieves tasks, optionally filtered by completion status (?done=true|false).
     * Edge cases:
     * - Parameter omitted: returns all existing tasks (200 OK).
     * - No tasks match the query: returns 204 No Content to notify that no relevant tasks exist.
     * - Matching tasks found: returns 200 OK with list.
     * - Parameter malformed (e.g. ?done=xyz): handled by Spring MVC returning 400 Bad Request.
     *
     * @param done optional boolean filter
     * @return 200 OK with list of tasks or 204 No Content if empty
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> tasks(@RequestParam(required = false) Boolean done) {
        List<Task> tasksList = taskService.getTasks(done);
        if (tasksList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasksList);
    }

    /**
     * Retrieves a task by its ID.
     * Edge cases:
     * - Task ID not found: throws TaskNotFoundException (404 Not Found).
     *
     * @param id the task ID
     * @return the task details
     */
    @GetMapping("/tasks/{id}")
    public Task tasks(@PathVariable long id) {
        return taskService.getTaskById(id);
    }

    /**
     * Creates a new task.
     * Edge cases:
     * - Title is missing or blank: throws TaskCreationFailed (400 Bad Request).
     *
     * @param taskRequest task payload
     */
    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@RequestBody TaskRequest taskRequest) {
        taskService.createTask(taskRequest);
    }

    /**
     * Updates an existing task's title or completion status.
     * Edge cases:
     * - Task ID does not exist: throws TaskNotFoundException (404 Not Found).
     * - Title is blank, empty, or whitespace-only: throws TaskCreationFailed (400 Bad Request).
     * - Task ID in request body: ignored; the path variable ID is immutable.
     *
     * @param id the ID of the task to update
     * @param taskRequest the update payload
     * @return the updated task (200 OK)
     */
    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable long id, @RequestBody TaskRequest taskRequest) {
        return taskService.updateTask(id, taskRequest);
    }

    /**
     * Deletes a task by ID.
     * Edge cases:
     * - Task ID not found: throws TaskNotFoundException (404 Not Found).
     *
     * @param id the ID of the task to delete
     */
    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Task")
    public static class TaskNotFoundException extends RuntimeException {}

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "title is blank")
    public static class TaskCreationFailed extends RuntimeException {}
}
