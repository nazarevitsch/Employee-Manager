package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.TaskDTO;
import com.bida.employer.manager.domain.dto.TaskDTOResponse;
import com.bida.employer.manager.domain.dto.TaskUpdateDTO;
import com.bida.employer.manager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<TaskDTOResponse> createTask(@RequestBody @Valid TaskDTO taskDTO) {
        return new ResponseEntity<>(taskService.createTask(taskDTO), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<TaskDTOResponse> updateTask(@RequestBody @Valid TaskUpdateDTO taskDTO) {
        return new ResponseEntity<>(taskService.updateTask(taskDTO), HttpStatus.OK);
    }

    @GetMapping("/{shiftId}")
    public ResponseEntity<List<TaskDTOResponse>> getTaskByShiftId(@PathVariable("shiftId") UUID shiftId) {
        return new ResponseEntity<>(taskService.getTasksByShiftId(shiftId), HttpStatus.OK);
    }
}
