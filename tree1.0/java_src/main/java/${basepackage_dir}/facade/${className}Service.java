<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 

package ${basepackage}.facade;

import java.util.ArrayList;
import java.util.List;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.flex.remoting.RemotingInclude;
import org.springframework.stereotype.Service;
import ${basepackage}.model.${className};
import ${basepackage}.query.${className}Query;
import ${basepackage}.service.${className}Manager;
import com.mcit.framework.orm.Page;
import com.mcit.framework.orm.PageRequest;

/**
 * @author qinmin
 * 带有@Service和@@RemotingDestination注解并且装载进Spring的bean则会自动导出为BlazeDS的RemoteObject对象
 */
@Service
@RemotingDestination
public class ${className}Service {

	private ${className}Manager ${classNameLower}Manager;
	/**通过spring注入${className}Manager*/
	public void set${className}Manager(${className}Manager ${classNameLower}Manager) {
		this.${classNameLower}Manager = ${classNameLower}Manager;
	}
	
	/**通过PageRequest查询列表*/
	@RemotingInclude
	public Page<${className}> findPage(PageRequest pr) {
		return ${classNameLower}Manager.findPage(pr);
	}

	@RemotingInclude
	public List<${className}> findList(${className}Query  ${classNameLower}Query) {
		return ${classNameLower}Manager.findList( ${classNameLower}Query);
	}
	
	@RemotingInclude
	public  ${className} save(${className} vo) {
		${classNameLower}Manager.save(vo);
		return vo;
	}

	@RemotingInclude
	public  ${className} update(${className} vo) {
		${classNameLower}Manager.update(vo);
		return vo;
	}
	
	@RemotingInclude
	public void del(${table.idColumn.javaType}[] ids) {
		for(${table.idColumn.javaType} id : ids) {
			${classNameLower}Manager.removeById(id);
		}
	}
	
	@RemotingInclude
	public ${className} getById(${table.idColumn.javaType} id) {
		return ${classNameLower}Manager.getById(id);
	}
    //封装树形结构
	@RemotingInclude
	public List<${className}> findTreeList(${className}Query  ${classNameLower}Query) {
		
        List<${className}> list = ${classNameLower}Manager.findList(${classNameLower}Query);
		
		List<${className}> treeNodeList = new ArrayList<${className}>();
		
		for (${className} ${classNameLower} : list) {
			if(${classNameLower}.getParentId().equals("-1")){
				
				List<${className}> subChildNodeList = getChildrenList(list, ${classNameLower});
				
				${classNameLower}.setChildren(subChildNodeList);				
				treeNodeList.add(${classNameLower});
			}
		}
		
		return treeNodeList;
	}
	
	//根据父节点，递归出所有子节点
	public List<${className}> getChildrenList(List<${className}> list, ${className} parentNode) {
		List<${className}> childrens = new ArrayList<${className}>();
		
		for(${className} ${classNameLower}: list){
			if(parentNode.getId().equals(${classNameLower}.getParentId())){
			
				List<${className}> subChildrens = getChildrenList(list, ${classNameLower});
				
				if (subChildrens.isEmpty()){
					${classNameLower}.setChildren(null);
				} else {
					${classNameLower}.setChildren(subChildrens);
				}
				
				
				${classNameLower}.setParent(parentNode);
				
				childrens.add(${classNameLower});
			}
		}
		
		return childrens;
	}
	
	/**
	 * 编码验证
	 * @param code
	 * @param id
	 * @return
	 */
	@RemotingInclude
	public Boolean validCode(String code,String id) {
		return  ${classNameLower}Manager.validCode(code,id);
	}
}
