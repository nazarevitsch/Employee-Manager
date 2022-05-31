package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.CheckInOut;
import com.bida.employer.manager.domain.dto.CheckInOutDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class CheckInOutMapper {

    private final ModelMapper modelMapper;

    public CheckInOutMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public CheckInOut dtoToEntity(CheckInOutDTO checkInOutDTO) {
        return modelMapper.map(checkInOutDTO, CheckInOut.class);
    }
}
