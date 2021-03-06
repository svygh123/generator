<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>
<#assign classNameLower = className?uncap_first>
package ${basepackage}.service;

<#list table.columns as column>
<#if column.javaType=="BigDecimal">
import java.math.BigDecimal;
<#break>
</#if>
</#list>
<#list table.columns as column>
<#if column.javaType=="Date">
import java.util.Date;
<#break>
</#if>
</#list>
import java.util.HashMap;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.isoftoon.ld.fx.config.CurrentUserSession;
import com.isoftoon.ld.fx.model.${className};
import com.isoftoon.ld.fx.model.OperationRecord;
import com.isoftoon.ld.fx.model.PrepareMaterial;
import com.isoftoon.ld.fx.service.OperationRecordService;
import com.isoftoon.ld.fx.utils.Actions;
import com.isoftoon.ld.fx.utils.ResponseEntity;
import com.isoftoon.ld.fx.utils.Transformer;
import com.isoftoon.orm.McitHibernateTemplate;
import com.isoftoon.orm.Page;
import com.isoftoon.orm.PageRequest;
import com.isoftoon.utils.Constants;
import com.isoftoon.utils.DateDeserializer;
import com.justep.biz.client.Action;
import com.justep.biz.client.ActionEngine;
import com.justep.biz.client.ActionResult;
import com.justep.biz.client.ActionUtils;
import com.justep.biz.client.data.impl.RowImpl;

public class ${className}Service extends AbstractService {
    McitHibernateTemplate<${className}, String> dao = null;
    McitHibernateTemplate<OperationRecord, String> operationRecordDao = null;
    OperationRecordService operationRecordService = null;

    public ${className}Service() {
        super();
        dao = new McitHibernateTemplate<${className}, String>(${className}.class);
        operationRecordDao = new McitHibernateTemplate<OperationRecord, String>(OperationRecord.class);
        operationRecordService = new OperationRecordService();
    }

    @SuppressWarnings("unchecked")
    public List<${className}> findAll() throws Exception {
        List<${className}> list = dao.createQuery("from ${className}").list();
        return list;
    }

    public Page<${className}> findByPage(PageRequest<${className}> pageRequest) throws Exception {
        Page<${className}> list = dao.find(pageRequest, "select t from ${className} t order by t.createTime desc", new ArrayList<>().toArray());
        return list;
    }

    <#list table.columns as column>
        <#if column.columnNameLower=="name">
    public Page<${className}> findByPage(PageRequest<${className}> pageRequest, String key) throws Exception {
        if (StringUtils.isBlank(key)) {
            return findByPage(pageRequest);
        }
        Page<${className}> list = dao.find(pageRequest, "select t from ${className} t where t.name like '%" + key + "%'",new ArrayList<>().toArray());
        return list;
    }
        </#if>
    </#list>

    public ${className} findById(String id) throws Exception {
        return dao.get(id);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Map> findByIds(Set<String> ids) throws Exception {
        String hql = "select new map(<#list table.columns as column>s.${column.columnNameLower} as ${column.columnNameLower}<#if column_has_next>,</#if></#list>) from ${className} s where s.id in(:ids)";
        return (List<Map>) dao.createQuery(hql).setParameterList("ids", ids).list();
    }

    public Timestamp findLatestUpdateTime() throws Exception {
        Timestamp result = (Timestamp) dao.findUnique("select max(t.updateTime) from ${className} t");
        return result;
    }

    public void save(${className} model) throws IOException {
        dao.save(model);
    }

    public void saveList(List<${className}> ${classNameLower}s) throws Exception {
        dao.saveList(${classNameLower}s);
    }

    public void update(${className} model) throws Exception {
        dao.update(model);
    }

    @SuppressWarnings("rawtypes")
    public void uploadCreated${className}() throws Exception {
        List<OperationRecord> operationRecords = operationRecordService.findByTabNameAndTypeAndNotUploaded(Constants.XXTableName, Constants.CREATE_ACTION);
        if (CollectionUtils.isNotEmpty(operationRecords)) {
            Set<String> ids = Sets.newHashSet();
            for (int i = 0; i < operationRecords.size(); i++) {
                OperationRecord operationRecord = operationRecords.get(i);
                ids.add(operationRecord.getPkValue());
            }

            // 查询离线记录
            List<Map> list = findByIds(ids);

            Action action = Actions.newAction("uploadCreated${className}Action", "${classNameLower}s", list);

            String result = (String) run(action.getName(), action);

            ResponseEntity resp = Transformer.transform(result);

            if (resp.isSuccessful()) {
                Date now = new Date();
                String success = Constants.OPERATE_SUCCEED;
                for (OperationRecord operationRecord : operationRecords) {
                    operationRecord.setSyncTime(now);
                    operationRecord.setSyncResult(success);
                }
                // 离线记录同步到服务器成功
                operationRecordDao.saveList(operationRecords);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void uploadUpdated${className}() throws Exception {
        List<OperationRecord> operationRecords = operationRecordService.findByTabNameAndTypeAndNotUploaded(Constants.XXTableName, Constants.UPDATE_ACTION);
        if (CollectionUtils.isNotEmpty(operationRecords)) {
            Set<String> ids = Sets.newHashSet();
            for (int i = 0; i < operationRecords.size(); i++) {
                OperationRecord operationRecord = operationRecords.get(i);
                ids.add(operationRecord.getPkValue());
            }

            // 查询离线记录
            List<Map> list = findByIds(ids);

            Action action = Actions.newAction("uploadUpdated${className}Action", "${classNameLower}s", list);

            String result = (String) run(action.getName(), action);

            ResponseEntity resp = Transformer.transform(result);

            if (resp.isSuccessful()) {
                Date now = new Date();
                String success = Constants.OPERATE_SUCCEED;
                for (OperationRecord operationRecord : operationRecords) {
                    operationRecord.setSyncTime(now);
                    operationRecord.setSyncResult(success);
                }
                // 离线记录同步到服务器成功
                operationRecordDao.saveList(operationRecords);
            }
        }
    }

    public List<${className}> sync${className}(Timestamp lastModified) throws Exception {
        Map<Object, Object> params = Maps.newHashMap("updateTime", lastModified);
        Action action = Actions.newAction("sync${className}Action", "params", params);
        String result = (String) run(action.getName(), action);
        return Transformer.toList(result, ${className}[].class);
    }

    /*

    // 同步服务器数据到本地////////////////////////////////////////////////////////////////////////////////////////////////
    <action name="sync${className}Action" global="false" procedure="sync${className}Procedure">
        <label language="zh_CN">同步${className}</label>
        <public type="Map" name="params" />
    </action>

    <procedure name="sync${className}Procedure" code-model="/ERP/common/logic/code" code="Client.sync${className}">
        <parameter type="Map" name="params" />
    </procedure>

    <has-action action="sync${className}Action" access-permission="public"></has-action>

    public static Map<String, Object> sync${className}(Map params) {
        return select("${table.sqlName}", params.get("updateTime") == null ? null : (params.get("updateTime").toString().length() == 0 ? null : (Timestamp) params.get("updateTime")), <#if table.sqlName?index_of("MM_") != -1>mmDataModel<#elseif table.sqlName?index_of("PP_") != -1>ppDataModel<#else>mmDataModel</#if>);
    }


    // 上传本地修改的数据到服务器////////////////////////////////////////////////////////////////////////////////////////////////
    <action name="uploadUpdated${className}Action" global="false" procedure="uploadUpdated${className}Procedure">
        <label language="zh_CN">上传：${className}更新记录</label>
        <public type="List" name="${classNameLower}s"></public>
    </action>

    <procedure name="uploadUpdated${className}Procedure" code-model="/ERP/common/logic/code" code="Client.uploadUpdated${className}">
        <parameter name="${classNameLower}s" type="List"/>
    </procedure>

    <has-action action="uploadUpdated${className}Action" access-permission="public"></has-action>

    // 上传：${className}更新记录
    @SuppressWarnings({ "rawtypes", "unchecked"})
    public static Map<String, Object> uploadUpdated${className}(List ${classNameLower}s) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            for (Object obj : ${classNameLower}s) {
                Map vars = (Map) obj;
                String ksql = "UPDATE ${table.sqlName} id SET <#list table.columns as column><#if !column.pk>id.${column.columnNameLower}=:${column.columnNameLower}<#if column_has_next>,</#if></#if></#list> " +
                        " WHERE id=:id";
                KSQL.executeUpdate(ksql, vars, <#if table.sqlName?index_of("MM_") != -1>mmDataModel<#elseif table.sqlName?index_of("PP_") != -1>ppDataModel<#else>mmDataModel</#if>, null);
            }
            result.put("flag", true);
            result.put("msg", "");
            result.put("result", "");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("flag", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }


    // 上传本地新增的数据到服务器////////////////////////////////////////////////////////////////////////////////////////////////
    <action name="uploadCreated${className}Action" global="false" procedure="uploadCreated${className}Procedure">
        <label language="zh_CN">上传：${className}新增记录</label>
        <public type="List" name="${classNameLower}s"></public>
    </action>

    <procedure name="uploadCreated${className}Procedure" code-model="/ERP/common/logic/code" code="Client.uploadCreated${className}">
        <parameter name="${classNameLower}s" type="List"/>
    </procedure>

    <has-action action="uploadCreated${className}Action" access-permission="public"></has-action>

    // 上传：${className}新增记录
    @SuppressWarnings({ "rawtypes", "unchecked"})
    public static Map<String, Object> uploadCreated${className}(List ${classNameLower}s) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            for (Object obj : ${classNameLower}s) {
                Map vars = (Map) obj;
                String ksql = "INSERT INTO ${table.sqlName} id (<#list table.columns as column><#if column.pk>id,<#else>id.${column.columnNameLower}<#if column_has_next>,</#if></#if></#list>) " +
                    " VALUES(<#list table.columns as column>:${column.columnNameLower}<#if column_has_next>,</#if></#list>)";
                KSQL.executeUpdate(ksql, vars, <#if table.sqlName?index_of("MM_") != -1>mmDataModel<#elseif table.sqlName?index_of("PP_") != -1>ppDataModel<#else>mmDataModel</#if>, null);
            }
            result.put("flag", true);
            result.put("msg", "");
            result.put("result", "");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("flag", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }


    // 调用////////////////////////////////////////////////////////////////////////////////////////////////

    // 上传：${className}新增记录
    ${classNameLower}Service.uploadCreated${className}();

    // 上传：${className}更新记录
    ${classNameLower}Service.uploadUpdated${className}();

    // insert语句hql写法
    sql = "insert into ${table.sqlName}(<#list table.columns as column>${column.columnNameLower}<#if column_has_next>,</#if></#list>) values(<#list table.columns as column>:${column.columnNameLower}<#if column_has_next>,</#if></#list>) ";
    session.createSQLQuery(sql)
            <#list table.columns as column>
            <#if !column.isDateTimeColumn>
            .setParameter("${column.columnNameLower}", record.get("${column.columnNameLower}"))
            <#else>
            .setTimestamp("${column.columnNameLower}", Constants.TIMESTAMP_FORMATTER.parse((String) record.get("${column.columnNameLower}")))
            </#if>
            </#list>
           .executeUpdate();
    */

}
