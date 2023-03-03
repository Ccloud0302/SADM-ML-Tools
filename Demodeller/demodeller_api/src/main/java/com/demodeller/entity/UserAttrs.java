package com.demodeller.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserAttrs {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    private List<Map<String, Object>> attrList;
    private int contextId;
    private int nodeId;
}
