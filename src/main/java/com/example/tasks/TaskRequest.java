package com.example.tasks;

/**
 * Data Transfer Object for creating and updating tasks.
 * Edge cases:
 * - title can be null or blank, which must be validated by the service/controller layer.
 * - id is optional; when updating, the path variable id takes precedence and any id in the request is ignored.
 */
public class TaskRequest {
    private int id;
    private String title;
    private boolean done;

    public TaskRequest() {
    }

    public TaskRequest(String title, boolean done) {
        this.title = title;
        this.done = done;
    }

    public TaskRequest(int id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean getStatus() {
        return this.done;
    }

    public boolean isDone() {
        return this.done;
    }
}
