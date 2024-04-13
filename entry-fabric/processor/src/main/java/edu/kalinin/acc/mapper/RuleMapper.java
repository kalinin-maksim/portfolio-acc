package edu.kalinin.acc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import edu.kalinin.acc.data.Input;
import edu.kalinin.acc.dto.BaseDTO;
import edu.kalinin.acc.dto.PaymentDTO;
import edu.kalinin.acc.helper.ParseHelper;

@Mapper(componentModel = "spring",
        uses = {
                ParseHelper.class
        },
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface RuleMapper {
    Input toInput(BaseDTO header, PaymentDTO body);
    Input toInput(edu.kalinin.acc.dto.external.input.BaseDTO header, edu.kalinin.acc.dto.external.input.LoansDTO body);
}