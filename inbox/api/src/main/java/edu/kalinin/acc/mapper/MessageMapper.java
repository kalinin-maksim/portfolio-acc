package edu.kalinin.acc.mapper;

import org.mapstruct.Mapper;
import edu.kalinin.acc.dto.MessageDto;
import edu.kalinin.acc.entity.Message;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    List<MessageDto> toDto(List<Message> messages);
}
