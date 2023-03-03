package com.demodeller.dal.impl;

import com.demodeller.base.util.GraphPageRecord;
import com.demodeller.base.util.JsonHelper;

import com.demodeller.base.util.StringUtil;
import com.demodeller.dal.IKGraphRepository;
import com.demodeller.entity.QAEntityItem;
import com.demodeller.query.GraphQuery;
import com.demodeller.base.util.Neo4jUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class KGraphRepository implements IKGraphRepository {

	/**
	 * 领域标签分页
	 * 
	 * @param queryItem
	 * @return
	 */
	public GraphPageRecord<HashMap<String, Object>> getPageContext(GraphQuery queryItem) {
		GraphPageRecord<HashMap<String, Object>> resultRecord = new GraphPageRecord<HashMap<String, Object>>();
		try {
			String totalCountQuery = "MATCH (n) RETURN count(distinct labels(n)) as count";
			long totalCount = 0;
			totalCount = Neo4jUtil.getGraphValue(totalCountQuery);
			if (totalCount > 0) {
				int skipCount = (queryItem.getPageIndex() - 1) * queryItem.getPageSize();
				int limitCount = queryItem.getPageSize();
				String contextNameSql = String.format(
						"START n=node(*)  RETURN distinct labels(n) as contextName,count(n) as nodecount order by nodecount desc SKIP %s LIMIT %s",
						skipCount, limitCount);
				List<HashMap<String, Object>> pageList = Neo4jUtil.getGraphNode(contextNameSql);
				resultRecord.setPageIndex(queryItem.getPageIndex());
				resultRecord.setPageSize(queryItem.getPageSize());
				resultRecord.setTotalCount(totalCount);
				resultRecord.setNodeList(pageList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultRecord;
	}

	/**
	 * 删除Neo4j 标签
	 * 
	 * @param contextName
	 */
	public void deleteKGContext(String contextName) {
		try {
			String deleteRelation = String.format("MATCH (n:`%s`) -[r]-(m)  delete r", contextName);
			Neo4jUtil.runCypherSql(deleteRelation);
			String deleteNode = String.format("MATCH (n:`%s`) delete n", contextName);
			Neo4jUtil.runCypherSql(deleteNode);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * 查询上下文的节点和关系
	 * 
	 * @param query
	 * @return node relationship
	 */
	public HashMap<String, Object> getGraphOfContext(GraphQuery query) {
		HashMap<String, Object> nr = new HashMap<String, Object>();
		try {
			String contextName = query.getContextName();
			// MATCH (n:`症状`) -[r]-(m:症状) where r.name='治疗' or r.name='危险因素' return n,m
			if (!StringUtil.isBlank(contextName)) {
				String cqr = "";
				List<String> lis = new ArrayList<String>();
				if (query.getRelation() != null && query.getRelation().length > 0) {
					for (String r : query.getRelation()) {
						String it = String.format("r.name='%s'", r);
						lis.add(it);
					}
					cqr = String.join(" or ", lis);
				}
				String cqWhere = "";
				if (!StringUtil.isBlank(query.getNodeName()) || !StringUtil.isBlank(cqr)) {

					if (!StringUtil.isBlank(query.getNodeName())) {
						if (query.getMatchType() == 1) {
							cqWhere = String.format("where n.name ='%s' ", query.getNodeName());

						} else {
							cqWhere = String.format("where n.name contains('%s')", query.getNodeName());
						}
					}
					String nodeOnly = cqWhere;
					if (!StringUtil.isBlank(cqr)) {
						if (StringUtil.isBlank(cqWhere)) {
							cqWhere = String.format(" where ( %s )", cqr);

						} else {
							cqWhere += String.format(" and ( %s )", cqr);
						}

					}
					// 下边的查询查不到单个没有关系的节点,考虑要不要左箭头
					String nodeSql = String.format("MATCH (n:`%s`) <-[r]->(m) %s return * limit %s", contextName, cqWhere,
							query.getPageSize());
					HashMap<String, Object> graphNode = Neo4jUtil.getGraphNodeAndShip(nodeSql);
					Object node = graphNode.get("node");
					// 没有关系显示则显示节点
					if (node != null) {
						nr.put("node", graphNode.get("node"));
						nr.put("relationship", graphNode.get("relationship"));
					} else {
						String nodecql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", contextName,
								nodeOnly, query.getPageSize());
						List<HashMap<String, Object>> nodeItem = Neo4jUtil.getGraphNode(nodecql);
						nr.put("node", nodeItem);
						nr.put("relationship", new ArrayList<HashMap<String, Object>>());
					}
				} else {
					String nodeSql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", contextName, cqWhere,
							query.getPageSize());
					List<HashMap<String, Object>> graphNode = Neo4jUtil.getGraphNode(nodeSql);
					nr.put("node", graphNode);
					String contextNameSql = String.format("MATCH (n:`%s`)<-[r]-> (m) %s RETURN distinct(r) limit %s", contextName,
							cqWhere, query.getPageSize());// m是否加领域
					List<HashMap<String, Object>> graphRelation = Neo4jUtil.getGraphRelationShip(contextNameSql);
					nr.put("relationship", graphRelation);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nr;
	}

	/**
	 * 获取节点列表
	 * 
	 * @param contextName
	 * @return
	 */
	public List<HashMap<String, Object>> getNodesOfContext(String contextName) {
		List<HashMap<String, Object>> resultItem = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();


		try {
			String contextNameSql = String.format("MATCH (n:`%s`) RETURN n", contextName);
			if (!StringUtil.isBlank(contextName)) {
				ents = Neo4jUtil.getGraphNode(contextNameSql);
				for (HashMap<String, Object> hashMap : ents) {
					HashMap<String, Object> et = new HashMap<String, Object>();
					et.put("name", hashMap.get("name"));
					et.put("value", hashMap.get("uuid"));
					if (et != null) {
						resultItem.add(et);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultItem;
	}

	/**
	 * 获取某个领域指定节点拥有的上下级的节点数
	 * 
	 * @param contextName
	 * @param nodeId
	 * @return long 数值
	 */
	public long getRelationNodeCount(String contextName, long nodeId) {
		long totalCount = 0;
		try {
			if (!StringUtil.isBlank(contextName)) {
				String nodeSql = String.format("MATCH (n:`%s`) <-[r]->(m)  where id(n)=%s return count(m)", contextName,
						nodeId);
				totalCount = Neo4jUtil.getGraphValue(nodeSql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalCount;
	}

	/**
	 * 创建领域,默认创建一个新的节点,给节点附上默认属性
	 * 
	 * @param contextName
	 */
	public void createContext(String contextName) {
		try {
			String cypherSql = String.format(
					"create (n:`%s`{entitytype:0,name:''}) return id(n)", contextName);
			Neo4jUtil.runCypherSql(cypherSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新领域名称
	 *
	 * @param contextName
	 */
	public void updateContextName(String oldName, String contextName) {
		try {
			String cypherSql = String.format(
					"match (n:`%s`) remove n:`%s` set n:`%s`", oldName, oldName, contextName);
			Neo4jUtil.runCypherSql(cypherSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 获取/展开更多节点,找到和该节点有关系的节点
	 * 
	 * @param contextName
	 * @param nodeId
	 * @return
	 */
	public HashMap<String, Object> getMoreRelationNode(String contextName, long nodeId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`) -[r]-(m) where id(n)=%s  return * limit 100", contextName,
					nodeId);
			result = Neo4jUtil.getGraphNodeAndShip(cypherSql);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 更新节点名称
	 * 
	 * @param contextName
	 * @param nodeId
	 * @param nodeName
	 * @return 修改后的节点
	 */
	public HashMap<String, Object> updateNodeName(String contextName, long nodeId, String nodeName) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`) where id(n)=%s set n.name='%s' return n", contextName, nodeId,
					nodeName);
			graphNodeList = Neo4jUtil.getGraphNode(cypherSql);

			if (graphNodeList.size() > 0) {
				return graphNodeList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 创建单个节点
	 * 
	 * @param contextName
	 * @param entity
	 * @return
	 */
	public HashMap<String, Object> createNode(String contextName, QAEntityItem entity) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
		try {
			if (entity.getUuid() != 0) {
				String sqlKeyVal = Neo4jUtil.getkeyvalCyphersql(entity);
				System.out.println("=====================");
				System.out.println(sqlKeyVal);
				String cypherSql = String.format("match (n:`%s`) where id(n)=%s set %s return n", contextName,
						entity.getUuid(), sqlKeyVal);
				graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
				System.out.println(graphNodeList);
				System.out.println("=====================");

			} else {
				entity.setColor("#ff4500");// 默认颜色
				entity.setR(30);// 默认半径
				String propertiesString = Neo4jUtil.getFilterPropertiesJson(JsonHelper.toJSONString(entity));
				String cypherSql = String.format("create (n:`%s` %s) return n", contextName, propertiesString);
				graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
			}
			if (graphNodeList.size() > 0) {
				rss = graphNodeList.get(0);
				return rss;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rss;
	}

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
	public HashMap<String, Object> batchCreateNode(String contextName, String sourceName, String relation,
			String[] targetNames) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:`%s` {name:'%s',color:'#ff4500',r:30}) return n";
			String cypherSql = String.format(cypherSqlFmt, contextName, sourceName);// 概念实体
			List<HashMap<String, Object>> graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
			if (graphNodeList.size() > 0) {
				HashMap<String, Object> sourceNode = graphNodeList.get(0);
				nodes.add(sourceNode);
				String sourceUuid = String.valueOf(sourceNode.get("uuid"));
				for (String tn : targetNames) {
					String targetNodeSql = String.format(cypherSqlFmt, contextName, tn);
					List<HashMap<String, Object>> targetNodeList = Neo4jUtil.getGraphNode(targetNodeSql);
					if (targetNodeList.size() > 0) {
						HashMap<String, Object> targetNode = targetNodeList.get(0);
						nodes.add(targetNode);
						String targetuuid = String.valueOf(targetNode.get("uuid"));
						String rSql = String.format(
								"match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
								contextName, contextName, sourceUuid, targetuuid, relation);
						List<HashMap<String, Object>> rShipList = Neo4jUtil.getGraphRelationShip(rSql);
						ships.addAll(rShipList);
					}

				}
			}
			rss.put("nodes", nodes);
			rss.put("ships", ships);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rss;
	}

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
	public HashMap<String, Object> batchCreateChildNode(String contextName, String sourceId, Integer entityType,
			String[] targetNames, String relation) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:`%s`{name:'%s',color:'#ff4500',r:30}) return n";
			String cypherSql = String.format("match (n:`%s`) where id(n)=%s return n", contextName, sourceId);
			List<HashMap<String, Object>> sourceNodeList = Neo4jUtil.getGraphNode(cypherSql);
			if (sourceNodeList.size() > 0) {
				nodes.addAll(sourceNodeList);
				for (String tn : targetNames) {
					String targetNodeSql = String.format(cypherSqlFmt, contextName, tn);
					List<HashMap<String, Object>> targetNodeList = Neo4jUtil.getGraphNode(targetNodeSql);
					if (targetNodeList.size() > 0) {
						HashMap<String, Object> targetNode = targetNodeList.get(0);
						nodes.add(targetNode);
						String targetUuid = String.valueOf(targetNode.get("uuid"));
						// 创建关系
						String rSql = String.format(
								"match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
								contextName, contextName, sourceId, targetUuid, relation);
						List<HashMap<String, Object>> shipList = Neo4jUtil.getGraphRelationShip(rSql);
						ships.addAll(shipList);
					}
				}
			}
			rss.put("nodes", nodes);
			rss.put("ships", ships);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rss;
	}

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
	public List<HashMap<String, Object>> batchCreateSameNode(String contextName, Integer entityType, String[] sourceNames) {
		List<HashMap<String, Object>> rss = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:`%s`{name:'%s',color:'#ff4500',r:30}) return n";
			for (String tn : sourceNames) {
				String sourceNodeSql = String.format(cypherSqlFmt, contextName, tn, entityType);
				List<HashMap<String, Object>> targetNodeList = Neo4jUtil.getGraphNode(sourceNodeSql);
				rss.addAll(targetNodeList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rss;
	}

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
	public HashMap<String, Object> createLink(String contextName, long sourceId, long targetId, String ship) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
					+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", contextName, contextName, sourceId, targetId, ship);
			List<HashMap<String, Object>> cypherResult = Neo4jUtil.getGraphRelationShip(cypherSql);
			if (cypherResult.size() > 0) {
				rss = cypherResult.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rss;
	}

	@Override
	public HashMap<String, Object> createLinkByUuid(String contextName, long sourceId, long targetId, String ship) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE n.uuid=%s AND m.uuid = %s "
					+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", contextName, contextName, sourceId, targetId, ship);
			List<HashMap<String, Object>> cypherResult = Neo4jUtil.getGraphRelationShip(cypherSql);
			if (cypherResult.size() > 0) {
				rss = cypherResult.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rss;
	}

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
	public HashMap<String, Object> updateLink(String contextName, long shipId, String shipName) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`) -[r]->(m) where id(r)=%s set r.name='%s' return r", contextName,
					shipId, shipName);
			List<HashMap<String, Object>> cypherResult = Neo4jUtil.getGraphRelationShip(cypherSql);
			if (cypherResult.size() > 0) {
				rss = cypherResult.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rss;
	}

	/**
	 * 删除节点(先删除关系再删除节点)
	 * 
	 * @param contextName
	 * @param nodeId
	 * @return
	 */
	public List<HashMap<String, Object>> deleteNode(String contextName, long nodeId) {
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		try {
			String nSql = String.format("MATCH (n:`%s`)  where id(n)=%s return n", contextName, nodeId);
			result = Neo4jUtil.getGraphNode(nSql);
			String rSql = String.format("MATCH (n:`%s`) <-[r]->(m) where id(n)=%s return r", contextName, nodeId);
			Neo4jUtil.getGraphRelationShip(rSql);
			String deleteRelationSql = String.format("MATCH (n:`%s`) <-[r]->(m) where id(n)=%s delete r", contextName, nodeId);
			Neo4jUtil.runCypherSql(deleteRelationSql);
			String deleteNodeSql = String.format("MATCH (n:`%s`) where id(n)=%s delete n", contextName, nodeId);
			Neo4jUtil.runCypherSql(deleteNodeSql);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除关系
	 * 
	 * @param contextName
	 * @param shipId
	 */
	public void deleteLink(String contextName, long shipId) {
		try {
			String cypherSql = String.format("MATCH (n:`%s`) -[r]- (m) where id(r)=%s delete r", contextName, shipId);
			Neo4jUtil.runCypherSql(cypherSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
	public HashMap<String, Object> createGraphByText(String contextName, Integer entityType, Integer operateType,
			Integer sourceId, String[] rss) {
		HashMap<String, Object> rsList = new HashMap<String, Object>();
		try {
			List<Object> nodeIds = new ArrayList<Object>();
			List<HashMap<String, Object>> nodeList = new ArrayList<HashMap<String, Object>>();
			List<HashMap<String, Object>> shipList = new ArrayList<HashMap<String, Object>>();

			if (rss != null && rss.length > 0) {
				for (String item : rss) {
					String[] ns = item.split(";");
					String nodeStart = ns[0];
					String ship = ns[1];
					String nodeEnd = ns[2];
					String nodeStartSql = String.format("MERGE (n:`%s`{name:'%s',entitytype:'%s'})  return n", contextName,
							nodeStart, entityType);
					String nodeEndSql = String.format("MERGE (n:`%s`{name:'%s',entitytype:'%s'})  return n", contextName,
							nodeEnd, entityType);
					// 创建初始节点
					List<HashMap<String, Object>> startNode = Neo4jUtil.getGraphNode(nodeStartSql);
					// 创建结束节点
					List<HashMap<String, Object>> endNode = Neo4jUtil.getGraphNode(nodeEndSql);
					Object startId = startNode.get(0).get("uuid");
					if (!nodeIds.contains(startId)) {
						nodeIds.add(startId);
						nodeList.addAll(startNode);
					}
					Object endId = endNode.get(0).get("uuid");
					if (!nodeIds.contains(endId)) {
						nodeIds.add(endId);
						nodeList.addAll(endNode);
					}
					if (sourceId != null && sourceId > 0 && operateType == 2) {// 添加下级
						String shipSql = String.format(
								"MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
										+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r",
								contextName, contextName, sourceId, startId, "");
						List<HashMap<String, Object>> shipResult = Neo4jUtil.getGraphRelationShip(shipSql);
						shipList.add(shipResult.get(0));
					}
					String shipSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
							+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", contextName, contextName, startId, endId, ship);
					List<HashMap<String, Object>> shipResult = Neo4jUtil.getGraphRelationShip(shipSql);
					shipList.addAll(shipResult);

				}
				rsList.put("node", nodeList);
				rsList.put("relationship", shipList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rsList;
	}

	public void batchCreateGraph(String contextName, List<Map<String, Object>> params) {
		try {
			if (params != null && params.size() > 0) {
				String nodeStr = Neo4jUtil.getFilterPropertiesJson(JsonHelper.toJSONString(params));
				String nodeCypher = String
						.format("UNWIND %s as row " + " MERGE (n:`%s` {name:row.SourceNode,source:row.Source})"
								+ " MERGE (m:`%s` {name:row.TargetNode,source:row.Source})", nodeStr, contextName, contextName);
				Neo4jUtil.runCypherSql(nodeCypher);
				String relationShipCypher = String.format("UNWIND %s as row " + " MATCH (n:`%s` {name:row.SourceNode})"
						+ " MATCH (m:`%s` {name:row.TargetNode})" + " MERGE (n)-[:RE{name:row.RelationShip}]->(m)",
						nodeStr, contextName, contextName);
				Neo4jUtil.runCypherSql(relationShipCypher);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量导入csv
	 * 
	 * @param contextName
	 * @param csvUrl
	 * @param isCreateIndex
	 */
	public void batchInsertByCSV(String contextName, String csvUrl, int isCreateIndex) {
		String loadNodeCypher1 = null;
		String loadNodeCypher2 = null;
		String addIndexCypher = null;
		addIndexCypher = " CREATE INDEX ON :" + contextName + "(name);";
		loadNodeCypher1 = " USING PERIODIC COMMIT 500 LOAD CSV FROM '" + csvUrl + "' AS line " + " MERGE (:`" + contextName
				+ "` {name:line[0]});";
		loadNodeCypher2 = " USING PERIODIC COMMIT 500 LOAD CSV FROM '" + csvUrl + "' AS line " + " MERGE (:`" + contextName
				+ "` {name:line[1]});";
		// 拼接生产关系导入cypher
		String loadRelCypher = null;
		String type = "RE";
		loadRelCypher = " USING PERIODIC COMMIT 500 LOAD CSV FROM  '" + csvUrl + "' AS line " + " MATCH (m:`" + contextName
				+ "`),(n:`" + contextName + "`) WHERE m.name=line[0] AND n.name=line[1] " + " MERGE (m)-[r:" + type + "]->(n) "
				+ "	SET r.name=line[2];";
		if(isCreateIndex==0){//已经创建索引的不能重新创建
			Neo4jUtil.runCypherSql(addIndexCypher);
		}
		Neo4jUtil.runCypherSql(loadNodeCypher1);
		Neo4jUtil.runCypherSql(loadNodeCypher2);
		Neo4jUtil.runCypherSql(loadRelCypher);
	}

	public void updateNodeFileStatus(String contextName, long nodeId, int status) {
		try {
			String nodeCypher = String.format("match (n:`%s`) where id(n)=%s set n.hasfile=%s ", contextName,nodeId, status);
			Neo4jUtil.runCypherSql(nodeCypher);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateNodeImg(String domain, long nodeId, String img) {
		try {
			String nodeCypher = String.format("match (n:`%s`) where id(n)=%s set n.image='%s' ", domain, nodeId, img);
			Neo4jUtil.runCypherSql(nodeCypher);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void removeNodeImg(String domain, long nodeId) {
		try {
			String nodeCypher = String.format("match (n:`%s`) where id(n)=%s remove n.image ", domain, nodeId);
			Neo4jUtil.runCypherSql(nodeCypher);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateCorrdOfNode(String contextName, String uuid, Double fx, Double fy) {
		String cypher = null;
		if (fx == null && fy==null) {
			cypher = " MATCH (n:" + contextName +") where ID(n)=" + uuid
					+ " set n.fx=null, n.fy=null; ";
		} else {
			if ("0.0".equals(fx.toString()) && "0.0".equals(fy.toString())) {
				cypher = " MATCH (n:" + contextName +") where ID(n)=" + uuid
						+ " set n.fx=null, n.fy=null; ";
			} else {
				cypher = " MATCH (n:" + contextName +") where ID(n)=" + uuid
						+ " set n.fx=" + fx + ", n.fy=" + fy + ";";
			}
		}
		Neo4jUtil.runCypherSql(cypher);
	}

	public HashMap<String, Object> getNodeAttrById(String contextName, int nodeId) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`) where id(n)=%s return n", contextName, nodeId);
			graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
			if (graphNodeList.size() > 0) {
				return graphNodeList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
