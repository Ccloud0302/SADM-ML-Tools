package com.demodeller.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;

import com.demodeller.common.R;

import com.demodeller.base.util.*;
import com.demodeller.config.MultipartFileToFile;
import com.demodeller.config.WebAppConfig;
import com.demodeller.entity.Domain;
import com.demodeller.entity.WordClass;
import com.demodeller.query.GraphQuery;
import com.demodeller.service.IDomainService;
import com.demodeller.service.IEntityElementsService;
import com.demodeller.service.IKGGraphService;
import com.demodeller.service.impl.BoundedContextService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping(value = "/")
public class IOController extends BaseController{
    @Autowired
    private BoundedContextService boundedContextService;
    @Autowired
    private IDomainService domainService;
    @Autowired
    private IKGGraphService KGGraphService;
    @Autowired
    private WebAppConfig config;
    @Autowired
    private IEntityElementsService entityElementsService;

    @ResponseBody
    @RequestMapping(value = "/toModel", method = {RequestMethod.POST})
    @ApiOperation("（上下文内）输出模型")
    public R<String> toModel(int domainId) {
        R<String> result = new R<String>();
        HashMap<String, Object> Model = new HashMap<>();
        try {
            //获取某个领域内所有上下文
            System.out.println("######################开始######################");
            List<Map<String, Object>> contextList = boundedContextService.getContexts(domainId);
            Domain domain = domainService.getById(domainId);
            String domainName = domain.getName();
            System.out.println("1.1、================领域名称================");
            System.out.println(domainName);
            System.out.println("1.2、================所有上下文================");
            System.out.println(contextList);

            List<HashMap<String, Object>> newContextList = new ArrayList<>();
            for (Map<String, Object> map : contextList) {
                Integer contextId = (Integer) map.get("id");
                String contextName = (String) map.get("name");
                System.out.println("=========上下文：" + contextName + "========");
                GraphQuery query = new GraphQuery();
                query.setContextName(contextName);
                //获取某个上下文内所有节点细节和关系
                HashMap<String, Object> graphData = KGGraphService.getGraphOfContext(query);
                System.out.println("2、================该上下文所有实体和关系");
                List<Map<String, Object>> nodeList = (List<Map<String, Object>>) graphData.get("node");
                HashMap<String, Object> nodeMap = new HashMap<>();

                System.out.println(nodeList);
                List<Map<String, Object>> relList = (List<Map<String, Object>>) graphData.get("relationship");
                System.out.println(relList);
                //获取某个上下文内所有通信（和其他上下文的联系）
                List<Map<String, Object>> msgList = boundedContextService.getAllMsgOfContext(contextId, domainId);
                System.out.println("3、================该上下文内所有通信");
                System.out.println(msgList);
                //获取某个上下文内所有节点（用来进一步查询节点动作）
                List<HashMap<String, Object>> nodes = KGGraphService.getNodesOfContext(contextName);
                for (Map<String, Object> node : nodeList) {
                    Integer nodeId = Integer.parseInt(node.get("uuid").toString());
                    String nodeName = (String) node.get("code");
                    System.out.println("4.0、================该上下文内节点" + nodeId + "的自定义属性");
                    List<Map<String, Object>> userAttrList = entityElementsService.getUserAttrList(contextId, nodeId);
                    System.out.println(userAttrList);

                    System.out.println("4.1、================该上下文内节点" + nodeId + "的事件");
                    List<Map<String, Object>> actionList = entityElementsService.getEventList(contextId, nodeId);
                    System.out.println(actionList);

//                    System.out.println("4.2、================该上下文内节点" + nodeId + "的指令");
//                    List<Map<String, Object>> orderList = kgservice.getOrderList(domainId, nodeId);
//                    System.out.println(orderList);

                    System.out.println("4.3、================该上下文内节点" + nodeId + "的状态");
                    List<String> modeList = new ArrayList<>();
                    modeList.add("Production");
                    modeList.add("Maintenance");
                    modeList.add("Manual");
                    List<Map<String, Object>> statusList = new ArrayList<>();
                    for (String mode : modeList) {
                        String stateJson = entityElementsService.getStateFlow(contextId, nodeId, mode);
                        if (stateJson != null) {
                            //把状态图中的状态抽取出来放数组里
                            Map<String, List<Map<String, Object>>> stateFlowMap = (Map<String, List<Map<String, Object>>>) JSON.parse(stateJson);
                            System.out.println(stateFlowMap);
                            List<Map<String, Object>> edgeList = new ArrayList<>();
                            List<Map<String, Object>> stateList = new ArrayList<>();
                            for (Map<String, Object> state : stateFlowMap.get("cells")) {
                                if (state.get("shape").equals("rect")) {
                                    if ((Integer) ((Map<String, Object>) state.get("size")).get("width") < 600) {
                                        Map<String, Object> stateMap = new HashMap<>();
                                        stateMap.put("id", state.get("id"));
                                        stateMap.put("name", ((Map<String, Object>) ((Map<String, Object>) state.get("attrs")).get("label")).get("text"));
                                        stateMap.put("des", ((Map<String, Object>) state.get("data")).get("des"));
                                        stateList.add(stateMap);
                                    }
                                } else if (state.get("shape").equals("edge")) {
                                    List<Map<String, Object>> labels = (List<Map<String, Object>>) state.get("labels");
                                    if (labels.get(0).containsKey("attrs")) {
                                        Map<String, Object> edgeMap = new HashMap<>();
                                        edgeMap.put("text", ((Map<String, Object>) ((Map<String, Object>) labels.get(0).get("attrs")).get("label")).get("text"));
                                        edgeMap.put("sourceId", ((Map<String, Object>) state.get("source")).get("cell"));
                                        edgeMap.put("targetId", ((Map<String, Object>) state.get("target")).get("cell"));
                                        edgeList.add(edgeMap);
                                    }
                                }
                            }
                            ;
                            System.out.println(stateList);
                            System.out.println(edgeList);

                            //把命令和状态绑定
                            for (int i = 0; i < stateList.size(); i++) {
                                for (int j = 0; j < edgeList.size(); j++) {
                                    if (edgeList.get(j).get("sourceId").equals(stateList.get(i).get("id"))) {
                                        stateList.get(i).put("postCommand", edgeList.get(j).get("text"));
                                    } else if (edgeList.get(j).get("targetId").equals(stateList.get(i).get("id"))) {
                                        stateList.get(i).put("preCommand", edgeList.get(j).get("text"));
                                    }
                                }
                            }
                            System.out.println(stateList);

                            Map<String, Object> statusMap = new HashMap<>();
                            statusMap.put("Mode", mode);
                            statusMap.put("State", stateList);
                            statusList.add(statusMap);
                            System.out.println(statusList);
                        }
                    }
                    System.out.println("*******节点结束******");

                    //往节点map内添加动作、状态和指令
//                    node.put("指令", orderList);
                    node.put("userAttr", userAttrList);
                    node.put("event", actionList);
                    node.put("state", statusList);
                    nodeMap.put(nodeName.replace(" ", ""), node);
                }

                //整合↓
                //该上下文新的节点List
                List<HashMap<String, Object>> newNodeList = new ArrayList<>();
                newNodeList.add(nodeMap);
//                System.out.println(newNodeList);
                //往上下文map里加通信、关系、实体
                HashMap<String, Object> contextMap = new HashMap<>();
                contextMap.put("communication", msgList);
                contextMap.put("relationship", relList);
                contextMap.put("entityList", newNodeList);
//                System.out.println(domainMap);
                HashMap<String, Object> newDomainMap = new HashMap<>();
                newDomainMap.put("contextName", contextName);
                newDomainMap.put("BoundedContext", contextMap);
                System.out.println(newDomainMap);
                newContextList.add(newDomainMap);
                System.out.println("*****************上下文结束*****************");
            }

            System.out.println("&&&&&&&&&&&&&&&最终模型&&&&&&&&&&&&&&&&&&");
            Model.put("domainName", domainName);
            Model.put("Domain", newContextList);
            HashMap<String, Object> Final = new HashMap<>();
            Final.put("Model", Model);
            System.out.println(Final);
            String jsonString = JSON.toJSONString(Final);
            // 输出json文件
            String path = System.getProperty("user.dir");
            FileUtil.createJsonFile(jsonString, path, domainName);

            //输出xml文件
            String modelJsonStr = JSON.toJSONString(Final);
            //json转xml
            boolean toXml = toXML.jsonToXML(modelJsonStr, domainName);
            if (toXml == true) {
                result.setData(jsonString);
                result.setCode(200);
                result.setMsg("成功输出");
            }


        } catch (Exception e) {
            e.printStackTrace();
            result.code = 500;
            result.setMsg("服务器错误");
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/importgraph", method = {RequestMethod.POST})
    @ApiOperation("导入图谱")
    public JSONObject importgraph(@RequestParam(value = "file", required = true) MultipartFile file,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject res = new JSONObject();
        if (file == null) {
            res.put("code", "500");
            res.put("msg", "请先选择有效的文件");
            return res;
        }
        // 上下文不能为空
        String contextId = request.getParameter("contextId");
        if (StringUtil.isBlank(contextId)) {
            res.put("code", "500");
            res.put("msg", "请先选择领域");
            return res;
        }
        System.out.println("开始导入csv...");
        System.out.println(contextId);
        List<Map<String,Object>> context = boundedContextService.getContextById(Integer.parseInt(contextId));
        String label = (String) context.get(0).get("name");
        System.out.println(label);

        File file1 = MultipartFileToFile.multipartFileToFile(file);

        InputStreamReader reader = new InputStreamReader(new FileInputStream(file1), "GBK");
        BufferedReader bfreader = new BufferedReader(reader);

//		List<Map<String, Object>> dataList = getFormatData(bfreader);
//        System.out.println(dataList);

        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            String line;
            while ((line = bfreader.readLine()) != null) {//包含该行内容的字符串，不包含任何行终止符，如果已到达流末尾，则返回 null
//				System.out.println(line);
                String new_line = line.replace("\"", "");
//				String[] strBuff  = new_line.split("\t");
                String[] strBuff = new_line.split(",");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("sourcenode", strBuff[0]);
                map.put("targetnode", strBuff[1]);
                map.put("relationship", strBuff[2]);
                mapList.add(map);
            }
//			System.out.println(mapList);
            List<List<String>> list = new ArrayList<>();
            for (Map<String, Object> item : mapList) {
                List<String> lst = new ArrayList<>();
                lst.add(item.get("sourcenode").toString());
                lst.add(item.get("targetnode").toString());
                lst.add(item.get("relationship").toString());
                list.add(lst);
            }
            System.out.println(list);

            String savePath = config.getLocation();
            String filename = "tc" + System.currentTimeMillis() + ".csv";
            CSVUtil.createCsvFile(list, savePath, filename);

//			String serverUrl=request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

            String csvUrl = "file:///" + filename;
//          System.out.println(csvUrl);

            KGGraphService.batchInsertByCSV(label, csvUrl, 0);
            System.out.println("开始导入词汇表");
            for (Map<String, Object> itemMap : mapList) {
                System.out.println(itemMap);
                WordClass wordClass1 = new WordClass();
                String sourcenode = (String) itemMap.get("sourcenode");
                wordClass1.setName(sourcenode);
                wordClass1.setContextId(Integer.parseInt(contextId));
                boundedContextService.createWordClass(wordClass1);

                WordClass wordClass2 = new WordClass();
                String targetnode = (String) itemMap.get("targetnode");
                wordClass2.setName(targetnode);
                wordClass2.setContextId(Integer.parseInt(contextId));
                System.out.println(wordClass2);
                boundedContextService.createWordClass(wordClass2);
            }

            res.put("code", 200);
            res.put("message", "success!");
            return res;
        } catch (Exception e) {
            res.put("code", 500);
            res.put("message", "服务器错误!");
        }
        return res;
    }

    @ResponseBody
    @RequestMapping(value = "/exportgraph", method = {RequestMethod.POST})
    @ApiOperation("导出图谱")
    public Map<String,Object> exportGraph(HttpServletRequest request) {
        Map<String,Object> res=new HashMap<>();
        System.out.println(request);
        String label = request.getParameter("context");
        String filePath = config.getLocation();
        String fileName = UUID.randomUUID() + ".csv";
        String fileUrl = filePath + fileName;
        String cypher = String.format(
                "MATCH (n:`%s`) -[r]->(m:`%s`) return n.name as source,m.name as target,r.name as relation", label, label);
        List<HashMap<String, Object>> list = Neo4jUtil.getGraphTable(cypher);
        if(list.size()==0){
            res.put("code", -1);
            res.put("message", "该领域没有任何有关系的实体!");
            return res;
        }
        try {
            FileUtil.createFile(fileUrl);
            CsvWriter csvWriter = new CsvWriter(fileUrl, ',', StandardCharsets.UTF_8);
            String[] header = {"source", "target", "relation"};
            csvWriter.writeRecord(header);
            for (HashMap<String, Object> hashMap : list) {
                int colSize = hashMap.size();
                String[] cntArr = new String[colSize];
                cntArr[0] = hashMap.get("source").toString().replace("\"", "");
                cntArr[1] = hashMap.get("target").toString().replace("\"", "");
                cntArr[2] = hashMap.get("relation").toString().replace("\"", "");
                try {
                    csvWriter.writeRecord(cntArr);
                } catch (IOException e) {
                    log.error("CSVUtil->createFile: 文件输出异常" + e.getMessage());
                }
            }
            csvWriter.close();
            String serverUrl = request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            String csvUrl = "/file/download/" + fileName;

            res.put("code", 200);
            res.put("fileName", csvUrl);
            res.put("message", "success!");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

}
