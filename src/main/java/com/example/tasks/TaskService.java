package com.example.tasks;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing business logic and in-memory persistence for tasks.
 */
@Service
public class TaskService {
    private final Map<Long, Task> tasksMap = new ConcurrentHashMap<>();

    /**
     * Initializes the service with default seed tasks.
     */
    public TaskService() {
        initDefaultTasks();
    }

    /**
     * Helper to populate or reset default tasks.
     */
    public void initDefaultTasks() {
        tasksMap.clear();
        tasksMap.put(1L, new Task(1, "task_1", false));
        tasksMap.put(2L, new Task(2, "task_2", false));
        tasksMap.put(3L, new Task(3, "task_3", false));
    }

    /**
     * Retrieves all tasks, optionally filtered by their completion status.
     * Edge cases:
     * - done is null: returns all existing tasks.
     * - done is true/false: returns only tasks matching that status.
     * - no tasks match: returns an empty list (caller can return 204 No Content).
     *
     * @param done optional boolean filter for task completion status
     * @return list of matching tasks
     */
    public List<Task> getTasks(Boolean done) {
        if (done == null) {
            return new ArrayList<>(tasksMap.values());
        }
        List<Task> filtered = new ArrayList<>();
        for (Task task : tasksMap.values()) {
            if (task.done() == done) {
                filtered.add(task);
            }
        }
        return filtered;
    }

    /**
     * Retrieves a task by its unique identifier.
     * Edge cases:
     * - id not found: throws TaskNotFoundException (mapped to 404 Not Found).
     *
     * @param id the unique ID of the task
     * @return the found task
     */
    public Task getTaskById(long id) {
        Task task = tasksMap.get(id);
        if (task == null) {
            throw new TaskController.TaskNotFoundException();
        }
        return task;
    }

    /**
     * Creates and stores a new task.
     * Edge cases:
     * - title is null or blank (empty or whitespace-only): throws TaskCreationFailed (400 Bad Request).
     *
     * @param request the task creation request data
     * @return the newly created task
     */
    public Task createTask(TaskRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new TaskController.TaskCreationFailed();
        }
        Task newTask = new Task(request.getId(), request.getTitle(), request.getStatus());
        tasksMap.put(newTask.id(), newTask);
        return newTask;
    }

    /**
     * Updates an existing task's title and/or done status.
     * Edge cases:
     * - id does not exist: throws TaskNotFoundException (404 Not Found).
     * - title is null or blank (whitespace-only): throws TaskCreationFailed (400 Bad Request).
     * - request body contains a different id: ignored; task ID is immutable and preserved from the path.
     *
     * @param id the unique ID of the task to update
     * @param request the update data containing title and done status
     * @return the updated task
     */
    public Task updateTask(long id, TaskRequest request) {
        if (!tasksMap.containsKey(id)) {
            throw new TaskController.TaskNotFoundException();
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new TaskController.TaskCreationFailed();
        }
        Task updatedTask = new Task(id, request.getTitle(), request.getStatus());
        tasksMap.put(id, updatedTask);
        return updatedTask;
    }

    /**
     * Deletes a task by its unique identifier.
     * Edge cases:
     * - id does not exist: throws TaskNotFoundException (404 Not Found).
     *
     * @param id the unique ID of the task to delete
     */
    public void deleteTask(long id) {
        if (!tasksMap.containsKey(id)) {
            throw new TaskController.TaskNotFoundException();
        }
        tasksMap.remove(id);
    }
}
