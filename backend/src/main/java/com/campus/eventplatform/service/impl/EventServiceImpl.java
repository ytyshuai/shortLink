package com.campus.eventplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.eventplatform.entity.Event;
import com.campus.eventplatform.mapper.EventMapper;
import com.campus.eventplatform.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {
}
