package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtoList = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtoList;
    }

    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count + 1;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
//        通过课程计划的id来判断是新增还是修改
        Long id = saveTeachplanDto.getId();
        if (id == null) {
//            新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
//            确定排序字段 select count(1) from teachplan where course_id=117 and parentid=0;
            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            int count = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(count);
            teachplanMapper.insert(teachplan);
        } else {
//            修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long courseId) {
        Teachplan teachplan = teachplanMapper.selectById(courseId);
        if (teachplan.getGrade() == 2) {
            int delete = teachplanMapper.deleteById(courseId);
            if (delete <= 0) {
                throw new RuntimeException("课程计划删除失败");
            }
            TeachplanMedia teachplanMedia = new TeachplanMedia();
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getCourseId, courseId);
            int delete1 = teachplanMediaMapper.deleteById(queryWrapper);
            if (delete <= 0) {
                throw new RuntimeException("课程媒介删除失败");
            }
        } else {
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper = queryWrapper.eq(Teachplan::getParentid, courseId);
            Integer count = teachplanMapper.selectCount(queryWrapper);
            if (count <= 0) {
                int delete = teachplanMapper.deleteById(courseId);
                if (delete <= 0) {
                    throw new RuntimeException("课程计划删除失败");
                }
            } else {
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            }
        }

    }
}
