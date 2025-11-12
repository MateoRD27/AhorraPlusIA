package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.SupportTicketDto;
import com.unimag.ahorroplusia.entity.entities.SupportTicket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupportTicketMapper {
    SupportTicketDto supportTicketToSupportTicketDTO(SupportTicket supportTicket);
    SupportTicket supportTicketDTOToSupportTicket(SupportTicketDto supportTicketDto);
}
