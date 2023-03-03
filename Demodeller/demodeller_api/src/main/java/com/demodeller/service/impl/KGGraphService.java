package com.demodeller.service.impl;

import com.demodeller.base.util.GraphPageRecord;
import com.demodeller.dal.IKGraphRepository;
import com.demodeller.entity.QAEntityItem;
import com.demodeller.query.GraphQuery;
import com.demodeller.service.IKGGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KGGraphService implements IKGGraphService {

    @Autowired
    @Qualifier("KGraphRepository")
    private IKGraphRepository kgRepository;
    
    @Override
	public GraphPageRecord<HashMap<String, Object>> getPageContext(GraphQuery queryItem) {
		return kgRepository.getPageContext(queryItem);
	}
    @Override
    public void deleteKGContext(String contextName) {
        kgRepository.deleteKGContext(contextName);
    }

    @Override
    public HashMap<String, Object> getGraphOfContext(GraphQuery query) {
        return kgRepository.getGraphOfContext(query);
    }

    @Override
    public List<HashMap<String, Object>> getNodesOfContext(String contextName) {
        return kgRepository.getNodesOfContext(contextName);
    }

    @Override
    public long getRelationNodeCount(String contextName, long nodeId) {
        return kgRepository.getRelationNodeCount(contextName, nodeId);
    }

    @Override
    public void createContext(String contextName) {
        kgRepository.createContext(contextName);
    }

    @Override
    public void updateContextName(String oldName, String contextName) {
        kgRepository.updateContextName(oldName, contextName);
    }

    @Override
    public HashMap<String, Object> getMoreRelationNode(String contextName, long nodeId) {
        return kgRepository.getMoreRelationNode(contextName, nodeId);
    }

    @Override
    public HashMap<String, Object> updateNodeName(String contextName, long nodeId, String nodeName) {
        return kgRepository.updateNodeName(contextName, nodeId, nodeName);
    }

    @Override
    public HashMap<String, Object> createNode(String contextName, QAEntityItem entity) {
        return kgRepository.createNode(contextName, entity);
    }

    @Override
    public HashMap<String, Object> batchCreateNode(String contextName, String sourceName, String relation,
                                                   String[] targetNames) {
        return kgRepository.batchCreateNode(contextName, sourceName, relation, targetNames);
    }

    @Override
    public HashMap<String, Object> batchCreateChildNode(String contextName, String sourceId, Integer entityType,
                                                        String[] targetNames, String relation) {
        return kgRepository.batchCreateChildNode(contextName, sourceId, entityType, targetNames, relation);
    }

    @Override
    public List<HashMap<String, Object>> batchCreateSameNode(String contextName, Integer entityType, String[] sourceNames) {
        return kgRepository.batchCreateSameNode(contextName, entityType, sourceNames);
    }

    @Override
    public HashMap<String, Object> createLink(String contextName, long sourceId, long targetId, String ship) {
        return kgRepository.createLink(contextName, sourceId, targetId, ship);
    }

    @Override
    public HashMap<String, Object> updateLink(String contextName, long shipId, String shipName) {
        return kgRepository.updateLink(contextName, shipId, shipName);
    }

    @Override
    public List<HashMap<String, Object>> deleteNode(String contextName, long nodeId) {
        return kgRepository.deleteNode(contextName, nodeId);
    }

    @Override
    public void deleteLink(String contextName, long shipId) {
        kgRepository.deleteLink(contextName, shipId);
    }

    @Override
    public HashMap<String, Object> createGraphByText(String contextName, Integer entityType, Integer operateType,
                                                     Integer sourceId, String[] rss) {
        return kgRepository.createGraphByText(contextName, entityType, operateType, sourceId, rss);
    }

    @Override
    public void batchCreateGraph(String contextName, List<Map<String, Object>> params) {
        kgRepository.batchCreateGraph(contextName, params);
    }

    @Override
    public void updateNodeFileStatus(String contextName, long nodeId, int status) {
        kgRepository.updateNodeFileStatus(contextName,nodeId,status);
    }

    @Override
    public void updateCorrdOfNode(String contextName, String uuid, Double fx, Double fy) {
        kgRepository.updateCorrdOfNode(contextName,uuid,fx,fy);
    }

    @Override
	public void batchInsertByCSV(String contextName, String csvUrl, int status) {
		kgRepository.batchInsertByCSV(contextName, csvUrl, status);
	}

    @Override
    public HashMap<String, Object> getNodeAttrById(String contextName, int nodeId) {
        return kgRepository.getNodeAttrById(contextName, nodeId);
    }
}
