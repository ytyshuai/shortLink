package com.campus.eventplatform.controller;

import com.campus.eventplatform.common.Result;
import com.campus.eventplatform.dto.EventReq;
import com.campus.eventplatform.entity.Event;
import com.campus.eventplatform.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public Result<Event> createEvent(@Valid @RequestBody EventReq req, Authentication authentication) {
        try {
            Event event = eventService.createEvent(req, authentication.getName());
            return Result.success(event);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody EventReq req, Authentication authentication) {
        try {
            Event event = eventService.updateEvent(id, req, authentication.getName());
            return Result.success(event);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteEvent(@PathVariable Long id, Authentication authentication) {
        try {
            eventService.deleteEvent(id, authentication.getName());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping
    public Result<List<Event>> getEvents() {
        return Result.success(eventService.getEvents());
    }

    @GetMapping("/{id}")
    public Result<Event> getEventById(@PathVariable Long id) {
        try {
            return Result.success(eventService.getEventById(id));
        } catch (Exception e) {
            return Result.error(404, e.getMessage());
        }
    }
}
