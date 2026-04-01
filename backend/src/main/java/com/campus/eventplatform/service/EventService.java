package com.campus.eventplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.eventplatform.entity.Event;
import com.campus.eventplatform.dto.EventReq;
import java.util.List;

public interface EventService extends IService<Event> {
    Event createEvent(EventReq req, String creatorUsername);
    Event updateEvent(Long id, EventReq req, String username);
    void deleteEvent(Long id, String username);
    List<Event> getEvents();
    Event getEventById(Long id);
}
