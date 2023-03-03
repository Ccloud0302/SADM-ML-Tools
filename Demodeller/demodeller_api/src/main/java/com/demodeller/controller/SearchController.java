package com.demodeller.controller;

import com.alibaba.fastjson.JSON;
import com.demodeller.entity.Domain;
import com.demodeller.query.GraphQuery;
import com.demodeller.common.R;

import com.demodeller.service.IDomainService;
import com.demodeller.service.IEntityElementsService;
import com.demodeller.service.IKGGraphService;
import com.demodeller.service.impl.BoundedContextService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
public class SearchController {
    @Autowired
    private BoundedContextService boundedContextService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IKGGraphService KGGraphService;

    @Autowired
    private IEntityElementsService entityElementsService;

    @ResponseBody
    @RequestMapping(value = "/toModelNotInput", method = {RequestMethod.POST})
    @ApiOperation("（上下文内）Json展示模型")
    public R<String> toModelNotInput(int domainId) {
        R<String> result = new R<String>();
        HashMap<String, Object> Model = new HashMap<>();
        try {
            //获取某个领域内所有上下文
            System.out.println("######################开始######################");
            List<Map<String, Object>> contextList = boundedContextService.getContexts(domainId);
            Domain domain = domainService.getById(domainId);
            String domainName = domain.getName();
            System.out.println("1.1、================领域名称================");
            System.out.println(domainName);
            System.out.println("1.2、================所有上下文================");
            System.out.println(contextList);

            List<HashMap<String, Object>> newDomainList = new ArrayList<>();
            for (Map<String, Object> map : contextList) {
                Integer contextId = (Integer) map.get("id");
                String contextName = (String) map.get("name");
                System.out.println("=========上下文：" + contextName + "========");
                GraphQuery query = new GraphQuery();
                query.setContextName(contextName);
                //获取某个上下文内所有节点细节和关系
                HashMap<String, Object> graphData = KGGraphService.getGraphOfContext(query);
                System.out.println("2、================该上下文所有实体和关系");
                List<Map<String, Object>> nodeList = (List<Map<String, Object>>) graphData.get("node");
                HashMap<String, Object> nodeMap = new HashMap<>();

                System.out.println(nodeList);
                List<Map<String, Object>> relList = (List<Map<String, Object>>) graphData.get("relationship");
                System.out.println(relList);
                //获取某个上下文内所有通信（和其他上下文的联系）
                List<Map<String, Object>> msgList = boundedContextService.getAllMsgOfContext(contextId, domainId);
                System.out.println("3、================该上下文内所有通信");
                System.out.println(msgList);
                //获取某个上下文内所有节点（用来进一步查询节点动作）
                List<HashMap<String, Object>> nodes = KGGraphService.getNodesOfContext(contextName);
                for (Map<String, Object> node : nodeList) {
                    Integer nodeId = Integer.parseInt(node.get("uuid").toString());
                    String nodeName = (String) node.get("code");
                    System.out.println("4.0、================该上下文内节点" + nodeId + "的自定义属性");
                    List<Map<String, Object>> userAttrList = entityElementsService.getUserAttrList(contextId, nodeId);

                    System.out.println("4.1、================该上下文内节点" + nodeId + "的事件");
                    List<Map<String, Object>> eventList = entityElementsService.getEventList(contextId, nodeId);
                    System.out.println(eventList);


                    System.out.println("4.3、================该上下文内节点" + nodeId + "的状态");
                    List<String> modeList = new ArrayList<>();
                    modeList.add("Production");
                    modeList.add("Maintenance");
                    modeList.add("Manual");
                    List<Map<String, Object>> statusList = new ArrayList<>();
                    for (String mode : modeList) {
                        String stateJson = entityElementsService.getStateFlow(contextId, nodeId, mode);
                        if (stateJson != null) {
                            //把状态图中的状态抽取出来放数组里
                            Map<String, List<Map<String, Object>>> stateFlowMap = (Map<String, List<Map<String, Object>>>) JSON.parse(stateJson);
                            System.out.println(stateFlowMap);
                            List<Map<String, Object>> edgeList = new ArrayList<>();
                            List<Map<String, Object>> stateList = new ArrayList<>();
                            for (Map<String, Object> state : stateFlowMap.get("cells")) {
                                if (state.get("shape").equals("rect")) {
                                    if ((Integer) ((Map<String, Object>) state.get("size")).get("width") < 600) {
                                        Map<String, Object> stateMap = new HashMap<>();
                                        stateMap.put("id", state.get("id"));
                                        stateMap.put("name", ((Map<String, Object>) ((Map<String, Object>) state.get("attrs")).get("label")).get("text"));
                                        stateMap.put("des", ((Map<String, Object>) state.get("data")).get("des"));
                                        stateList.add(stateMap);
                                    }
                                } else if (state.get("shape").equals("edge")) {
                                    List<Map<String, Object>> labels = (List<Map<String, Object>>) state.get("labels");
                                    if (labels.get(0).containsKey("attrs")) {
                                        Map<String, Object> edgeMap = new HashMap<>();
                                        edgeMap.put("text", ((Map<String, Object>) ((Map<String, Object>) labels.get(0).get("attrs")).get("label")).get("text"));
                                        edgeMap.put("sourceId", ((Map<String, Object>) state.get("source")).get("cell"));
                                        edgeMap.put("targetId", ((Map<String, Object>) state.get("target")).get("cell"));
                                        edgeList.add(edgeMap);
                                    }
                                }
                            }
                            ;
                            System.out.println(stateList);
                            System.out.println(edgeList);

                            //把命令和状态绑定
                            for (int i = 0; i < stateList.size(); i++) {
                                for (int j = 0; j < edgeList.size(); j++) {
                                    if (edgeList.get(j).get("sourceId").equals(stateList.get(i).get("id"))) {
                                        stateList.get(i).put("postCommand", edgeList.get(j).get("text"));
                                    } else if (edgeList.get(j).get("targetId").equals(stateList.get(i).get("id"))) {
                                        stateList.get(i).put("preCommand", edgeList.get(j).get("text"));
                                    }
                                }
                            }
                            System.out.println(stateList);

                            Map<String, Object> statusMap = new HashMap<>();
                            statusMap.put("Mode", mode);
                            statusMap.put("State", stateList);
                            statusList.add(statusMap);
                            System.out.println(statusList);
                        }
                    }
                    System.out.println("*******节点结束******");

                    //往节点map内添加动作、状态和指令
//                    node.put("指令", orderList);
                    node.put("userAttr", userAttrList);
                    node.put("event", eventList);
                    node.put("state", statusList);
                    nodeMap.put(nodeName.replace(" ", ""), node);
                }

                //整合↓
                //该上下文新的节点List
                List<HashMap<String, Object>> newNodeList = new ArrayList<>();
                newNodeList.add(nodeMap);
//                System.out.println(newNodeList);
                //往上下文map里加通信、关系、实体
                HashMap<String, Object> contextMap = new HashMap<>();
                contextMap.put("communication", msgList);
                contextMap.put("relationship", relList);
                contextMap.put("entityList", newNodeList);
//                System.out.println(domainMap);
                HashMap<String, Object> newContextMap = new HashMap<>();
                newContextMap.put("contextName", contextName);
                newContextMap.put("BoundedContext", contextMap);
                System.out.println(newContextMap);
                newDomainList.add(newContextMap);
                System.out.println("*****************上下文结束*****************");
            }

            System.out.println("&&&&&&&&&&&&&&&最终模型&&&&&&&&&&&&&&&&&&");
            Model.put("domainName", domainName);
            Model.put("Domain", newDomainList);
            HashMap<String, Object> Final = new HashMap<>();
            Final.put("Model", Model);
            String jsonString = JSON.toJSONString(Final);
            System.out.println(jsonString);
            result.setData(jsonString);
            result.setCode(200);
            result.setMsg("获取成功");

        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }
}
