package com.demodeller.service.impl;

import com.demodeller.entity.*;
import com.demodeller.mapper.BoundedContextMapper;
import com.demodeller.service.IBoundedContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BoundedContextService implements IBoundedContextService {

    @Autowired
    private BoundedContextMapper boundedContextMapper;


    @Override
    public List<Map<String,Object>> getContextById(int contextId) {
        return boundedContextMapper.getContextById(contextId);
    }

    @Override
    public List<Map<String, Object>> getContextByName(String contextName) {
        return boundedContextMapper.getContextByName(contextName);
    }

    @Override
    public List<Map<String, Object>> getContexts(int domainId) {
        return boundedContextMapper.getContexts(domainId);
    }

    @Override
    public List<Map<String, Object>> loadContext(String contextName, String createUser, int domainId) {
        List<Map<String, Object>> list = boundedContextMapper.loadContext(contextName, createUser, domainId);
        return list;
    }

    @Override
    public void updateContext(BoundedContext boundedContext) {
        boundedContextMapper.updateContext(boundedContext);
    }

    @Override
    public void createContext(Map<String, Object> map) {
        boundedContextMapper.createContext(map);
    }


    @Override
    public void deleteContext(Integer contextId) {
        boundedContextMapper.deleteContext(contextId);
    }




    @Override
    public void createWordClass(WordClass wordClass) {
        boundedContextMapper.createWordClass(wordClass);
    }

    @Override
    public List<Map<String, Object>> getWordClassList(int contextId) {
        return boundedContextMapper.getWordClassList(contextId);
    }

    @Override
    public void deleteWordClass(String wordId) {
        boundedContextMapper.deleteWordClass(wordId);
    }

    @Override
    public void deleteWordClassByContextId(Integer contextId) {
        boundedContextMapper.deleteWordClassByContextId(contextId);
    }

    @Override
    public void updateWordClass(WordClass wordClass) {
        boundedContextMapper.updateWordClass(wordClass);
    }


    @Override
    public void createWordClassAttr(WordClassAttr wordClassAttr) {
        boundedContextMapper.createWordClassAttr((wordClassAttr));
    }

    @Override
    public List<Map<String, Object>> getWordClassAttrList(int contextId, String classId) {
        return boundedContextMapper.getWordClassAttrList(contextId, classId);
    }

    @Override
    public void deleteWordClassAttr(String classAttrId) {
        boundedContextMapper.deleteWordClassAttr(classAttrId);
    }

    @Override
    public void updateWordClassAttr(WordClassAttr wordClassAttr) {
        boundedContextMapper.updateWordClassAttr(wordClassAttr);
    }

    @Override
    public void createWordRel(WordRel wordRel) {
        boundedContextMapper.createWordRel(wordRel);
    }

    @Override
    public List<Map<String, Object>> getWordRelList(int contextId) {
        return boundedContextMapper.getWordRelList(contextId);
    }

    @Override
    public void deleteWordRel(String relId) {
        boundedContextMapper.deleteWordRel(relId);
    }

    @Override
    public void deleteWordRelByContextId(Integer contextId) {
        boundedContextMapper.deleteWordRelByContextId(contextId);
    }

    @Override
    public void updateWordRel(WordRel wordRel) {
        boundedContextMapper.updateWordRel(wordRel);
    }

    @Override
    public List<Map<String, Object>> getAllMsgOfContext(int contextId, int domainId) {
        return boundedContextMapper.getAllMsgOfContext(contextId, domainId);
    }

    @Override
    public List<Map<String, Object>> getAllMsgOfDomain(int domainId) {
        return boundedContextMapper.getAllMsgOfDomain(domainId);
    }




    @Override
    public void createMsg(Msg msg) {
        boundedContextMapper.createMsg(msg);
    }

    @Override
    public void deleteMsg(String msgId) {
        boundedContextMapper.deleteMsg(msgId);
    }

    @Override
    public void deleteMsgByContextId(Integer contextId) {
        boundedContextMapper.deleteMsgByContextId(contextId);
    }

    @Override
    public void updateMsg(Msg msg) {
        boundedContextMapper.updateMsg(msg);
    }



    @Override
    public List<Map<String, Object>> getAllTriggerActionByContextId(int contextId) {
        return boundedContextMapper.getAllTriggerActionByContextId(contextId);
    }

    @Override
    public List<Map<String, Object>> getAllCauseEventByContextId(int contextId) {
        return boundedContextMapper.getAllCauseEventByContextId(contextId);
    }

    @Override
    public void createUncertainty(Uncertainty uncertainty) {
        boundedContextMapper.createUncertainty(uncertainty);
    }

    @Override
    public void deleteUncertainty(String uncertaintyId) {
        boundedContextMapper.deleteUncertainty(uncertaintyId);
    }

    @Override
    public void updateUncertainty(Uncertainty uncertainty) {
        boundedContextMapper.updateUncertainty(uncertainty);
    }

    @Override
    public List<Map<String, Object>> getAllUncertainty(int contextId, int domainId) {
        return boundedContextMapper.getAllUncertainty(contextId, domainId);
    }
}
