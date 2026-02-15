package ru.webbyskytracker.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.webbyskytracker.usersservice.dto.request.NewTaskRequest;
import ru.webbyskytracker.usersservice.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/new-task")
    public ResponseEntity<?> newTask(
            @RequestBody NewTaskRequest task,
            @RequestHeader("Authorization") String authorizationHeader
    ){
        System.out.println(task.getTime());
        return taskService.createNewTask(task, authorizationHeader);
    }

    @GetMapping("/get-user-tasks")
    public ResponseEntity<?> getUserTasks(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return taskService.getUserTasks(authorizationHeader);
    }

    @GetMapping("/get-user-tasks-by-date")
    public ResponseEntity<?> getUserTasksByDate(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String date
    ){
        return taskService.getUserTasksByDate(authorizationHeader, date);
    }

    @GetMapping("/get-task-details/{taskId}")
    public ResponseEntity<?> getTaskDetails(
            @PathVariable Long taskId
    ){
        return taskService.getTaskById(taskId);
    }

    @PostMapping("/delete-task/{taskId}")
    public ResponseEntity<?> deleteTaskById(
            @PathVariable Long taskId
    ){
        return taskService.deleteTaskById(taskId);
    }

    @GetMapping("/count-user-tasks-by-date")
    public ResponseEntity<?> countUserTasksByDate(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String date
    ) {
        return taskService.countUserTasksByDate(authorizationHeader, date);
    }
}
