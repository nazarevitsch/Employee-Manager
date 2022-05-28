package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.TaskDTO;
import com.bida.employer.manager.domain.dto.TaskDTOResponse;
import com.bida.employer.manager.domain.dto.TaskUpdateDTO;
import com.bida.employer.manager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<TaskDTOResponse> createTask(@Valid TaskDTO taskDTO) {
        return new ResponseEntity<>(taskService.createTask(taskDTO), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<TaskDTOResponse> createTask(@Valid TaskUpdateDTO taskDTO) {
        return new ResponseEntity<>(taskService.updateTask(taskDTO), HttpStatus.CREATED);
    }
}
