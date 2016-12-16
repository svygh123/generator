<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.service;

import static com.mcit.framework.util.ObjectUtils.isNotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ${basepackage}.model.${className};
import ${basepackage}.query.${className}Query;
import com.mcit.framework.orm.McitHibernateTemplate;
import com.mcit.framework.orm.Page;
import com.mcit.framework.orm.PageRequest;
import com.risinda.erp.common.utils.Common;
import com.risinda.erp.oa.achives.model.AchivesCategory;
@Service
@Transactional
public class ${className}Manager{
	  <#list table.columns as column>
		<#if column.pk>
	private McitHibernateTemplate<${className}, ${column.javaType}> dao;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		dao = new McitHibernateTemplate<${className}, ${column.javaType}>(sessionFactory, ${className}.class);
	}
		</#if>
	</#list>
	
	@Transactional(readOnly=true)
	public Page<${className}> findPage(PageRequest<${className}Query> pageRequest) {
		List<Object> list = new ArrayList<Object>();
		${className}Query query = pageRequest.getFilter();
		StringBuilder hql = new StringBuilder("select t from ${className} t where 1=1 ");
		if (isNotEmpty(query)) {
			// 过滤条件
			<#list table.columns as column>
			<#if column.filter>
			if (isNotEmpty(query.get${column.columnName}())) {
	            hql.append(" and  t.${column.columnNameLower} ${column.filterType} ? ");
	            <#if column.filterType == "like">
	            list.add("%" + query.get${column.columnName}() + "%");
	            <#else>
	            list.add(query.get${column.columnName}());
	            </#if>
	        }
			</#if>
			</#list>
		}
		return dao.find(pageRequest, hql.toString(), list.toArray());
	}

	@Transactional(readOnly=true)
	public List<${className}> findList(${className}Query query) {
		List<Object> list = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder("select t from ${className} t where 1=1 ");
		if (isNotEmpty(query)) {
			//过滤条件
			<#list table.columns as column>
			<#if column.filter>
			if (isNotEmpty(query.get${column.columnName}())) {
				hql.append(" and  t.${column.columnNameLower} ${column.filterType} ? ");
	            <#if column.filterType == "like">
	            list.add("%" + query.get${column.columnName}() + "%");
	            <#else>
	            list.add(query.get${column.columnName}());
	            </#if>
	        }
			</#if>
			</#list>
		}
		hql.append(" order by t.serialNumber )");
		
		List<${className}> listLike = dao.find(hql.toString(), list.toArray());
		
		if (isNotEmpty(query.getName())) {
			if (listLike.size() > 0) {
				Set<String> set = new HashSet<String>();
				for (${className} vo : listLike) {
					set.add("'" + vo.getId() + "'");
					String[] ids = vo.getFullId().split("/");
					for (String s : ids) {
						set.add("'" + s + "'");
					}
				}
				String inStr = Common.arrayToString(set.toArray());
				String newHql = "select t from ${className} t where t.id in(" + inStr + ")";
				
				return dao.find(newHql.toString());
			} else {
				return listLike;
			}
        } else {
        	return listLike;
        }
	}
	
  <#list table.columns as column>
	<#if column.pk>
	@Transactional(readOnly=true)
	public ${className} getById(${column.javaType} id) {
		return dao.get(id);
	}	
	
	public void removeById(${column.javaType} id) {
		dao.delete(id);
	}	
	</#if>
</#list>
	public void save(${className} ${classNameLower}) {
		dao.save(${classNameLower});
	}
	
	public void update(${className} ${classNameLower}) {
		dao.update(${classNameLower});
	}
	
	/**
	 * 验证编码是否存在
	 */
	@Transactional(readOnly=true)
	public Boolean validCode(String code,String id) {
		List<Object> list = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder("select t from ${className} t where t.code = ? ");
		list.add(code);
		if (null != id && !"".equals(id)) {
			hql.append(" and t.id <> ?");
			list.add(id);
		}
		List<${className}> items = dao.find(hql.toString(), list.toArray());
		return items.size() == 0;
	}
}
