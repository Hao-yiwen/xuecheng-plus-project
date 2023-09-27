package com.xuecheng.learning.service;

import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;

/**
 * 选课相关接口
 */
public interface MyCourseTableService {
    /**
     * 添加课程
     * @param userId
     * @param courseId
     * @return
     */
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * 判断学习资格
     * @param userId
     * @param courseId
     * @return
     */
    public XcCourseTablesDto getLearningStatus(String userId,Long courseId);

    public XcCourseTables addCourseTables(XcChooseCourse xcChooseCourse);

    public XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish);

    public XcChooseCourse addChargeCourse(String userId, CoursePublish coursePublish);
}
