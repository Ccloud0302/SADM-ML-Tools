package com.demodeller.service;


import com.demodeller.entity.*;

import java.util.List;
import java.util.Map;


public interface IBoundedContextService {
	List<Map<String,Object>> getContextById(int contextId);
	List<Map<String,Object>> getContextByName(String contextName);
	List<Map<String,Object>> loadContext(String contextName, String createUser, int domainId);
	List<Map<String,Object>> getContexts(int domainId);

	void createContext(Map<String, Object> map);
	void updateContext(BoundedContext boundedContext);
	void deleteContext(Integer contextId);

	void createWordClass(WordClass wordClass);
	List<Map<String,Object>> getWordClassList(int contextId);
	void deleteWordClass(String wordId);
	void deleteWordClassByContextId(Integer contextId);
	void updateWordClass(WordClass wordClass);

	void createWordRel(WordRel wordRel);
	List<Map<String,Object>> getWordRelList(int contextId);
	void deleteWordRel(String relId);
	void deleteWordRelByContextId(Integer contextId);
	void updateWordRel(WordRel wordRel);

	void createWordClassAttr(WordClassAttr wordClassAttr);
	List<Map<String,Object>> getWordClassAttrList(int contextId, String classId);
	void deleteWordClassAttr(String classAttrId);
	void updateWordClassAttr(WordClassAttr wordClassAttr);

	List<Map<String,Object>> getAllMsgOfContext(int contextId, int domainId);
	List<Map<String,Object>> getAllMsgOfDomain(int domainId);
	void createMsg(Msg msg);
	void deleteMsg(String msgId);
	void deleteMsgByContextId(Integer contextId);
	void updateMsg(Msg msg);

	List<Map<String,Object>> getAllTriggerActionByContextId(int contextId);
	List<Map<String,Object>> getAllCauseEventByContextId(int contextId);

	void createUncertainty(Uncertainty uncertainty);
	void deleteUncertainty(String uncertaintyId);
	void updateUncertainty(Uncertainty uncertainty);
	List<Map<String,Object>> getAllUncertainty(int contextId, int domainId);
}
