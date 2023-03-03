import requests
import json


# 端口配置
contextRoot = "http://121.250.212.55:8081/"

class getModel:
    param = {"domain": 4, "context": "焊接", "entity": "车模"}

    # 获取模型
    def get_model(self):
        return self.get_model_context(self.param["domain"])

    # 获取领域（的上下文）
    def get_model_context(self, domainId):
        url = contextRoot + "getAllDomainsBySsId"
        data = {"sserviceId": domainId}
        response = requests.post(url, params=data)
        data1_json = json.loads(response.text)['data']
        for d in data1_json:
            if d['name'] == self.param['context']:
                contextName = d['name']
                contextId = d['id']
        return self.get_model_entity(contextName, contextId)

    # 获取上下文（的实体）
    def get_model_entity(self, contextName, contextId):
        url = contextRoot + "getdomainnodes"
        data = {"domain": contextName}
        response = requests.post(url, params=data)
        data2_json = json.loads(response.text)['data']
        for d in data2_json:
            if d['name'] == self.param['entity']:
                entityId = d['value']
        return self.get_model_attr(contextName, contextId, entityId)

    def get_model_attr(self, contextName, contextId, entityId):
        url = contextRoot + "getNodeAttrById"
        data = {"domain": contextName, "nodeid": entityId}
        response_attr = requests.post(url, params=data)

        url_user_attr = contextRoot + "getUserAttrList"
        data_user_attr = {"domainId": contextId, "nodeId": entityId}
        response_user_attr = requests.post(url_user_attr, params=data_user_attr)

        data_json_attr = json.loads(response_attr.text)['data']
        data_json_user_attr = json.loads(response_user_attr.text)['data']

        data_json_attr['userAttr'] = data_json_user_attr
        return data_json_attr
