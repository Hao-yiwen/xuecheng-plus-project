package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class TeachplanDto extends Teachplan {
//    与媒体资源关联信息
    private TeachplanMedia teachplanMedia;
//    小章节
    private List<TeachplanDto> teachPlanTreeNodes;

}
