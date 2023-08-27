package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
//        调用mapper
        List<CourseCategoryTreeDto> list = courseCategoryMapper.selectTreeNodes(id);
//        封装成list
        Map<String, CourseCategoryTreeDto> mapTemp = list.stream().filter(item -> !id.equals(item.getId())).collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
//        定义list作为最终list
        ArrayList<CourseCategoryTreeDto> courseCategoryTreeDtoArrayList = new ArrayList<>();
        list.stream().filter(item -> !id.equals(item.getId())).forEach(item->{
            if(item.getParentid().equals(id)) {
                courseCategoryTreeDtoArrayList.add(item);
            }
//            找到每个子节点放在父节点的childrenTnreeodes属性中
            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
            if(courseCategoryTreeDto !=null){
                if(courseCategoryTreeDto.getChildrenTreeNodes()==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryTreeDtoArrayList;
    }
}
