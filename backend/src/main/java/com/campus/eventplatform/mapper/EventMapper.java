package com.campus.eventplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.eventplatform.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EventMapper extends BaseMapper<Event> {
}
