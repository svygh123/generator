<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.query;

import java.io.Serializable;

public class ${className}Query implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;
    
	<#list table.columns as column>
		<#if column.filter>
	private ${column.javaType} ${column.columnNameLower};
		</#if>
	</#list>
	
	<#list table.columns as column>
		<#if column.filter>
	public ${column.javaType} get${column.columnName}() {
		return this.${column.columnNameLower};
	}
	
	public void set${column.columnName}(${column.javaType} value) {
		this.${column.columnNameLower} = value;
	}
		</#if>
	</#list>
}