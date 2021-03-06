package com.example.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.LambdaUpdateChainWrapper;
import com.example.hotel.entity.User;
import com.example.hotel.mapper.UserMapper;
import com.example.hotel.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class UserService{

  @Autowired
  private   UserMapper userMapper;
  @Autowired
  private SnowflakeIdWorker sequence;


    public Result list(PageInfo info) {
        Page<User> page = new Page<>(info.getPagenum(),info.getPagesize());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(info.getQuery())){
            wrapper.like("admin_nickname",info.getQuery());
        }
        IPage<User> userIPage = userMapper.selectPage(page,wrapper);
        Result result = new Result();
        result.getData().add(userIPage);
        return  result;

    }


    public User login(Main main) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getAdminUsername,main.getUsername());
        return userMapper.selectOne(wrapper);
    }


    public Result save(User bean) {
        Result result = new Result();

        Long id = bean.getAdminId();
        if(id ==null) {
            //添加
             id = sequence.nextId();
             bean.setAdminId(id);
            int i = userMapper.insert(bean);
            if (i > 0) {
                result.setCode(State.OK.INFO());
                result.setMsg("添加成功");
            } else {
                result.setCode(State.FAIL.INFO());
                result.setMsg("添加失败");
            }
        }else {
            //修改
            int i = userMapper.updateById(bean);
            if (i > 0) {
                result.setCode(State.OK.INFO());
                result.setMsg("修改成功");
            } else {
                result.setCode(State.FAIL.INFO());
                result.setMsg("修改失败");
            }
        }
        result.setData(Arrays.asList(bean));

        return result;
    }

    public Result remove(Long id) {
        Result result = new Result();
        int i = userMapper.deleteById(id);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getAdminId,id);
        int no = userMapper.delete(wrapper);
        if (no>0){
            result.setCode(State.OK.INFO());
            result.setMsg("删除成功");
        }else {
            result.setCode(State.FAIL.INFO());
            result.setMsg("删除失败");
        }
        return result;

    }

    public Result load(Long id) {
        Result result = new Result();
        User user = userMapper.selectById(id);
        if(user == null){
            result.setCode(State.FAIL.INFO());
            result.setMsg("查询失败");
        }
        result.setCode(State.OK.INFO());
        result.setMsg("查询成功");
        result.setData(Arrays.asList(user));
        return result;
    }

    public Result setrole(Long id, Long roleid) {
        Result result = new Result();
        boolean update = new LambdaUpdateChainWrapper<User>(userMapper)
                .set(User::getAdminRole, roleid)
                .eq(User::getAdminId, id)
                .update();
        if(update){
            result.setCode(State.OK.INFO());
            result.setMsg("设置成功");
        }
        return result;
    }
}
