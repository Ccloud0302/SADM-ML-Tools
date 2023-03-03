package com.demodeller.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WordRel {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    private String name;
    private String subRelOf;
    private String domains;
    private String ranges;
    private int contextId;
}
