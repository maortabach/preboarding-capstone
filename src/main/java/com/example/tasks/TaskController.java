package com.example.tasks;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class TaskController {
    private final Map<Long,Task> tasksMap = new HashMap<>();

    public TaskController(){
        tasksMap.put(1L,new Task(1,"task_1",false));
        tasksMap.put(2L,new Task(2,"task_2",false));
        tasksMap.put(3L,new Task(3,"task_3",false));
    }

    @GetMapping("/tasks")
    public List<Task> tasks(){
        List<Task> tasksList = new ArrayList<>();
        for(Map.Entry<Long,Task> entry: tasksMap.entrySet()){
            tasksList.add(entry.getValue());
        }
        return tasksList;
    }
    @GetMapping("/tasks/{id}")
    public Task tasks(@PathVariable long id){
        Task output = tasksMap.getOrDefault(id,null);
        if(output == null) throw new TaskNotFoundException();
        return output;
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@RequestBody TaskRequest taskRequest){
        if(taskRequest.getTitle() == null || taskRequest.getTitle().isBlank()){
            throw new TaskCreationFailed();
        }
        Task newTask = new Task(taskRequest.getId(), taskRequest.getTitle(), taskRequest.getStatus());
        tasksMap.put(newTask.id(),newTask);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable long id){
        if(!tasksMap.containsKey(id)){
            throw new TaskNotFoundException();
        }
        tasksMap.remove(id);
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Task")  // 404
    public static class TaskNotFoundException extends RuntimeException {}
    @ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="title is blank")  // 204
    public static class TaskCreationFailed extends RuntimeException {}

}
