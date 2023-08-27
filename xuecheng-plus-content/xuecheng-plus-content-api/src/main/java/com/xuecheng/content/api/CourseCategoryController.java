package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yw.hao
 * @version 1.0.0
 * @description 课程分类相关接口
 */
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;

   @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
       return courseCategoryService.queryTreeNodes("1");
   }
}
