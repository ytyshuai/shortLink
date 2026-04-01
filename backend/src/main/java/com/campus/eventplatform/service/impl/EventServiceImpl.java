package com.campus.eventplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.eventplatform.entity.Event;
import com.campus.eventplatform.entity.User;
import com.campus.eventplatform.mapper.EventMapper;
import com.campus.eventplatform.service.EventService;
import com.campus.eventplatform.service.UserService;
import com.campus.eventplatform.dto.EventReq;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

    @Autowired
    private UserService userService;

    @Override
    public Event createEvent(EventReq req, String creatorUsername) {
        User user = userService.findByUsername(creatorUsername);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Event event = new Event();
        BeanUtils.copyProperties(req, event);
        event.setCreatorId(user.getId());
        this.save(event);
        return event;
    }

    @Override
    public Event updateEvent(Long id, EventReq req, String username) {
        Event event = this.getById(id);
        if (event == null) {
            throw new RuntimeException("活动不存在");
        }
        User user = userService.findByUsername(username);
        if (!event.getCreatorId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权修改该活动");
        }
        BeanUtils.copyProperties(req, event, "id", "creatorId", "createTime", "updateTime", "deleted");
        this.updateById(event);
        return event;
    }

    @Override
    public void deleteEvent(Long id, String username) {
        Event event = this.getById(id);
        if (event == null) {
            throw new RuntimeException("活动不存在");
        }
        User user = userService.findByUsername(username);
        if (!event.getCreatorId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权删除该活动");
        }
        this.removeById(id);
    }

    @Override
    public List<Event> getEvents() {
        return this.list(new LambdaQueryWrapper<Event>().orderByDesc(Event::getCreateTime));
    }

    @Override
    public Event getEventById(Long id) {
        Event event = this.getById(id);
        if (event == null) {
            throw new RuntimeException("活动不存在");
        }
        return event;
    }
}
