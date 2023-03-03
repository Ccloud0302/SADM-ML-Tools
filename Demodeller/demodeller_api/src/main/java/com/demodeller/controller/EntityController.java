package com.demodeller.controller;

import com.demodeller.base.util.Neo4jUtil;
import com.demodeller.base.util.StringUtil;
import com.demodeller.entity.*;
import com.demodeller.service.IKGGraphService;
import com.demodeller.common.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = "/")
public class EntityController extends BaseController {
    @Autowired
    private IKGGraphService KGGraphService;

    @ResponseBody
    @RequestMapping(value = "/getCypherResult", method = {RequestMethod.POST})
    @ApiOperation("获取cypher结果")
    public R<HashMap<String, Object>> getCypherResult(String cypher) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        String error = "";
        try {
            HashMap<String, Object> graphData = Neo4jUtil.getGraphNodeAndShip(cypher);
            result.code = 200;
            result.data = graphData;
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            error = e.getMessage();
            result.setMsg("服务器错误");
        } finally {
            if (StringUtil.isNotBlank(error)) {
                result.code = 500;
                result.setMsg(error);
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getRelationNodeCount", method = {RequestMethod.POST})
    @ApiOperation("获取关系节点数量")
    public R<String> getRelationNodeCount(String contextName, long nodeId) {
        R<String> result = new R<String>();
        try {
            long totalcount = 0;
            if (!StringUtil.isBlank(contextName)) {
                totalcount = KGGraphService.getRelationNodeCount(contextName, nodeId);
                result.code = 200;
                result.setData(String.valueOf(totalcount));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getMoreRelationNode", method = {RequestMethod.POST})
    @ApiOperation("获取更多关系节点")
    public R<HashMap<String, Object>> getMoreRelationNode(String contextName, long nodeId) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        try {
            if (!StringUtil.isBlank(contextName)) {
                HashMap<String, Object> graphModel = KGGraphService.getMoreRelationNode(contextName, nodeId);
                if (graphModel != null) {
                    result.code = 200;
                    result.setData(graphModel);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateNodeName", method = {RequestMethod.POST})
    @ApiOperation("更新节点名字")
    public R<HashMap<String, Object>> updateNodeName(String contextName, long nodeId, String nodeName) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        HashMap<String, Object> graphNodeList = new HashMap<String, Object>();
        try {
            if (!StringUtil.isBlank(contextName)) {
                graphNodeList = KGGraphService.updateNodeName(contextName, nodeId, nodeName);
                if (graphNodeList.size() > 0) {
                    result.code = 200;
                    result.setData(graphNodeList);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateCorrdOfNode", method = {RequestMethod.POST})
    @ApiOperation("更新相关节点")
    public R<String> updateCorrdOfNode(String contextName, String uuid, Double fx, Double fy) {
        R<String> result = new R<String>();
        try {
            KGGraphService.updateCorrdOfNode(contextName, uuid, fx, fy);
            result.code = 200;
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/createNode", method = {RequestMethod.POST})
    @ApiOperation("创建节点")
    public R<HashMap<String, Object>> createNode(QAEntityItem entity, HttpServletRequest request,
                                                 HttpServletResponse response) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        HashMap<String, Object> graphNode = new HashMap<String, Object>();
        System.out.println("entity----------" + entity);
        try {
            String contextName = request.getParameter("contextName");
            graphNode = KGGraphService.createNode(contextName, entity);
            System.out.println(graphNode.get("r").getClass().getName());
            if (graphNode != null && graphNode.size() > 0) {
                result.code = 200;
                result.setData(graphNode);
                result.setMsg("success");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/batchCreateNode", method = {RequestMethod.POST})
    @ApiOperation("批量创建节点")
    public R<HashMap<String, Object>> batchCreateNode(String contextName, String sourceName, String[] targetNames,
                                                      String relation) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            rss = KGGraphService.batchCreateNode(contextName, sourceName, relation, targetNames);
            result.code = 200;
            result.setData(rss);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/batchCreateChildNode", method = {RequestMethod.POST})
    @ApiOperation("批量创建子节点")
    public R<HashMap<String, Object>> batchCreateChildNode(String contextName, String sourceId, Integer entityType,
                                                           String[] targetNames, String relation) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            rss = KGGraphService.batchCreateChildNode(contextName, sourceId, entityType, targetNames, relation);
            result.code = 200;
            result.setData(rss);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/batchCreateSameNode", method = {RequestMethod.POST})
    @ApiOperation("批量创建相同节点")
    public R<List<HashMap<String, Object>>> batchCreateSameNode(String contextName, Integer entityType,
                                                                String[] sourceNames) {
        R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
        List<HashMap<String, Object>> rss = new ArrayList<HashMap<String, Object>>();
        try {
            rss = KGGraphService.batchCreateSameNode(contextName, entityType, sourceNames);
            result.code = 200;
            result.setData(rss);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/createLink", method = {RequestMethod.POST})
    @ApiOperation("创建关系")
    public R<HashMap<String, Object>> createLink(String contextName, long sourceId, long targetId, String ship) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        try {
            HashMap<String, Object> cypherResult = KGGraphService.createLink(contextName, sourceId, targetId, ship);
            result.code = 200;
            result.setData(cypherResult);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateLink", method = {RequestMethod.POST})
    @ApiOperation("更新关系")
    public R<HashMap<String, Object>> updateLink(String contextName, long shipId, String shipName) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        try {
            HashMap<String, Object> cypherResult = KGGraphService.updateLink(contextName, shipId, shipName);
            result.code = 200;
            result.setData(cypherResult);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteNode", method = {RequestMethod.POST})
    @ApiOperation("删除节点")
    public R<List<HashMap<String, Object>>> deleteNode(String contextName, long nodeId) {
        R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
        try {
            List<HashMap<String, Object>> rList = KGGraphService.deleteNode(contextName, nodeId);
            result.code = 200;
            result.setData(rList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteLink", method = {RequestMethod.POST})
    @ApiOperation("删除关系")
    public R<HashMap<String, Object>> deleteLink(String contextName, long shipId) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        try {
            KGGraphService.deleteLink(contextName, shipId);
            result.code = 200;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

}
