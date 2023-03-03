package com.demodeller.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserAttr {
    private String id;
    private String attrLabel;
    private String attrValue;
    private int contextId;
    private int nodeId;
}
