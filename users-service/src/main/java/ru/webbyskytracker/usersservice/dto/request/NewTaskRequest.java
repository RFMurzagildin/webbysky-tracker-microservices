package ru.webbyskytracker.usersservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.webbyskytracker.usersservice.entity.Task;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewTaskRequest {
    private Long id;
    private String description;
    private LocalDateTime time;

    public NewTaskRequest(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        this.time = task.getTime();
    }
}
