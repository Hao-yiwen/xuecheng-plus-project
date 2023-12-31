package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.dto.enums.MoveType;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Transactional
    @Override
    public void deleteTeachplan(Long courseId) {
        Teachplan teachplan = teachplanMapper.selectById(courseId);
        if (teachplan.getGrade() == 2) {
            int delete = teachplanMapper.deleteById(courseId);
            if (delete <= 0) {
                throw new RuntimeException("课程计划删除失败");
            }
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getCourseId, courseId);
            List<TeachplanMedia> teachplanMediaList = teachplanMediaMapper.selectList(queryWrapper);
            if (teachplanMediaList.size() > 0) {
                int delete1 = teachplanMediaMapper.deleteBatchIds(teachplanMediaList);
                if (delete1 <= 0) {
                    throw new RuntimeException("课程媒介删除失败");
                }
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

    @Transactional
    @Override
    public void moveupTeachplan(String movetype, Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            return;  // Or handle it differently
        }
        int currentOrder = teachplan.getOrderby();
        int newOrder;
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid())
                .eq(Teachplan::getCourseId, teachplan.getCourseId());

        if (MoveType.MOVEUP.getMoveType().equals(movetype) && currentOrder != 1) {
            newOrder = currentOrder - 1;
            queryWrapper.eq(Teachplan::getOrderby, newOrder);
        } else if (currentOrder != teachplanMapper.selectCount(queryWrapper)) {
            newOrder = currentOrder + 1;
            queryWrapper.eq(Teachplan::getOrderby, newOrder);
        } else {
            return;  // Or handle it differently
        }

        Teachplan adjacentTeachplan = teachplanMapper.selectOne(queryWrapper);

        if (adjacentTeachplan != null) {
            // Swap the 'orderby' values of the two Teachplan objects
            teachplan.setOrderby(newOrder);
            adjacentTeachplan.setOrderby(currentOrder);

            // Perform the updates
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(adjacentTeachplan);
        }
    }

    @Transactional
    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二季教学计划绑定");
        }

        // 删除原有记录，根据课程计划id，删除绑定媒资
        LambdaQueryWrapper<TeachplanMedia> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId());
        int delete = teachplanMediaMapper.delete(lambdaQueryWrapper);

        // 再添加原有记录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto,teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMediaMapper.insert(teachplanMedia);
    }
}
