package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.UserDTOResponse;
import com.bida.employer.manager.domain.dto.UserRegistrationDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public User dtoToEntity(UserRegistrationDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public UserDTOResponse entityToDto(User user) {
        return modelMapper.map(user, UserDTOResponse.class);
    }
}
