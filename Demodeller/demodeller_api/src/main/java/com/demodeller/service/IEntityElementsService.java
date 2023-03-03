package com.demodeller.service;


import com.demodeller.entity.*;

import java.util.List;
import java.util.Map;

public interface IEntityElementsService {

	void createOrder(Order order);
	List<Map<String,Object>> getOrderList(int contextId, int nodeId);
	void updateOrder(Order order);
	void deleteOrder(String orderId);

	void createEvent(Event event);
	void updateEvent(Event event);
	List<Map<String,Object>> getEventList(int contextId, int nodeId);
	void deleteEvent(String eventId);


	void createParams(Param param);
	void createRes(Response res);
	List<Map<String,Object>> getParamListByActionId(String actionId);
	List<String> getCodeListByActionId(String actionId);
	void updateParam(Param param);
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
	boolean isUserAttrExist(String attrId);
	void createUserAttr(UserAttr userAttr);
	void deleteUserAttrById(String attrId);

	void saveNodeImage(List<Map<String, Object>> mapList);
	void saveNodeContent(Map<String, Object> map);
	void updateNodeContent(Map<String, Object> map);
	List<Map<String,Object>> getNodeImageList(Integer contextId,Integer nodeId);
	List<Map<String,Object>> getNodeContent(Integer contextId, Integer nodeId);
	void deleteNodeImage(Integer contextId,Integer nodeId);
}
