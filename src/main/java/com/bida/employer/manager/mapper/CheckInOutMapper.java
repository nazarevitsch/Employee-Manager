package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.CheckInOut;
import com.bida.employer.manager.domain.dto.CheckInOutDTO;
import com.bida.employer.manager.domain.dto.CheckInOutDTOResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public CheckInOutDTOResponse entityToDto(CheckInOut checkInOut) {
        return modelMapper.map(checkInOut, CheckInOutDTOResponse.class);
    }

    public List<CheckInOutDTOResponse> entityToDto(List<CheckInOut> checkInOutList) {
        return checkInOutList.stream().map(this::entityToDto).collect(Collectors.toList());
    }
}
