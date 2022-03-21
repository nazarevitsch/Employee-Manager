package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.Rule;
import com.bida.employer.manager.domain.dto.RuleDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class RuleMapper {

    private final ModelMapper modelMapper;

    public RuleMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Rule dtoToEntity(RuleDTO ruleDTO) {
        return modelMapper.map(ruleDTO, Rule.class);
    }
}
