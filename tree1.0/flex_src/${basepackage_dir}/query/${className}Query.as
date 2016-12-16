<#include "/actionscript_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
<#assign underscoreName = table.underscoreName/>
<#assign classNameLowerCase = className?lower_case/>
package ${basepackage}.query {
		
	[Bindable]
	[RemoteClass(alias="${basepackage}.query.${className}Query")]
	public class ${className}Query {
		// columns START
		<#list table.columns as column>
			<#if column.filter>
		public var ${column.columnNameLower} : ${column.asType};
			</#if>
		</#list>		
		// columns END
	}
}