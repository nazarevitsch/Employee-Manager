package com.bida.employer.manager.mapper;

import com.bida.employer.manager.domain.Rule;
import com.bida.employer.manager.domain.dto.RuleDTO;
import com.bida.employer.manager.domain.dto.RuleDTOResponse;
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

    public RuleDTOResponse entityToDto(Rule rule) {
        return modelMapper.map(rule, RuleDTOResponse.class);
    }
}
