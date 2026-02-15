package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.usersservice.dto.request.NewTaskRequest;
import ru.webbyskytracker.usersservice.dto.response.ApiResponse;
import ru.webbyskytracker.usersservice.entity.Task;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.repository.TaskRepository;
import ru.webbyskytracker.usersservice.security.jwt.JwtService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final JwtService jwtService;

    public ResponseEntity<?> createNewTask(NewTaskRequest task, String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String email = jwtService.getEmailFromToken(token);
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Task newTask = Task.builder()
                    .description(task.getDescription())
                    .time(task.getTime())
                    .user(user)
                    .build();
            taskRepository.save(newTask);
            return new ResponseEntity<>(new ApiResponse(true, "Task added"), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(false, "Missing token"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> getUserTasks(String authorization) {
        String token = authorization.substring(7);
        String email = jwtService.getEmailFromToken(token);
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Task> tasks = taskRepository.findByUser(user);
            List<NewTaskRequest> responseTasks = tasks.stream()
                    .map(NewTaskRequest::new)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(responseTasks, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getUserTasksByDate(String authorization, String date) {
        String token = authorization.substring(7);
        String email = jwtService.getEmailFromToken(token);
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            LocalDate parsedDate = LocalDate.parse(date);
            List<Task> tasks = taskRepository.findByUserAndDate(user, parsedDate);
            List<NewTaskRequest> responseTasks = tasks.stream()
                    .map(NewTaskRequest::new)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responseTasks, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getTaskById(Long taskId){
        Task task = taskRepository.getTaskById(taskId);
        if(task == null){
            return new ResponseEntity<>(new ApiResponse(false, "Task not found"), HttpStatus.BAD_REQUEST);
        }
        NewTaskRequest taskDto = new NewTaskRequest(
                task.getId(),
                task.getDescription(),
                task.getTime()
        );
        return new ResponseEntity<>(taskDto, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteTaskById(Long taskId){
        taskRepository.deleteById(taskId);
        return new ResponseEntity<>(new ApiResponse(true, "Task deleted"), HttpStatus.OK);
    }

    public ResponseEntity<?> countUserTasksByDate(String authorization, String date) {
        String token = authorization.substring(7);
        String email = jwtService.getEmailFromToken(token);
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            LocalDate parsedDate = LocalDate.parse(date);
            Long taskCount = taskRepository.countTasksByUserAndDate(user, parsedDate);

            return new ResponseEntity<>(Collections.singletonMap("count", taskCount), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "User not found"), HttpStatus.BAD_REQUEST);
        }
    }

}
