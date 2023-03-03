package com.demodeller.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.sql.Timestamp;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Event {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    private String name;
    private String type;
    private String message;
    private int contextId;
    private int nodeId;
    private String causeCommand;
    private String causeCommandId;
    private Timestamp dateTime;
}
