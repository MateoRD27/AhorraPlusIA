package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.NotificationDTO;
import com.unimag.ahorroplusia.entity.entities.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {
    NotificationDTO notificationDTOToNotificationDTO(Notification notification);
    Notification notificationDTOToNotification(NotificationDTO notificationDTO);

}
