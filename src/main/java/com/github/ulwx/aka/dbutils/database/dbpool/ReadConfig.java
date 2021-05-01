
package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.Path;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReadConfig {

    private static Logger log = LoggerFactory.getLogger(ReadConfig.class);
    /*连接池名称->属性Map*/
    private ConcurrentHashMap<String, Map<String, String>> properties = new ConcurrentHashMap<>();
    /*连接池名称->SlaveMap,slaveMap的key为<server>的name属性，value为属性Map*/
    private ConcurrentHashMap<String, Map<String,Map<String, String>>> slaveProperites = new ConcurrentHashMap<>();
   //为<setting>元素配置
    private ConcurrentHashMap<String, String> glsettings = new ConcurrentHashMap<>();
    private String dbpoolFileName;
    public static final String DEFAULT="dbpool.xml";
    //连接池名对应dbpool.xml文件名的Map
    public volatile static ConcurrentHashMap<String,Set<String>> poolName2DbPoolFileNameMap=new ConcurrentHashMap<>();
    //dbpool.xml文件名对应的ReadConfig对象Map
    public volatile static ConcurrentHashMap<String,ReadConfig> map = init();
    private ReadConfig(String dbpoolFileName) {
        this.dbpoolFileName=dbpoolFileName;
    }
    public static ReadConfig getInstance(String dbpoolFileName) {
        ReadConfig ret=map.get(dbpoolFileName);
        return ret;
    }
    public static ConcurrentHashMap<String,ReadConfig> init() {
        try {
            ConcurrentHashMap<String,ReadConfig> map = new ConcurrentHashMap<>();
            Resource[] resources=Path.getResourcesLikeAntPathMatch("classpath:*dbpool.xml");
            for(int i=0;i<resources.length;i++){
                String poolXmlFileName=new File(resources[i].getURI()).getName();
                ReadConfig readConfig=new ReadConfig(poolXmlFileName);
                readConfig.parse();
                map.put(poolXmlFileName,readConfig);
                Set<String> set = readConfig.getProperties().keySet();
                for(String poolName:set){
                    Set<String> fileNameSet=poolName2DbPoolFileNameMap.get(poolName);
                    if(fileNameSet==null){
                        fileNameSet=new HashSet<>();
                        poolName2DbPoolFileNameMap.put(poolName,fileNameSet);
                    }else{
                       ///
                    }
                    if(fileNameSet.contains(poolXmlFileName) )throw new DbException(poolXmlFileName+"不能存在相同的数据库连接池名称!!");
                    fileNameSet.add(poolXmlFileName);
                    if(fileNameSet.size()>1){
                        throw new DbException(poolName+"数据库连接池在多个" +
                                "dbpool.xml里定义!!!文件"+poolXmlFileName+","+fileNameSet.iterator().next()+"里重复定义！");
                    }
                }
            }
            return map;
        } catch (IOException e) {
            log.error(e+"",e);
        }
        return null;

    }

    public static Set<String> findDBPoolXmlNames(String dbPoolName){
        return poolName2DbPoolFileNameMap.get(dbPoolName);
    }
    public ConcurrentHashMap<String, Map<String, Map<String, String>>> getSlaveProperites() {
        return slaveProperites;
    }


    public ConcurrentHashMap<String, Map<String, String>> getProperties() {
        return properties;
    }

    public Map<String, String> getGlSettings() {
        return glsettings;
    }

    /**
     * 读配置文件
     *
     * @return
     */
    private void parse() {

        String dbpoolXmlFileName= this.dbpoolFileName;
        ConcurrentHashMap<String, Map<String, String>> maps = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Map<String, Map<String, String>>> slaveMaps = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> settings = new ConcurrentHashMap<>();
        SAXReader reader = null;
        try {
            reader = new SAXReader();
            //读取文件 转换成Document "/dbpool.xml"
            List<InputStream> listInputStreams = Arrays.asList(Path.getResource("/"+dbpoolXmlFileName));
            for (int i = 0; i < listInputStreams.size(); i++) {
                Document doc = reader.read(listInputStreams.get(i));
                //获取根节点元素对象
                Element root = doc.getRootElement();
                List<Node> tableNameRule = root.selectNodes("./setting/table-name-rule");
                if (tableNameRule != null && tableNameRule.size() > 0) {
                    settings.put("table-name-rule", ((Element) tableNameRule.get(0)).getTextTrim());
                }

                List<Node> tableColumRule = root.selectNodes("./setting/table-colum-rule");
                if (tableColumRule != null && tableColumRule.size() > 0) {
                    settings.put("table-colum-rule", ((Element) tableColumRule.get(0)).getTextTrim());
                }

                @SuppressWarnings("unchecked")
                Iterator<Element> iterator = root.elementIterator("dbpool");
                while (iterator.hasNext()) {
                    Element config_el = iterator.next();
                    String configName = config_el.attributeValue("name");
                    if(configName==null){
                        throw new DbException(dbpoolXmlFileName+"里连接池必须指定name属性！");
                    }
                    String type = config_el.attributeValue("type");
                    String ref = config_el.attributeValue("ref");
                    String refClass = config_el.attributeValue("ref-class");
                    String checkTime=config_el.attributeValue("check-time");
                    HashMap<String, String> map = new HashMap<String, String>();
                    maps.put(configName, map);
                    if(type!=null && !type.isEmpty()) {
                        map.put("type", type.trim());
                    }
                    if(ref!=null && !ref.isEmpty()) {
                        map.put("ref", ref.trim());
                    }
                    if(refClass!=null && !refClass.isEmpty()){
                        map.put("ref-class", refClass.trim());
                    }
                    if(checkTime!=null && !checkTime.isEmpty()){
                        map.put("check-time", checkTime.trim());
                    }
                    String tableNameRule2 = StringUtils.trim(config_el.attributeValue("table-name-rule"));
                    String tableColumRule2 = StringUtils.trim(config_el.attributeValue("table-colum-rule"));
                    if (StringUtils.isEmpty(tableNameRule2)) {
                        tableNameRule2 = settings.get("table-name-rule");
                        if (StringUtils.isEmpty(tableNameRule2)) {
                            tableNameRule2="underline_to_camel";
                        }
                    }
                    map.put("table-name-rule", tableNameRule2);
                    if (StringUtils.isEmpty(tableColumRule2)) {
                        tableColumRule2 = settings.get("table-colum-rule");
                        if (StringUtils.isEmpty(tableColumRule2)) {
                            tableColumRule2="underline_to_camel";
                        }
                    }
                    map.put("table-colum-rule", tableColumRule2);

                    @SuppressWarnings("unchecked")
                    Iterator<Element> nList = config_el.elementIterator("property");
                    while (nList.hasNext()) {
                        Element propertye_nn = nList.next();
                        String name = ((Element) propertye_nn).attributeValue("name");
                        String value = ((Element) propertye_nn).getTextTrim();
                        map.put(name, value);

                    }
                    HashMap<String, Map<String, String>> slaveServers = new HashMap<>();
                    Iterator<Node> nSlaveList = config_el.selectNodes(".//server").iterator();
                    while (nSlaveList.hasNext()) {
                        Element server_el = (Element) nSlaveList.next();
                        String serverName = server_el.attributeValue("name");
                        Iterator<Element> nSlavePList = server_el.elementIterator("property");
                        HashMap<String, String> mapp = new HashMap<String, String>();
                        slaveServers.put(serverName, mapp);
                        while (nSlavePList.hasNext()) {
                            Element pro_el = (Element) nSlavePList.next();
                            String name = pro_el.attributeValue("name");
                            String value = pro_el.getTextTrim();
                            mapp.put(name, value);

                        }
                    }
                    slaveMaps.put(configName, slaveServers);
                }
            }

        } catch (Exception e) {
            log.error("读取XML文档异常。。。。", e);
        } finally {

        }
        properties = maps;
        slaveProperites = slaveMaps;
        glsettings = settings;
    }

    /**
     * 测试配置文件
     */
    public void testReadCfg() {

    }

    public static void main(String[] args) {
        // ReadConfig.parse();
        //
        // System.out.println(CollectionUtils.toString(ReadConfig.properties));
        // System.out.println(CollectionUtils.toString(ReadConfig.trans));
        ReadConfig rf = new ReadConfig("dbpool.xml");
        System.out.println(ObjectUtils.toString(rf.properties));
        System.out.println(ObjectUtils.toString(rf.slaveProperites));
    }
}
