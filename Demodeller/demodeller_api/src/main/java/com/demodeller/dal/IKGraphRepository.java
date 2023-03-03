package com.demodeller.dal;

import com.demodeller.base.util.GraphPageRecord;
import com.demodeller.entity.QAEntityItem;
import com.demodeller.query.GraphQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface IKGraphRepository {
	/**
	 * 领域标签分页
	 * @param queryItem
	 * @return
	 */
	GraphPageRecord<HashMap<String, Object>> getPageContext(GraphQuery queryItem);
	/**
	 * 删除Neo4j 标签
	 * 
	 * @param contextName
	 */
	void deleteKGContext(String contextName);

	/**
	 * 查询图谱节点和关系
	 * 
	 * @param query
	 * @return node relationship
	 */
	HashMap<String, Object> getGraphOfContext(GraphQuery query);

	/**
	 * 获取节点列表
	 * 
	 * @param contextName

	 * @return
	 */
	List<HashMap<String, Object>>getNodesOfContext(String contextName);

	/**
	 * 获取某个领域指定节点拥有的上下级的节点数
	 * 
	 * @param contextName
	 * @param nodeId
	 * @return long 数值
	 */
	long getRelationNodeCount(String contextName, long nodeId);

	/**
	 * 创建领域,默认创建一个新的节点,给节点附上默认属性
	 * 
	 * @param contextName
	 */
	void createContext(String contextName);
	/**
	 * 更新领域名称
	 *
	 * @param contextName
	 * @param oldName
	 */
	void updateContextName(String oldName, String contextName);

	/**
	 * 获取/展开更多节点,找到和该节点有关系的节点
	 * 
	 * @param contextName
	 * @param nodeId
	 * @return
	 */
	HashMap<String, Object> getMoreRelationNode(String contextName, long nodeId);

	/**
	 * 更新节点名称
	 * 
	 * @param contextName
	 * @param nodeId
	 * @param nodeName
	 * @return 修改后的节点
	 */
	HashMap<String, Object> updateNodeName(String contextName, long nodeId, String nodeName);

	/**
	 * 创建单个节点
	 * 
	 * @param contextName
	 * @param entity
	 * @return
	 */
	HashMap<String, Object> createNode(String contextName, QAEntityItem entity);

	/**
	 * 批量创建节点和关系
	 * 
	 * @param contextName
	 *            领域
	 * @param sourceName
	 *            源节点
	 * @param relation
	 *            关系
	 * @param targetNames
	 *            目标节点数组
	 * @return
	 */
	HashMap<String, Object> batchCreateNode(String contextName, String sourceName, String relation, String[] targetNames);

	/**
	 * 批量创建下级节点
	 * 
	 * @param contextName
	 *            领域
	 * @param sourceId
	 *            源节点id
	 * @param entityType
	 *            节点类型
	 * @param targetNames
	 *            目标节点名称数组
	 * @param relation
	 *            关系
	 * @return
	 */
	HashMap<String, Object> batchCreateChildNode(String contextName, String sourceId, Integer entityType,
			String[] targetNames, String relation);

	/**
	 * 批量创建同级节点
	 * 
	 * @param contextName
	 *            领域
	 * @param entityType
	 *            节点类型
	 * @param sourceNames
	 *            节点名称
	 * @return
	 */
	List<HashMap<String, Object>> batchCreateSameNode(String contextName, Integer entityType, String[] sourceNames);

	/**
	 * 添加关系
	 * 
	 * @param contextName
	 *            领域
	 * @param sourceId
	 *            源节点id
	 * @param targetId
	 *            目标节点id
	 * @param ship
	 *            关系
	 * @return
	 */
	HashMap<String, Object> createLink(String contextName, long sourceId, long targetId, String ship);
	HashMap<String, Object> createLinkByUuid(String domain, long sourceId, long targetId, String ship);

	/**
	 * 更新关系
	 * 
	 * @param contextName
	 *            领域
	 * @param shipId
	 *            关系id
	 * @param shipName
	 *            关系名称
	 * @return
	 */
	HashMap<String, Object> updateLink(String contextName, long shipId, String shipName);

	/**
	 * 删除节点(先删除关系再删除节点)
	 * 
	 * @param contextName
	 * @param nodeId
	 * @return
	 */
	List<HashMap<String, Object>> deleteNode(String contextName, long nodeId);

	/**
	 * 删除关系
	 * 
	 * @param contextName
	 * @param shipId
	 */
	void deleteLink(String contextName, long shipId);

	/**
	 * 段落识别出的三元组生成图谱
	 * 
	 * @param contextName
	 * @param entityType
	 * @param operateType
	 * @param sourceId
	 * @param rss
	 *            关系三元组
	 *            [[startname;ship;endname],[startname1;ship1;endname1],[startname2;ship2;endname2]]
	 * @return node relationship
	 */
	HashMap<String, Object> createGraphByText(String contextName, Integer entityType, Integer operateType, Integer sourceId,
			String[] rss);
	/**
	 * 批量创建节点，关系
	 * @param contextName
	 * @param params 三元组 sourcenode,relationship,targetnode
	 */
	void batchCreateGraph(String contextName, List<Map<String,Object>> params);
	/**
	 * 更新节点有无附件
	 * @param contextName
	 * @param nodeId
	 * @param status
	 */
	void updateNodeFileStatus(String contextName,long nodeId, int status);
	/**
	 * 更新图谱节点的图片
	 * @param domain
	 * @param nodeId
	 * @param img
	 */
	void updateNodeImg(String domain, long nodeId, String img);

	/**
	 * 移除节点图片
	 * @param domain
	 * @param nodeId
	 */
	void removeNodeImg(String domain, long nodeId);
	/**
	 * 导入csv
	 * @param contextName
	 * @param csvUrl
	 * @param status
	 */
	void batchInsertByCSV(String contextName, String csvUrl, int status) ;
	void updateCorrdOfNode(String contextName, String uuid, Double fx, Double fy);

	HashMap<String, Object> getNodeAttrById(String contextName, int nodeId);
}
