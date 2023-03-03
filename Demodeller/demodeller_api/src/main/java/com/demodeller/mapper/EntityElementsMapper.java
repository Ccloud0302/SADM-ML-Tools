package com.demodeller.mapper;

import com.demodeller.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface EntityElementsMapper {



    void createOrder(Order order);
    List<Map<String,Object>> getOrderList(int contextId, int nodeId);
    void updateOrder(Order order);
    void deleteOrder(String orderId);

    void createEvent(Event event);
    List<Map<String, Object>> getEventList(int contextId, int nodeId);
    void updateEvent(Event event);
    void deleteEvent(String actionId);


    void createParams(com.demodeller.entity.Param param);
    void createRes(Response res);
    List<Map<String,Object>> getParamListByActionId(String actionId);
    List<String> getCodeListByActionId(String actionId);
    void updateParam(com.demodeller.entity.Param param);
    void deleteAllResByActionId(String actionId);
    void deleteAllParamsByActionId(String actionId);

    void saveStateFlow(StateFlow stateFlow);
    void deleteStateFlowByNodeIdAndDomainId(Integer contextId, Integer nodeId, String mode);
    String getStateFlow(Integer contextId, Integer nodeId, String mode);

    List<Map<String,Object>> getStateActionList(int contextId, int nodeId, String stateName, String mode);
    void addStateAction(StateAction stateAction);
    void updateStateAction(StateAction stateAction);
    void deleteStateAction(String stateActionId);
    void updateStateActionName(String newName, String oldName, int contextId, int nodeId);
    void deleteParamByParamId(String paramId);

    List<Map<String,Object>> getUserAttrList(int contextId, int nodeId);
    String getUserAttr(String attrLabel);
    void updateUserAttr(UserAttr userAttr);
    List<Map<String,Object>> isUserAttrExist(String attrId);
    void createUserAttr(UserAttr userAttr);
    void deleteUserAttrById(String attrId);

    void saveNodeImage(@Param("maplist") List<Map<String, Object>> mapList);
    List<Map<String, Object>> getNodeImageList(@Param("contextId") Integer contextId, @Param("nodeId") Integer nodeId);
    void deleteNodeImage(@Param("contextId") Integer contextId, @Param("nodeId") Integer nodeId);


    void saveNodeContent(@Param("params") Map<String, Object> map);
    List<Map<String, Object>> getNodeContent(@Param("contextId") Integer contextId, @Param("nodeId") Integer nodeId);
    void updateNodeContent(@Param("params") Map<String, Object> map);




}
