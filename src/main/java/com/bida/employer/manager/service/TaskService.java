package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.Shift;
import com.bida.employer.manager.domain.Task;
import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.TaskDTO;
import com.bida.employer.manager.domain.dto.TaskDTOResponse;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.mapper.TaskMapper;
import com.bida.employer.manager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ShiftService shiftService;

    public TaskDTOResponse createTask(TaskDTO taskDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Shift shift = shiftService.findById(taskDTO.getShiftId());
        if (!shift.getOrganizationId().equals(currentUser.getOrganizationId())) {
            throw new BadRequestException("Shift with id: " + shift.getId() + " is from another organization!");
        }
        if (taskDTO.getTaskTime() != null && taskDTO.getTaskTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("You can't set time for task before now!");
        }
        if (shift.getShiftStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("You can't set time to old shift!");
        }
        Task createdTask = taskRepository.save(taskMapper.dtoToEntity(taskDTO));
        return taskMapper.entityToDto(createdTask);
    }
}
