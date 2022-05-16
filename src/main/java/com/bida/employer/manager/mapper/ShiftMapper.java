package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.Shift;
import com.bida.employer.manager.domain.dto.CreateShiftDTO;
import com.bida.employer.manager.domain.dto.ShiftDTOResponse;
import com.bida.employer.manager.domain.dto.UpdateShiftDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class ShiftMapper {

    private final ModelMapper modelMapper;

    public ShiftMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Shift dtoToEntity(CreateShiftDTO createShiftDTO) {
        return modelMapper.map(createShiftDTO, Shift.class);
    }

    public Shift dtoToEntity(UpdateShiftDTO updateShiftDTO) {
        return modelMapper.map(updateShiftDTO, Shift.class);
    }

    public ShiftDTOResponse entityToDto(Shift shift) {
        return modelMapper.map(shift, ShiftDTOResponse.class);
    }
}
