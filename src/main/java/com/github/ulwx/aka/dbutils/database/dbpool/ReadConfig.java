
package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.tool.support.Path;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import com.github.ulwx.aka.dbutils.tool.support.path.UrlResourceUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReadConfig {

    private static final Logger log = LoggerFactory.getLogger(ReadConfig.class);

    /*连接池名称->属性Map*/
    private volatile ConcurrentHashMap<String, Map<String, String>> dbpoolProperties = new ConcurrentHashMap<>();
    /*连接池名称->SlaveMap,slaveMap的key为<server>的name属性，value为属性Map*/
    private volatile ConcurrentHashMap<String, Map<String, Map<String, String>>> dbpoolSlaveProperites = new ConcurrentHashMap<>();
    /*事务管理器配置，key为type, value为属性Map*/
    private volatile ConcurrentHashMap<String, Map<String, String>> transactionManagersProperties=new ConcurrentHashMap<>();
    //为<setting>元素配置
    private volatile ConcurrentHashMap<String, String> glsettings = new ConcurrentHashMap<>();
    private volatile String poolXmlFilePathName;
    private volatile String dbpoolFileName;
    private volatile Resource resource;
    public static final String DEFAULT = "dbpool.xml";
    //dbpool.xml文件名->ReadConfig对象的Map
    public volatile static ConcurrentHashMap<String, ReadConfig> map = new ConcurrentHashMap<String, ReadConfig>();

    public String getPoolXmlFilePathName() {
        return poolXmlFilePathName;
    }

    public String getDbpoolFileName() {
        return dbpoolFileName;
    }

    public void setDbpoolFileName(String dbpoolFileName) {
        this.dbpoolFileName = dbpoolFileName;
    }

    private ReadConfig(String poolXmlFilePathName) {
        this.poolXmlFilePathName = poolXmlFilePathName;
        try {
            this.resource = UrlResourceUtils.newURLResource(poolXmlFilePathName);
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    private ReadConfig(Resource resource) {
        try {
            this.resource = resource;
            this.dbpoolFileName = resource.getURL().toString();
        } catch (Exception e) {
            throw new DbException(e);
        }
    }

    public static ReadConfig getInstance(String dbpoolFileName)  {
        try {
            ReadConfig ret = map.get(dbpoolFileName);
            if (ret == null) {
                synchronized (ReadConfig.class) {
                    ret = map.get(dbpoolFileName);
                    if (ret == null) {
                        init(dbpoolFileName);
                        ret = map.get(dbpoolFileName);
                    }
                }
            }
            return ret;
        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException(e);
        }
    }


    private static volatile Map<String, Resource[]> dbpoolFileNameResourceCache = new ConcurrentHashMap<>();

    /**
     * 根据指定的路径找到资源，可以指定如：<code><file:/dbpool.xml，classpath:mysql/dbpool.xml，classpath*:mysql/dbpool.xml</code>的路径。
     * </code><br/>
     *
     * @param dbpoolFileName
     * <p>可以指定如：<code><file:/dbpool.xml，classpath:mysql/dbpool.xml，classpath*:mysql/dbpool.xml</code>等样式的路径。</p>
     * <p>如果没有指定<code>"file:"， "classpath:"，"classpath*:"</code> 前缀，则系统会默认添加classpath*:前缀。</p>
     * <p>
     * 例如：
     * <ul><li><code>/mysql/dbpool.xml</code>，则为<code>classpath*:/mysql/dbpool.xml</code>；</li>
     * <li>如果为<code>mysql/dbpool.xml</code>，则为<code>classpath*:mysql/dbpool.xml</code>。
     * <br/>注意：<code>/mysql/dbpool.xml</code>和 <code>mysql/dbpool.xml </code>效果相同。</li>
     *
     *</ul>
     * </p>
     * @return
     * @throws Exception
     */
    public static Resource[] getResource(String dbpoolFileName) throws Exception {
        Resource[] resources = dbpoolFileNameResourceCache.get(dbpoolFileName);
        if (resources != null) return resources;
        synchronized (ReadConfig.class) {
            resources = dbpoolFileNameResourceCache.get(dbpoolFileName);
            if (resources == null) {
                resources=Path.getResources(dbpoolFileName);
                if (resources != null) {
                    dbpoolFileNameResourceCache.put(dbpoolFileName, resources);
                } else {
                    dbpoolFileNameResourceCache.put(dbpoolFileName, new Resource[0]);
                }
            }
        }
        return resources;
    }

    public static void checkResource(Resource[] resources, String dbpoolFileName)  {
        try {
            Path.checkResource(resources,dbpoolFileName);
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }
    }

    private static ConcurrentHashMap<String, ReadConfig> init(String dbpoolFileName) {
        try {
            Resource[] resources = getResource(dbpoolFileName);
            checkResource(resources, dbpoolFileName);
            String poolXmlFilePathName = resources[0].getURL().toString();
            ReadConfig readConfig = new ReadConfig(poolXmlFilePathName);
            readConfig.parse();
            readConfig.setDbpoolFileName(dbpoolFileName);
            map.put(dbpoolFileName, readConfig);
            return map;
        } catch (Exception ex) {
            if (ex instanceof DbException) throw (DbException) ex;
            throw new DbException(ex);
        }

    }

    public ConcurrentHashMap<String, Map<String, Map<String, String>>> getDbpoolSlaveProperites() {
        return dbpoolSlaveProperites;
    }

    public ConcurrentHashMap<String, Map<String, String>> getTransactionManagersProperties() {
        return transactionManagersProperties;
    }

    public ConcurrentHashMap<String, Map<String, String>> getDbpoolProperties() {
        return dbpoolProperties;
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

        String dbpoolXmlFileName = this.dbpoolFileName;
        ConcurrentHashMap<String, Map<String, String>> maps = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Map<String, Map<String, String>>> slaveMaps = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> settings = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Map<String, String>> transactionManagersMap= new ConcurrentHashMap<>();
        SAXReader reader = null;
        InputStream inputStream = null;
        try {
            reader = new SAXReader();
            //读取文件 转换成Document "/dbpool.xml"
            inputStream = this.resource.getInputStream();
            Document doc = reader.read(inputStream);
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

            List<Node> transactionManagerNodes = root.selectNodes("./transaction-managers/transaction-manager");
            for(int i = 0; i<transactionManagerNodes.size(); i++){
                Element element = (Element) transactionManagerNodes.get(i);
                Map map =  new HashMap<String, String>();
                String name = element.attributeValue("name");
                map.put("name",name);
                String type = element.attributeValue("type");
                map.put("type",type);
                String enable = element.attributeValue("enable");
                map.put("enable",enable);
                @SuppressWarnings("unchecked")
                Iterator<Element> nList = element.elementIterator("property");
                while (nList.hasNext()) {
                    Element propertye_nn = nList.next();
                    String _name = ((Element) propertye_nn).attributeValue("name");
                    String _value = ((Element) propertye_nn).getTextTrim();
                    map.put(_name, _value);

                }
                transactionManagersMap.put(type,map);
            }

            @SuppressWarnings("unchecked")
            Iterator<Element> iterator = root.elementIterator("dbpool");
            while (iterator.hasNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                Element config_el = iterator.next();
                String configName = config_el.attributeValue("name");
                if (configName == null) {
                    throw new DbException(dbpoolXmlFileName + "里连接池必须指定name属性！");
                }
                if (configName != null && !configName.isEmpty()) {
                    map.put("name", configName.trim());
                }
                String type = config_el.attributeValue("type");
                String ref = config_el.attributeValue("ref");
                String refClass = config_el.attributeValue("ref-class");
                String checkTime = config_el.attributeValue("check-time");

                maps.put(configName, map);
                if (type != null && !type.isEmpty()) {
                    map.put("type", type.trim());
                }
                if (ref != null && !ref.isEmpty()) {
                    map.put("ref", ref.trim());
                }
                if (refClass != null && !refClass.isEmpty()) {
                    map.put("ref-class", refClass.trim());
                }
                if (checkTime != null && !checkTime.isEmpty()) {
                    map.put("check-time", checkTime.trim());
                }
                String tableNameRule2 = StringUtils.trim(config_el.attributeValue("table-name-rule"));
                String tableColumRule2 = StringUtils.trim(config_el.attributeValue("table-colum-rule"));
                if (StringUtils.isEmpty(tableNameRule2)) {
                    tableNameRule2 = settings.get("table-name-rule");
                    if (StringUtils.isEmpty(tableNameRule2)) {
                        tableNameRule2 = "underline_to_camel";
                    }
                }
                map.put("table-name-rule", tableNameRule2);
                if (StringUtils.isEmpty(tableColumRule2)) {
                    tableColumRule2 = settings.get("table-colum-rule");
                    if (StringUtils.isEmpty(tableColumRule2)) {
                        tableColumRule2 = "underline_to_camel";
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


        } catch (Exception e) {
            log.error("读取XML文档异常。。。。", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("" + e);
                }
            }
        }
        this.dbpoolProperties = maps;
        this.dbpoolSlaveProperites = slaveMaps;
        this.glsettings = settings;
        this.transactionManagersProperties=transactionManagersMap;
    }



    public static void main(String[] args)throws Exception {
        // ReadConfig.parse();
        //
        // System.out.println(CollectionUtils.toString(ReadConfig.properties));
        // System.out.println(CollectionUtils.toString(ReadConfig.trans));
        // ReadConfig rf = new ReadConfig("mysql/dbpool.xml");
        // System.out.println(ObjectUtils.toString(rf.properties));
        // System.out.println(ObjectUtils.toString(rf.slaveProperites));
        Resource[] rs= Path.getResourcesLikeAntPathMatch("classpath:mysql/dbpool.xml");
        int i=0;
    }
}
