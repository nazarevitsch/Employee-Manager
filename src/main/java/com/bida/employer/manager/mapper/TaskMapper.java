package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.Task;
import com.bida.employer.manager.domain.dto.TaskDTO;
import com.bida.employer.manager.domain.dto.TaskDTOResponse;
import com.bida.employer.manager.domain.dto.TaskUpdateDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    private final ModelMapper modelMapper;

    public TaskMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Task dtoToEntity(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    public Task dtoToEntity(TaskUpdateDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    public TaskDTOResponse entityToDto(Task task) {
        return modelMapper.map(task, TaskDTOResponse.class);
    }

    public List<TaskDTOResponse> entityToDto(List<Task> task) {
        return task.stream().map(this::entityToDto).collect(Collectors.toList());
    }
}
