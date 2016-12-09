<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>
<#assign classNameLower = className?uncap_first>
package ${basepackage}.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.isoftoon.ld.fx.model.${className};
import com.isoftoon.ld.fx.utils.SimpleResult;
import com.isoftoon.orm.McitHibernateTemplate;
import com.isoftoon.orm.Page;
import com.isoftoon.orm.PageRequest;
import com.isoftoon.utils.DateDeserializer;
import com.justep.biz.client.Action;
import com.justep.biz.client.ActionEngine;
import com.justep.biz.client.ActionResult;
import com.justep.biz.client.ActionUtils;

public class ${className}Service extends AbstractService {
    McitHibernateTemplate<${className}, String> dao = null;

    public ${className}Service() {
        super();
        dao = new McitHibernateTemplate<${className}, String>(${className}.class);
    }

    public List<${className}> findAll() {
        @SuppressWarnings("unchecked")
        List<${className}> list = dao.createQuery("from ${className}").list();
        return list;
    }

    public Page<${className}> findByPage(PageRequest<${className}> pageRequest) {
        Page<${className}> list = dao.find(pageRequest, "select t from ${className} t", new ArrayList<>().toArray());
        return list;
    }

    <#list table.columns as column>
        <#if column.columnNameLower=="name">
    public Page<${className}> findByPage(PageRequest<${className}> pageRequest, String key) {
        if (key==null || "".equals(key)) {
            return findByPage(pageRequest);
        }
        Page<${className}> list = dao.find(pageRequest, "select t from ${className} t where t.name like '%" + key + "%'",new ArrayList<>().toArray());
        return list;
    }
        </#if>
    </#list>

    public void save(${className} model) throws IOException {
        dao.save(model);
    }

    public void update(${className} model) throws IOException {
        dao.save(model);
    }

    public ${className} findById(String id) {
        return dao.get(id);
    }

    public Timestamp findByMaxTime() {
        Timestamp result = (Timestamp) dao.findUnique("select max(t.updateTime) from ${className} t");
        return result;
    }

    @Override
    public Object excute(String sessionId, Object... parames) {
        String action = (String) parames[0];

        if ("create".equals(action)) {
            return create(sessionId, (${className}) parames[1]);
        } else if ("query".equals(action)) {
            return query(sessionId, parames[1]);
        } else if ("update".equals(action)) {
            return update(sessionId, (${className}) parames[1]);
        }
        return null;
    }

    public List<${className}> queryUpdateTime(Timestamp obj) {
        String result = (String) run("query", obj);

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();
        SimpleResult gist = gson.fromJson(result, SimpleResult.class);

        return null == gist ? null : gson.fromJson(gson.toJson(gist.getResult()), new TypeToken<List<${className}>>() {
        }.getType());
    }

    private String query(String sessionId, Object updateTime) {
        Action action = new Action();
        // 指定动作的process、activity和action，这里要注意登录的用户应该有执行这个功能中的这个动作的权限
        action.setProcess("/ERP/common/process/BaseCode/baseCodeProcess");
        action.setActivity("clientActivity");
        action.setName("sync${className}Action");
        action.setParameter("updateTime", updateTime);

        ActionResult actionResult = ActionEngine.invokeAction(action, ActionUtils.JSON_CONTENT_TYPE, sessionId, null, null);

        // 判断是否调用成功
        if (actionResult.isSuccess()) {
            // 返回值
            JSONObject json = (JSONObject) actionResult.getDatas().get(0);
            JSONObject data = json.getJSONObject("value");

            logger.debug(data.toJSONString());
            return data.toJSONString();
        } else {
            throw new RuntimeException(actionResult.getMessage());
        }
    }

    private String create(String sessionId, ${className} bean) {
        try {
            Action action = new Action();
            // 指定动作的process、activity和action，这里要注意登录的用户应该有执行这个功能中的这个动作的权限
            action.setProcess("/ERP/common/process/BaseCode/baseCodeProcess");
            action.setActivity("clientActivity");
            action.setName("create${className}Action");

            <#list table.columns as column>
                <#if column.isDateTimeColumn>
            action.setParameter("${column.columnNameLower}", bean.get${column.columnName}()!=null?new Timestamp(bean.get${column.columnName}().getTime()));
                <#elseif column.javaType=="Long">
            action.setParameter("${column.columnNameLower}", bean.get${column.columnName}()!=null?new BigDecimal(bean.get${column.columnName}()):null);
                <#else>
            action.setParameter("${column.columnNameLower}", bean.get${column.columnName}());
                </#if>
            </#list>

            ActionResult actionResult = ActionEngine.invokeAction(action, ActionUtils.JSON_CONTENT_TYPE, sessionId, null, null);

            // 判断是否调用成功
            if (actionResult.isSuccess()) {
                // 返回值
                JSONObject json = (JSONObject) actionResult.getDatas().get(0);
                JSONObject data = json.getJSONObject("value");

                logger.debug(data.toJSONString());
                return data.toJSONString();
            } else {
                throw new RuntimeException(actionResult.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String update(String sessionId, ${className} bean) {
        try {
            Action action = new Action();
            // 指定动作的process、activity和action，这里要注意登录的用户应该有执行这个功能中的这个动作的权限
            action.setProcess("/ERP/common/process/BaseCode/baseCodeProcess");
            action.setActivity("clientActivity");
            action.setName("update${className}Action");

            <#list table.columns as column>
                <#if column.isDateTimeColumn>
            action.setParameter("${column.columnNameLower}", new Timestamp(bean.get${column.columnName}().getTime()));
                <#elseif column.javaType=="Long">
            action.setParameter("${column.columnNameLower}", bean.get${column.columnName}()!=null?new BigDecimal(bean.get${column.columnName}()):null);
                <#else>
            action.setParameter("${column.columnNameLower}", bean.get${column.columnName}());
                </#if>
            </#list>

            ActionResult actionResult = ActionEngine.invokeAction(action, ActionUtils.JSON_CONTENT_TYPE, sessionId, null, null);

            // 判断是否调用成功
            if (actionResult.isSuccess()) {
                // 返回值
                JSONObject json = (JSONObject) actionResult.getDatas().get(0);
                JSONObject data = json.getJSONObject("value");

                logger.debug(data.toJSONString());
                return data.toJSONString();
            } else {
                throw new RuntimeException(actionResult.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*

    <action name="create${className}Action" global="false" procedure="create${className}Procedure">
        <label language="zh_CN">${table.tableAlias}</label>
        <#list table.columns as column>
            <#if column.isDateTimeColumn>
        <public name="${column.columnNameLower}" type="DateTime"></public>
            <#elseif column.javaType=="Long">
        <public name="${column.columnNameLower}" type="Decimal"></public>
            <#else>
        <public name="${column.columnNameLower}" type="${column.javaType}"></public>
            </#if>
        </#list>
    </action>

    <procedure name="create${className}Procedure" code-model="/ERP/common/logic/code" code="Client.create${className}">
        <#list table.columns as column>
            <#if column.isDateTimeColumn>
        <parameter name="${column.columnNameLower}" type="DateTime"></parameter>
            <#elseif column.javaType=="Long">
        <parameter name="${column.columnNameLower}" type="Decimal"></parameter>
            <#else>
        <parameter name="${column.columnNameLower}" type="${column.javaType}"></parameter>
            </#if>
        </#list>
    </procedure>

    public static Map<String, Object> create${className}(<#list table.columns as column><#if column.isDateTimeColumn>Timestamp ${column.columnNameLower}<#if column_has_next>,</#if><#elseif column.javaType=="Long">BigDecimal ${column.columnNameLower}<#if column_has_next>,</#if><#else>${column.javaType} ${column.columnNameLower}<#if column_has_next>,</#if></#if></#list>) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            HashMap<String,Object> vars = new HashMap<String,Object>();
            <#list table.columns as column>
            vars.put("${column.columnNameLower}", ${column.columnNameLower});
            </#list>
            String ksql = "INSERT INTO ${table.sqlName} id (<#list table.columns as column><#if column.pk>id,<#else>id.${column.columnNameLower}<#if column_has_next>,</#if></#if></#list>) " +
                    " VALUES(<#list table.columns as column>:${column.columnNameLower}<#if column_has_next>,</#if></#list>)";
            KSQL.executeUpdate(ksql, vars, mmDataModel, null);

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

     */

}
