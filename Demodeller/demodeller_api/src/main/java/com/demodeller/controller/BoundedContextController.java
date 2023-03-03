package com.demodeller.controller;

import com.alibaba.fastjson.JSONObject;
import com.demodeller.base.util.GraphPageRecord;
import com.demodeller.base.util.StringUtil;
import com.demodeller.common.R;
import com.demodeller.entity.*;
import com.demodeller.query.GraphQuery;
import com.demodeller.service.IBoundedContextService;
import com.demodeller.service.IKGGraphService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
public class BoundedContextController {
    @Autowired
    private IKGGraphService KGGraphService;
    @Autowired
    private IBoundedContextService boundedContextService;

    @ResponseBody
    @RequestMapping(value = "/loadContext", method = {RequestMethod.GET}) // call db.labels
    @ApiOperation("获取某个领域的上下文")
    public R<GraphPageRecord<Map<String, Object>>> loadContext(GraphQuery queryItem, int domainId) {
        R<GraphPageRecord<Map<String, Object>>> result = new R<GraphPageRecord<Map<String, Object>>>();
        GraphPageRecord<Map<String, Object>> resultRecord = new GraphPageRecord<Map<String, Object>>();
        try {
            String userName = "tc";
            PageHelper.startPage(queryItem.getPageIndex(), queryItem.getPageSize(), true);
            List<Map<String, Object>> domainList = boundedContextService.loadContext(queryItem.getContextName(), userName, domainId);
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(domainList);
            System.out.println("==============================");
            System.out.println(pageInfo.getList());
            System.out.println("==============================");
            long total = pageInfo.getTotal();
            resultRecord.setPageIndex(queryItem.getPageIndex());
            resultRecord.setPageSize(queryItem.getPageSize());
            resultRecord.setTotalCount(new Long(total).intValue());
            resultRecord.setNodeList(pageInfo.getList());
            result.code = 200;
            result.setData(resultRecord);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getContextNodes", method = {RequestMethod.POST})
    @ApiOperation("获取某个上下文的节点列表")
    public R<List<HashMap<String, Object>>> getNodesOfContext(String contextName) {
        R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
        try {
            List<HashMap<String, Object>> graphNodes = KGGraphService.getNodesOfContext(contextName);
            System.out.println(graphNodes);
            result.code = 200;
            result.data = graphNodes;

        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getContextGraph", method = {RequestMethod.POST})
    @ApiOperation("查询某个上下文的节点和关系")
    public R<HashMap<String, Object>> getGraphOfContext(GraphQuery query) {
        R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
        try {
            HashMap<String, Object> graphData = KGGraphService.getGraphOfContext(query);
            result.code = 200;
            result.data = graphData;
            System.out.println(graphData);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getOthersContextsById", method = {RequestMethod.POST})
    @ApiOperation("获取除自己外的上下文")
    public R<List<Map<String, Object>>> getOthersContextsById(int domainId, int contextId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> contextList = boundedContextService.getContexts(domainId);
            for (Map<String, Object> context : contextList) {
                if (context.get("id").equals(contextId)) {
                    contextList.remove(context);
                    break;
                }
            }
            result.setCode(200);
            result.setData(contextList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getContexts", method = {RequestMethod.POST})
    @ApiOperation("获取所有上下文")
    public R<List<Map<String, Object>>> getContexts(int domainId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> domainList = boundedContextService.getContexts(domainId);
            result.setCode(200);
            result.setData(domainList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getContextById", method = {RequestMethod.POST})
    @ApiOperation("根据Id获取上下文")
    public R<Map<String, Object>> getContextById(int contextId) {
        R<Map<String, Object>> result = new R<Map<String, Object>>();
        try {
            List<Map<String,Object>> contextList = boundedContextService.getContextById(contextId);
            Map<String,Object> context = contextList.get(0);
            result.setCode(200);
            result.setData(context);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateContext", method = {RequestMethod.POST})
    @ApiOperation("更新上下文")
    public R<String> updateContext(@RequestBody BoundedContext boundedContext) {
        R<String> result = new R<String>();
        System.out.println(boundedContext);
        try {
            String oldName = (String) getContextById(boundedContext.getId()).getData().get("name");
            KGGraphService.updateContextName(oldName, boundedContext.getName());

            boundedContextService.updateContext(boundedContext);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/createContext", method = {RequestMethod.POST})
    @ApiOperation("创建上下文")
    public R<String> createContext(String contextName, Integer domainId) {
        R<String> result = new R<String>();
        try {
            if (!StringUtil.isBlank(contextName)) {
                List<Map<String, Object>> domainItem = boundedContextService.getContextByName(contextName);
                if (domainItem.size() > 0) {
                    result.code = 300;
                    result.setMsg("上下文已存在");
                } else {
                    String name = "tc";
                    Map<String, Object> maps = new HashMap<String, Object>();
                    maps.put("name", contextName);
                    maps.put("nodecount", 1);
                    maps.put("shipcount", 0);
                    maps.put("status", 1);
                    maps.put("createuser", name);
                    maps.put("domainId", domainId);
                    boundedContextService.createContext(maps);// 保存到mysql
                    KGGraphService.createContext(contextName);// 保存到图数据
                    result.code = 200;
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
    @RequestMapping(value = "/deleteContext", method = {RequestMethod.POST})
    @ApiOperation("删除上下文")
    public R<List<HashMap<String, Object>>> deleteContext(Integer contextId, String contextName) {
        R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
        try {
            boundedContextService.deleteContext(contextId);
            KGGraphService.deleteKGContext(contextName);
            boundedContextService.deleteWordClassByContextId(contextId);
            boundedContextService.deleteMsgByContextId(contextId);
            result.code = 200;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/createWordClass", method = {RequestMethod.POST})
    @ApiOperation("创建词汇类")
    public R<String> createWordClass(@RequestBody WordClass wordClass) {
        R<String> result = new R<String>();
        System.out.println("word");
        System.out.println(wordClass);
        try {
            boundedContextService.createWordClass(wordClass);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getWordClassList", method = {RequestMethod.POST})
    @ApiOperation("获取词汇类列表")
    public R<List<Map<String, Object>>> getWordClassList(int contextId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> wordList = boundedContextService.getWordClassList(contextId);
            System.out.println(wordList);
            result.setCode(200);
            result.setData(wordList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteWordClass", method = {RequestMethod.POST})
    @ApiOperation("删除词汇类")
    public R<String> deleteWordClass(@RequestBody String RelId) {
        R<String> result = new R<String>();
        Map relIdMap = JSONObject.parseObject(RelId);
        String relId_new = (String) relIdMap.get("RelId");
        try {
            boundedContextService.deleteWordClass(relId_new);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateWordClass", method = {RequestMethod.POST})
    @ApiOperation("更新词汇类")
    public R<String> updateWordClass(@RequestBody WordClass wordClass) {
        R<String> result = new R<String>();
//		System.out.println(word);
        try {
            boundedContextService.updateWordClass(wordClass);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getOtherWordClass", method = {RequestMethod.POST})
    @ApiOperation("获取除自己外的词汇类")
    public R<List<Map<String, Object>>> getOtherWordClass(String classId, int contextId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> wordClassList = boundedContextService.getWordClassList(contextId);
            for (Map<String, Object> wordClass : wordClassList) {
                if (wordClass.get("id").equals(classId)) {
                    wordClassList.remove(wordClass);
                    break;
                }
            }
            result.setCode(200);
            result.setData(wordClassList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/createWordClassAttr", method = {RequestMethod.POST})
    @ApiOperation("创建词汇类属性")
    public R<String> createWordClassAttr(@RequestBody WordClassAttr wordClassAttr) {
        R<String> result = new R<String>();
        try {
            boundedContextService.createWordClassAttr(wordClassAttr);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getWordClassAttrList", method = {RequestMethod.POST})
    @ApiOperation("获取词汇类属性列表")
    public R<List<Map<String, Object>>> getWordClassAttrList(int contextId, String classId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> wordClassAttrList = boundedContextService.getWordClassAttrList(contextId, classId);
            result.setCode(200);
            result.setData(wordClassAttrList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteWordClassAttr", method = {RequestMethod.POST})
    @ApiOperation("删除词汇类属性")
    public R<String> deleteWordClassAttr(@RequestBody String classAttrId) {
        R<String> result = new R<String>();
        Map classAttrIdMap = JSONObject.parseObject(classAttrId);
        String classAttrId_new = (String) classAttrIdMap.get("classAttrId");
        try {
            boundedContextService.deleteWordClassAttr(classAttrId_new);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateWordClassAttr", method = {RequestMethod.POST})
    @ApiOperation("更新词汇类属性")
    public R<String> updateWordClassAttr(@RequestBody WordClassAttr wordClassAttr) {
        R<String> result = new R<String>();
        try {
            boundedContextService.updateWordClassAttr(wordClassAttr);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/createWordRel", method = {RequestMethod.POST})
    @ApiOperation("创建词汇关系")
    public R<String> createWordRel(@RequestBody WordRel wordRel) {
        R<String> result = new R<String>();
        System.out.println("wordRel");
        System.out.println(wordRel);
        try {
            boundedContextService.createWordRel(wordRel);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getWordRelList", method = {RequestMethod.POST})
    @ApiOperation("获取词汇关系列表")
    public R<List<Map<String, Object>>> getWordRelList(int contextId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> wordList = boundedContextService.getWordRelList(contextId);
            System.out.println(wordList);
            result.setCode(200);
            result.setData(wordList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteWordRel", method = {RequestMethod.POST})
    @ApiOperation("删除词汇关系")
    public R<String> deleteWordRel(@RequestBody String relId) {
        R<String> result = new R<String>();
        Map relIdMap = JSONObject.parseObject(relId);
        String relId_new = (String) relIdMap.get("relId");
        try {
            boundedContextService.deleteWordRel(relId_new);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateWordRel", method = {RequestMethod.POST})
    @ApiOperation("更新词汇关系")
    public R<String> updateWordRel(@RequestBody WordRel wordRel) {
        R<String> result = new R<String>();
//		System.out.println(word);
        try {
            boundedContextService.updateWordRel(wordRel);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getOtherWordRel", method = {RequestMethod.POST})
    @ApiOperation("获取除自己外的词汇关系")
    public R<List<Map<String, Object>>> getOtherWordRel(String relId, int contextId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> wordRelList = boundedContextService.getWordRelList(contextId);
            for (Map<String, Object> wordRel : wordRelList) {
                if (wordRel.get("id").equals(relId)) {
                    wordRelList.remove(wordRel);
                    break;
                }
            }
            result.setCode(200);
            result.setData(wordRelList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/getAllMsgOfContext", method = {RequestMethod.POST})
    @ApiOperation("获取某个上下文的消息传递")
    public R<List<Map<String, Object>>> getAllMsgOfContext(int contextId, int domainId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> msgList = boundedContextService.getAllMsgOfContext(contextId, domainId);
//			System.out.println(msgList);
            result.setCode(200);
            result.setData(msgList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getAllMsgOfDomain", method = {RequestMethod.POST})
    @ApiOperation("获取某个领域内所有消息传递")
    public R<List<Map<String, Object>>> getAllMsgOfDomain(int domainId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> msgList = boundedContextService.getAllMsgOfDomain(domainId);
//			System.out.println(msgList);
            result.setCode(200);
            result.setData(msgList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/getAllTriggerActionByContextId", method = {RequestMethod.POST})
    @ApiOperation("获取上下文内所有动作")
    public R<List<Map<String, Object>>> getAllTriggerActionByContextId(int contextId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> triggerActionList = boundedContextService.getAllTriggerActionByContextId(contextId);
            result.setCode(200);
            result.setData(triggerActionList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getAllCauseEventByContextId", method = {RequestMethod.POST})
    @ApiOperation("获取上下文内所有事件")
    public R<List<Map<String, Object>>> getAllCauseEventByContextId(int contextId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> causeEventList = boundedContextService.getAllCauseEventByContextId(contextId);
            result.setCode(200);
            result.setData(causeEventList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/createMsg", method = {RequestMethod.POST})
    @ApiOperation("创建消息")
    public R<String> createMsg(@RequestBody Msg msg) {
        R<String> result = new R<String>();
//		System.out.println(msg);
        try {
            boundedContextService.createMsg(msg);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteMsg", method = {RequestMethod.POST})
    @ApiOperation("删除消息")
    public R<String> deleteMsg(@RequestBody String msgId) {
        R<String> result = new R<String>();
        Map msgIdMap = JSONObject.parseObject(msgId);
        String msgId_new = (String) msgIdMap.get("msgId");
        System.out.println(msgId_new);
        try {
            boundedContextService.deleteMsg(msgId_new);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateMsg", method = {RequestMethod.POST})
    @ApiOperation("更新消息")
    public R<String> updateMsg(@RequestBody Msg msg) {
        R<String> result = new R<String>();
        System.out.println(msg);
        try {
            boundedContextService.updateMsg(msg);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/createUncertainty", method = {RequestMethod.POST})
    @ApiOperation("创建不确定性")
    public R<String> createUncertainty(@RequestBody Uncertainty uncertainty) {
        R<String> result = new R<String>();
        try {
            boundedContextService.createUncertainty(uncertainty);
            result.code = 200;
            result.setMsg("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteUncertainty", method = {RequestMethod.POST})
    @ApiOperation("删除不确定性")
    public R<String> deleteUncertainty(@RequestBody String uncertaintyId) {
        R<String> result = new R<String>();
        Map uncertaintyIdMap = JSONObject.parseObject(uncertaintyId);
        String uncertaintyId_new = (String) uncertaintyIdMap.get("uncertaintyId");
        try {
            boundedContextService.deleteUncertainty(uncertaintyId_new);
            result.code = 200;
            result.setMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/updateUncertainty", method = {RequestMethod.POST})
    @ApiOperation("更新不确定性")
    public R<String> updateUncertainty(@RequestBody Uncertainty uncertainty) {
        R<String> result = new R<String>();
        try {
            boundedContextService.updateUncertainty(uncertainty);
            result.code = 200;
            result.setMsg("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getAllUncertainty", method = {RequestMethod.POST})
    @ApiOperation("获取某个上下文的不确定性")
    public R<List<Map<String, Object>>> getAllUncertainty(int contextId, int domainId) {
        R<List<Map<String, Object>>> result = new R<List<Map<String, Object>>>();
        try {
            List<Map<String, Object>> msgList = boundedContextService.getAllUncertainty(contextId, domainId);
            result.setCode(200);
            result.setData(msgList);
        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }
}
