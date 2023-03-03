package com.demodeller.mapper;

import com.demodeller.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BoundedContextMapper {
    List<Map<String, Object>> loadContext(@Param("contextName") String contextName, @Param("createUser") String createUser, @Param("domainId") int domainId);
    List<Map<String,Object>> getContexts(int domainId);
    List<Map<String,Object>> getContextById(int contextId);
    List<Map<String, Object>> getContextByName(@Param("contextName") String contextName);
    void createContext(@Param("params") Map<String, Object> map);
    void deleteContext(@Param("contextId") Integer contextId);
    void updateContext(BoundedContext boundedContext);

    void createWordClass(WordClass wordClass);
    List<Map<String,Object>> getWordClassList(int contextId);
    void deleteWordClass(String wordId);
    void deleteWordClassByContextId(Integer contextId);
    void updateWordClass(WordClass wordClass);

    void createWordClassAttr(WordClassAttr wordClassAttr);
    List<Map<String,Object>> getWordClassAttrList(int contextId, String classId);
    void deleteWordClassAttr(String classAttrId);
    void updateWordClassAttr(WordClassAttr wordClassAttr);

    void createWordRel(WordRel wordRel);
    List<Map<String,Object>> getWordRelList(int contextId);
    void deleteWordRel(String relId);
    void deleteWordRelByContextId(Integer contextId);
    void updateWordRel(WordRel wordRel);

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
