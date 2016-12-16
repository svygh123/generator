<#include "/actionscript_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
<#assign underscoreName = table.underscoreName/>
<#assign classNameLowerCase = className?lower_case/>
	
package ${basepackage}.model{
	
	import com.mcit.framework.orm.BaseEntity;
    import com.mcit.framework.util.DateConvertUtils;
	import com.risinda.erp.common.utils.Common;

	[Bindable]
	public class ${className}Base extends BaseEntity
	{
		// alias
		<#list table.columns as column>
		public static const ALIAS_${column.constantName}:String = "${column.columnAlias}";
		</#list>

		// date formats
		<#list table.columns as column>
			<#if column.isDateTimeColumn>
		<#if column.constantName == "CREATE_TIME">
		public static const FORMAT_${column.constantName} : String = DATE_TIME_FORMAT;
		<#else>
			public static const FORMAT_${column.constantName} : String = DATE_FORMAT;
		</#if>
			</#if>
		</#list>
				
		// columns START
		<#list table.columns as column>
		public var ${column.columnNameLower}:${column.asType};
		</#list>		
		// columns END

		<#list table.columns as column>
			<#if column.isDateTimeColumn>
		[Transient]
		public function get ${column.columnNameLower}String() : String {
			return DateConvertUtils.format(this.${column.columnNameLower},FORMAT_${column.constantName});
		}
		public function set ${column.columnNameLower}String(value:String) : void { return; }
		
			<#elseif column.columnNameLower == "flowState">
		[Transient]
		public function get ${column.columnNameLower}String() : String {
			return Common.getFlowStateString(this.${column.columnNameLower});
		}
		public function set ${column.columnNameLower}String(value:String) : void { return; }
		
			<#elseif column.columnNameLower == "state">
		[Transient]
		public function get ${column.columnNameLower}String() : String {
			return Common.getStateString(this.${column.columnNameLower});
		}
		public function set ${column.columnNameLower}String(value:String) : void { return; }
		
			</#if>
		</#list>		
	}
	
	
}