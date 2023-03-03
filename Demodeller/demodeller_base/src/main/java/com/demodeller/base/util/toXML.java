package com.demodeller.base.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class toXML {
    public static boolean jsonToXML (String modelJsonStr, String sserviceName) throws Exception{
        //json转xml
        JSONObject jsonObj = new JSONObject(modelJsonStr);
        String xmlString = XML.toString(jsonObj);
        String xmlTo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<ufinterface roottag=\"voucher\" billtype=\"gl\" replace=\"Y\" receiver=\"1112\" sender=\"01\" isexchange=\"Y\" proc=\"add\" operation=\"req\" filename=\"e:\\1.xml\">";

        String filename = "C:\\Users\\shiha\\Desktop\\" + sserviceName + ".xml";
        System.out.println(xmlString);
        Document document = DocumentHelper.parseText(xmlString);
        OutputXml(document, filename);
        return true;
    }

    public static void OutputXml(Document doc,String filename) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        /** 指定XML编码 */
        format.setEncoding("UTF-8");
        /** 将document中的内容写入文件中 */
        XMLWriter writer;
        try {
            writer = new XMLWriter(new FileWriter(new File(filename)), format);
            writer.write(doc);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
