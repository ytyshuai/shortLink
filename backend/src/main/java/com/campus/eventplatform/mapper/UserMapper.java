package com.campus.eventplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.eventplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}