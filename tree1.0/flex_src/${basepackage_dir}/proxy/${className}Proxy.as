<#include "/actionscript_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
<#assign underscoreName = table.underscoreName/>
<#assign classNameFirstLower = className?uncap_first>

package ${basepackage}.proxy {

	import com.jmax.remoting.AbstractRemotingProxy;
	import com.jmax.remoting.IInvokeResponder;
	import com.mcit.framework.orm.PageRequest;
	import ${basepackage}.model.*;
	import ${basepackage}.query.*;

	public class ${className}Proxy extends AbstractRemotingProxy {
		public function ${className}Proxy(){
			 super("${classNameFirstLower}Service");
		}
		
		public function save(${classNameFirstLower} : ${className}) : IInvokeResponder {
			return super.invoke("save", ${classNameFirstLower});
		}
		
		public function update(${classNameFirstLower} : ${className}) : IInvokeResponder {
			return super.invoke("update", ${classNameFirstLower});
		}
		
		public function findPage(pageRequest : PageRequest) : IInvokeResponder {
			return super.invoke("findPage", pageRequest);
		}
		
		public function findList(${classNameFirstLower}Query : ${className}Query=null) : IInvokeResponder {
			return super.invoke("findList", ${classNameFirstLower}Query);
		}
		
		public function del(array : Array) : IInvokeResponder {
			return super.invoke("del", array);
		}
		
		public function getTreeResult() : IInvokeResponder {
			return super.invoke("getTreeResult");
		}
		
		public function validCode(code : String, id : String) : IInvokeResponder {
			return super.invoke("validCode", code, id);
		}
		
		public function findTreeList(${classNameFirstLower}Query : ${className}Query=null) : IInvokeResponder {
			return super.invoke("findTreeList", ${classNameFirstLower}Query);
		}
	}
}