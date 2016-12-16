<#include "/actionscript_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
<#assign underscoreName = table.underscoreName/>
<#assign classNameLowerCase = className?lower_case/>

package ${basepackage}.model {
	
	import com.mcit.framework.orm.BaseEntity;
	import mx.collections.ArrayCollection;
	[Bindable]
	[RemoteClass(alias = "${basepackage}.model.${className}")]
	public class ${className} extends ${className}Base {
		public var parent : ${className};
		public var children : ArrayCollection;
	}	
}