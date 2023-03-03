package com.demodeller.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demodeller.base.util.DateUtil;
import com.demodeller.base.util.StringUtil;
import com.demodeller.entity.*;
import com.demodeller.service.IBoundedContextService;
import com.demodeller.common.R;

import com.demodeller.service.IEntityElementsService;
import com.demodeller.service.IKGGraphService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
public class EntityElementsController {
    @Autowired
    private IEntityElementsService entityElementsService;
    @Autowired
    private IBoundedContextService boundedContextService;
    @Autowired
    private IKGGraphService KGGraphService;

    @ResponseBody
    @RequestMapping(value = "/getNodeAttrById", method = {RequestMethod.POST})
    @ApiOperation("获取实体节点属性")
    public R<HashMap<String, Object>> getNodeAttrById(String contextName, int nodeId) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        try {
            HashMap<String, Object> contents = KGGraphService.getNodeAttrById(contextName, nodeId);
            result.code = 200;
            result.setData(contents);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getOrderList", method = {RequestMethod.POST})
    @ApiOperation("获取指令列表")
    public R<List<Map<String, Object>>> getOrderList(int contextId, int nodeId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> orderList = entityElementsService.getOrderList(contextId, nodeId);
            result.setCode(200);
            result.setData(orderList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/createOrder", method = {RequestMethod.POST})
    @ApiOperation("创建指令")
    public R<String> createOrder(@RequestBody Order order) {
        R<String> result = new R<String>();
        try {
            //添加指令
            entityElementsService.createOrder(order);

            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateOrder", method = {RequestMethod.POST})
    @ApiOperation("更新指令")
    public R<String> updateOrder(@RequestBody Order order) {
        R<String> result = new R<String>();
        try {
            //修改指令
            entityElementsService.updateOrder(order);

            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteOrder", method = {RequestMethod.POST})
    @ApiOperation("删除指令")
    public R<String> deleteOrder(@RequestBody Order order) {
        R<String> result = new R<String>();
        try {
            //修改动作
            entityElementsService.deleteOrder(order.getId());

            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getEventList", method = {RequestMethod.POST})
    @ApiOperation("获取事件列表")
    public R<List<Map<String, Object>>> getEventList(int contextId, int nodeId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> eventList = entityElementsService.getEventList(contextId, nodeId);
            result.setCode(200);
            result.setData(eventList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/createEvent", method = {RequestMethod.POST})
    @ApiOperation("创建事件")
    public R<String> createEvent(@RequestBody Event event) {
        R<String> result = new R<String>();
        try {
            entityElementsService.createEvent(event);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateEvent", method = {RequestMethod.POST})
    @ApiOperation("更新事件")
    public R<String> updateEvent(@RequestBody Event event) {
        R<String> result = new R<String>();
        try {
            entityElementsService.updateEvent(event);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteEvent", method = {RequestMethod.POST})
    @ApiOperation("删除事件")
    public R<String> deleteEvent(@RequestBody Event event) {
        R<String> result = new R<String>();
        try {
            entityElementsService.deleteEvent(event.getId());
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/addStateFlow", method = {RequestMethod.POST})
    @ApiOperation("新建状态流")
    public R<String> addStateFlow(String graphData, Integer contextId, Integer nodeId, String mode) {
        R<String> result = new R<String>();
        try {
            if (!StringUtil.isBlank(graphData)) {
                entityElementsService.deleteStateFlowByNodeIdAndDomainId(contextId, nodeId, mode);
                StateFlow stateFlow = new StateFlow();
                stateFlow.setGraphData(graphData);
                stateFlow.setContextId(contextId);
                stateFlow.setNodeId(nodeId);
                stateFlow.setMode(mode);

                System.out.println(stateFlow);
                entityElementsService.saveStateFlow(stateFlow);// 保存到mysql
                result.code = 200;
                result.setMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getStateFlow", method = {RequestMethod.POST})
    @ApiOperation("更新状态流")
    public R<String> getStateFlow(Integer contextId, Integer nodeId, String mode) {
        R<String> result = new R<String>();
        System.out.println(mode);
        try {
            String graphData = entityElementsService.getStateFlow(contextId, nodeId, mode);
//            System.out.println(graphData);
            result.setCode(200);
            result.setData(graphData);
            result.setMsg("获取到数据");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/addStateAction", method = {RequestMethod.POST})
    @ApiOperation("添加状态动作")
    public R<String> addStateAction(StateAction stateAction) {
        R<String> result = new R<String>();
//        System.out.println(stateAction);
        try {
            entityElementsService.addStateAction(stateAction);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getStateActionList", method = {RequestMethod.POST})
    @ApiOperation("获取状态所有动作")
    public R<List<Map<String, Object>>> getStateActionList(int contextId, int nodeId, String stateName, String mode) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> stateActionList = entityElementsService.getStateActionList(contextId, nodeId, stateName, mode);
            System.out.println(stateActionList);
            for (Map<String, Object> action : stateActionList) {
                Object actionId = action.get("id");
                List<Map<String, Object>> paramList = entityElementsService.getParamListByActionId((String) actionId);
                action.put("params", paramList);
            }
            result.setCode(200);
            result.setData(stateActionList);
            result.setMsg("获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateStateAction", method = {RequestMethod.POST})
    @ApiOperation("更新状态动作")
    public R<String> updateStateAction(StateAction stateAction) {
        R<String> result = new R<String>();
//        System.out.println(stateAction);
        try {
            entityElementsService.updateStateAction(stateAction);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateStateActionName", method = {RequestMethod.POST})
    @ApiOperation("更新状态动作名称")
    public R<String> updateStateActionName(String newName, String oldName, int contextId, int nodeId) {
        R<String> result = new R<String>();
        System.out.println(newName);
        try {
            entityElementsService.updateStateActionName(newName, oldName, contextId, nodeId);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteStateAction", method = {RequestMethod.POST})
    @ApiOperation("删除状态动作")
    public R<String> deleteStateAction(@RequestBody String stateActionId) {
        R<String> result = new R<String>();
        Map stateActionIdMap = JSONObject.parseObject(stateActionId);
        String stateActionId_new = (String) stateActionIdMap.get("stateActionId");
        try {
            entityElementsService.deleteStateAction(stateActionId_new);
            entityElementsService.deleteAllParamsByActionId(stateActionId_new);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/addParam", method = {RequestMethod.POST})
    @ApiOperation("添加动作参数")
    public R<String> addParam(Param param) {
        R<String> result = new R<String>();
        System.out.println(param);
        try {
            entityElementsService.createParams(param);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getParamsByActionId", method = {RequestMethod.POST})
    @ApiOperation("根据动作Id获取参数")
    public R<List<Map<String, Object>>> getParamsByActionId(String actionId) {
        R<List<Map<String, Object>>> result = new R<>();
        try {
            List<Map<String, Object>> paramList = entityElementsService.getParamListByActionId(actionId);
            result.setCode(200);
            result.setData(paramList);
            result.setMsg("获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateParam", method = {RequestMethod.POST})
    @ApiOperation("更新动作参数")
    public R<String> updateParam(Param param) {
        R<String> result = new R<>();
        try {
            System.out.println(param);
            entityElementsService.updateParam(param);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteParamByParamId", method = {RequestMethod.POST})
    @ApiOperation("根据Id删除参数")
    public R<String> deleteParamByParamId(@RequestBody String paramId) {
        R<String> result = new R<String>();
        Map paramIdMap = JSONObject.parseObject(paramId);
        String paramId_new = (String) paramIdMap.get("paramId");
        try {
            entityElementsService.deleteParamByParamId(paramId_new);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    //获取、添加、删除自定义属性
    @ResponseBody
    @RequestMapping(value = "/updateUserAttr", method = {RequestMethod.POST})
    @ApiOperation("更新/添加自定义属性")
    public R<String> updateUserAttr(@RequestBody UserAttrs userAttrs) {
        R<String> result = new R<>();
        for (Map<String, Object> attrMap : userAttrs.getAttrList()) {
            String id = (String) attrMap.get("id");
            UserAttr userAttr = new UserAttr();
            userAttr.setId(id);
            userAttr.setAttrValue((String) attrMap.get("attrValue"));
            userAttr.setAttrLabel((String) attrMap.get("attrLabel"));
            userAttr.setContextId(userAttrs.getContextId());
            userAttr.setNodeId(userAttrs.getNodeId());
            System.out.println(userAttr);
            if (entityElementsService.isUserAttrExist(id) == true) {
                entityElementsService.updateUserAttr(userAttr);
            } else {
                entityElementsService.createUserAttr(userAttr);
            }
            ;
        }
        try {
            result.code = 200;
            result.setMsg("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getUserAttrList", method = {RequestMethod.POST})
    @ApiOperation("获取所有自定义属性")
    public R<List<Map<String, Object>>> getUserAttrList(int contextId, int nodeId) {
        R<List<Map<String, Object>>> result = new R<>();
        try {
            List<Map<String, Object>> UserAttrList = entityElementsService.getUserAttrList(contextId, nodeId);
            result.setCode(200);
            result.setData(UserAttrList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getUserAttr", method = {RequestMethod.POST})
    @ApiOperation("根据属性名获取自定义属性")
    public R<String> getUserAttr(String attrLabel) {
        R<String> result = new R<>();
        try {
            String UserAttr = entityElementsService.getUserAttr(attrLabel);
            result.setCode(200);
            result.setData(UserAttr);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteUserAttr", method = {RequestMethod.POST})
    @ApiOperation("删除自定义属性")
    public R<String> deleteUserAttr(String attrId) {
        R<String> result = new R<>();
        try {
            System.out.println(attrId);
            entityElementsService.deleteUserAttrById(attrId);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/saveNodeContent", method = {RequestMethod.POST})
    @ApiOperation("保存节点富文本内容")
    public R<String> saveNodeContent(@RequestBody Map<String, Object> params) {
        R<String> result = new R<String>();
        try {
            String username = "tc";
            int contextId = (int) params.get("contextId");
            String nodeId = params.get("nodeId").toString();
            String content = params.get("content").toString();
            List<Map<String, Object>> contextList = boundedContextService.getContextById(contextId);
            if (contextList != null && contextList.size() > 0) {
                String domainName = contextList.get(0).get("name").toString();
                // 检查是否存在
                List<Map<String, Object>> items = entityElementsService.getNodeContent(contextId, Integer.parseInt(nodeId));
                if (items != null && items.size() > 0) {
                    Map<String, Object> oldItem = items.get(0);
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("contextId", oldItem.get("contextId"));
                    item.put("nodeId", oldItem.get("nodeId"));
                    item.put("content", content);
                    item.put("modifyUser", username);
                    item.put("modifyTime", DateUtil.getDateNow());
                    entityElementsService.updateNodeContent(item);
                    result.code = 200;
                    result.setMsg("更新成功");
                } else {
                    Map<String, Object> sb = new HashMap<String, Object>();
                    sb.put("content", content);
                    sb.put("contextId", contextId);
                    sb.put("nodeId", nodeId);
                    sb.put("status", 1);
                    sb.put("createUser", username);
                    sb.put("createTime", DateUtil.getDateNow());
                    if (sb != null && sb.size() > 0) {
                        entityElementsService.saveNodeContent(sb);
                        result.code = 200;
                        result.setMsg("保存成功");
                    }
                }
                // 更新到图数据库,表明该节点有附件,加个标识,0=没有,1=有
                KGGraphService.updateNodeFileStatus(domainName, Long.parseLong(nodeId), 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getNodeContent", method = {RequestMethod.POST})
    @ApiOperation("获取节点富文本内容")
    public R<Map<String, Object>> getNodeContent(int contextId, int nodeId) {
        R<Map<String, Object>> result = new R<Map<String, Object>>();
        try {
            List<Map<String, Object>> contents = entityElementsService.getNodeContent(contextId, nodeId);
            if (contents != null && contents.size() > 0) {
                result.code = 200;
                result.setData(contents.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getNodeImage", method = {RequestMethod.POST})
    @ApiOperation("获取图片列表")
    public R<List<Map<String, Object>>> getNodeImagelist(int contextId, int nodeId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> images = entityElementsService.getNodeImageList(contextId, nodeId);
            result.code = 200;
            result.setData(images);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/saveNodeImage", method = {RequestMethod.POST})
    @ApiOperation("保存节点图片")
    public R<String> saveNodeImage(@RequestBody Map<String, Object> params) {
        R<String> result = new R<String>();
        try {
            String userName = "tc";
            int contextId = (int) params.get("contextId");
            String nodeId = params.get("nodeId").toString();
            String imageList = params.get("imageList").toString();
            List<Map<String, Object>> domainList = boundedContextService.getContextById(contextId);
            if (domainList != null && domainList.size() > 0) {
                String domainName = domainList.get(0).get("name").toString();
                entityElementsService.deleteNodeImage(contextId, Integer.parseInt(nodeId));
                List<Map> imageItems = JSON.parseArray(imageList, Map.class);
                List<Map<String, Object>> submitItemList = new ArrayList<Map<String, Object>>();
                if (!imageItems.isEmpty()) {
                    for (Map<String, Object> item : imageItems) {
                        String file = item.get("fileurl").toString();
                        int sourcetype = 0;
                        Map<String, Object> sb = new HashMap<String, Object>();
                        sb.put("file", file);
                        sb.put("imageType", sourcetype);
                        sb.put("contextId", contextId);
                        sb.put("nodeId", nodeId);
                        sb.put("status", 1);
                        sb.put("createUser", userName);
                        sb.put("createTime", DateUtil.getDateNow());
                        submitItemList.add(sb);
                    }
                }
                if (submitItemList != null && submitItemList.size() > 0) {
                    entityElementsService.saveNodeImage(submitItemList);
                    // 更新到图数据库,表明该节点有附件,加个标识,0=没有,1=有
                    KGGraphService.updateNodeFileStatus(domainName, Long.parseLong(nodeId), 1);
                    result.code = 200;
                    result.setMsg("操作成功");
                } else {
                    KGGraphService.updateNodeFileStatus(domainName, Long.parseLong(nodeId), 0);
                    result.code = 200;
                    result.setMsg("操作成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getNodeDetail", method = {RequestMethod.POST})
    @ApiOperation("获取节点的富文本和图片")
    public R<Map<String, Object>> getNodeDetail(int contextId, int nodeId) {
        R<Map<String, Object>> result = new R<Map<String, Object>>();
        try {
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("content", "");
            res.put("imageList", new String[]{});
            List<Map<String, Object>> contents = entityElementsService.getNodeContent(contextId, nodeId);
            if (contents != null && contents.size() > 0) {
                res.replace("content", contents.get(0).get("content"));
            }
            List<Map<String, Object>> images = entityElementsService.getNodeImageList(contextId, nodeId);
            if (images != null && images.size() > 0) {
                res.replace("imageList", images);
            }
            result.code = 200;
            result.setData(res);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }
}
