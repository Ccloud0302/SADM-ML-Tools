package com.demodeller.service.impl;

import com.demodeller.entity.*;
import com.demodeller.mapper.EntityElementsMapper;
import com.demodeller.service.IEntityElementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EntityElementsService implements IEntityElementsService {

    @Autowired
    private EntityElementsMapper entityElementsMapper;


    @Override
    public void createOrder(Order order) {
        entityElementsMapper.createOrder(order);
    }

    @Override
    public List<Map<String, Object>> getOrderList(int contextId, int nodeId) {
        return entityElementsMapper.getOrderList(contextId, nodeId);
    }

    @Override
    public void updateOrder(Order order) {
        entityElementsMapper.updateOrder(order);
    }

    @Override
    public void deleteOrder(String orderId) {
        entityElementsMapper.deleteOrder(orderId);
    }

    @Override
    public void createEvent(Event event) {
        entityElementsMapper.createEvent(event);
    }

    @Override
    public List<Map<String, Object>> getEventList(int contextId, int nodeId) {
        return entityElementsMapper.getEventList(contextId, nodeId);
    }

    @Override
    public void updateEvent(Event event) {
        entityElementsMapper.updateEvent(event);
    }

    @Override
    public void deleteEvent(String eventId) {
        entityElementsMapper.deleteEvent(eventId);
    }


    @Override
    public void createParams(Param param) {
        entityElementsMapper.createParams(param);
    }

    @Override
    public void createRes(Response res) {
        entityElementsMapper.createRes(res);
    }

    @Override
    public List<Map<String, Object>> getParamListByActionId(String actionId) {
        return entityElementsMapper.getParamListByActionId(actionId);
    }

    @Override
    public List<String> getCodeListByActionId(String actionId) {
        return entityElementsMapper.getCodeListByActionId(actionId);
    }



    @Override
    public void updateParam(Param param) {
        entityElementsMapper.updateParam(param);
    }

    @Override
    public void deleteAllResByActionId(String actionId) {
        entityElementsMapper.deleteAllResByActionId(actionId);
    }



    @Override
    public void deleteAllParamsByActionId(String actionId) {
        entityElementsMapper.deleteAllParamsByActionId(actionId);
    }


    @Override
    public void saveStateFlow(StateFlow stateFlow) {
        entityElementsMapper.saveStateFlow(stateFlow);
    }

    @Override
    public void deleteStateFlowByNodeIdAndDomainId(Integer contextId, Integer nodeId, String mode) {
        entityElementsMapper.deleteStateFlowByNodeIdAndDomainId(contextId, nodeId, mode);
    }

    @Override
    public String getStateFlow(Integer contextId, Integer nodeId, String mode) {
        return entityElementsMapper.getStateFlow(contextId, nodeId, mode);
    }


    @Override
    public List<Map<String, Object>> getStateActionList(int contextId, int nodeId, String stateName, String mode) {
        return entityElementsMapper.getStateActionList(contextId, nodeId, stateName, mode);
    }

    @Override
    public void addStateAction(StateAction stateAction) {
        entityElementsMapper.addStateAction(stateAction);
    }

    @Override
    public void updateStateAction(StateAction stateAction) {
        entityElementsMapper.updateStateAction(stateAction);
    }

    @Override
    public void deleteStateAction(String stateActionId) {
        entityElementsMapper.deleteStateAction(stateActionId);
    }

    @Override
    public void updateStateActionName(String newName, String oldName, int contextId, int nodeId) {
        entityElementsMapper.updateStateActionName(newName, oldName, contextId, nodeId);
    }

    @Override
    public void deleteParamByParamId(String paramId) {
        entityElementsMapper.deleteParamByParamId(paramId);
    }


    @Override
    public List<Map<String, Object>> getUserAttrList(int contextId, int nodeId) {
        return entityElementsMapper.getUserAttrList(contextId, nodeId);
    }

    @Override
    public String getUserAttr(String attrLabel) {
        return entityElementsMapper.getUserAttr(attrLabel);
    }

    @Override
    public void updateUserAttr(UserAttr userAttr) {
        entityElementsMapper.updateUserAttr(userAttr);
    }

    @Override
    public boolean isUserAttrExist(String attrId) {
        if (entityElementsMapper.isUserAttrExist(attrId).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void createUserAttr(UserAttr userAttr) {
        entityElementsMapper.createUserAttr(userAttr);
    }

    @Override
    public void deleteUserAttrById(String attrId) {
        entityElementsMapper.deleteUserAttrById(attrId);
    }

    @Override
    public void saveNodeImage(List<Map<String, Object>> mapList) {
        entityElementsMapper.saveNodeImage(mapList);
    }

    @Override
    public void saveNodeContent(Map<String, Object> map) {
        entityElementsMapper.saveNodeContent(map);
    }

    @Override
    public void updateNodeContent(Map<String, Object> map) {
        entityElementsMapper.updateNodeContent(map);
    }

    @Override
    public List<Map<String, Object>> getNodeImageList(Integer contextId, Integer nodeId) {
        return entityElementsMapper.getNodeImageList(contextId, nodeId);
    }

    @Override
    public List<Map<String, Object>> getNodeContent(Integer contextId, Integer nodeId) {
        return entityElementsMapper.getNodeContent(contextId, nodeId);
    }

    @Override
    public void deleteNodeImage(Integer contextId, Integer nodeId) {
        entityElementsMapper.deleteNodeImage(contextId, nodeId);
    }

}
