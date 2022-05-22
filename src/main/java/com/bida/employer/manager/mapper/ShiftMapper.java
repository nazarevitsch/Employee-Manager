package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.Shift;
import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.domain.dto.ShiftDTOResponse;
import com.bida.employer.manager.domain.dto.ShiftTimeDTO;
import com.bida.employer.manager.domain.dto.UpdateShiftDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ShiftMapper {

    private final ModelMapper modelMapper;

    public ShiftMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Shift dtoToEntity(UUID currentUserId, UUID organizationId, UUID userId, CreateShiftDTO createShiftDTO, ShiftTimeDTO shiftTimeDTO) {
        Shift shift = new Shift();
        shift.setTitle(createShiftDTO.getTitle());
        shift.setDescription(createShiftDTO.getDescription());
        shift.setLastModificationUser(currentUserId);
        shift.setLastModificationDate(LocalDateTime.now());
        shift.setShiftStart(shiftTimeDTO.getShiftStart());
        shift.setShiftFinish(shiftTimeDTO.getShiftFinish());
        shift.setOrganizationId(organizationId);
        shift.setUserId(userId);
        return shift;
    }

    public Shift dtoToEntity(UpdateShiftDTO updateShiftDTO) {
        return modelMapper.map(updateShiftDTO, Shift.class);
    }

    public ShiftDTOResponse entityToDto(Shift shift) {
        return modelMapper.map(shift, ShiftDTOResponse.class);
    }

    public List<ShiftDTOResponse> entityToDto(List<Shift> shift) {
        return shift.stream().map(this::entityToDto).collect(Collectors.toList());
    }
}
