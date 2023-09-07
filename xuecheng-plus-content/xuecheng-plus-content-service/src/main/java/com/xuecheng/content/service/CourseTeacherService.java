package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    public List<CourseTeacher> getCourseTeacher(Long courseId);

    public CourseTeacher updateCourseTeacher(CourseTeacher courseTeacher);

    public void deleteCourseTeacher(Long courseId, Long teacherId);
}
