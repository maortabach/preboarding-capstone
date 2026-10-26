package com.example.tasks;

public class TaskRequest {
    int id;
    String title;
    boolean done;

    public TaskRequest(int id, String title, boolean done){
        this.id = id;
        this.title = title;
        this.done = done;
    }
    public int getId(){
        return this.id;
    }
    public String getTitle(){
        return this.title;
    }
    public boolean getStatus(){
        return this.done;
    }
}
