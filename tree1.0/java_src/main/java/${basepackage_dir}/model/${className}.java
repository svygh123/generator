<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first> 
package ${basepackage}.model;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "${table.sqlName}")
public class ${className} extends ${className}Base{
	<@generateFields/>
	<@generatePkProperties/>
	<#macro generateFields>
	<#if table.compositeId>
	
	private ${className}Id id;
	<#else>
		<#list table.columns as column>
			<#if column.pk>
			
	private ${column.javaType} ${column.columnNameLower};
	
			</#if>
		</#list>
	</#if>
	</#macro>
	<#macro generatePkProperties>
	<#if table.compositeId>
	@EmbeddedId
	public ${className}Id getId() {
		return this.id;
	}
	
	public void setId(${className}Id id) {
		this.id = id;
	}
	<#else>
		<#list table.columns as column>
			<#if column.pk>

	public void set${column.columnName}(${column.javaType} value) {
		this.${column.columnNameLower} = value;
	}
	
	//@Id @GeneratedValue(generator="system-uuid")
	@Id
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name = "${column.sqlName}", unique = ${column.unique?string}, nullable = ${column.nullable?string}, insertable = true, updatable = true, length = ${column.size})
	public ${column.javaType} get${column.columnName}() {
		return this.${column.columnNameLower};
	}
			</#if>
		</#list>
	</#if>
	
</#macro>
	private ${className} parent;
	private List<${className}> children;
	
		
	
	@Transient
	public ${className} getParent() {
		return parent;
	}
	
	
	public void setParent(${className} parent) {
		this.parent = parent;
	}
	
	@Transient
	public List<${className}> getChildren() {
		return children;
	}
	
	
	public void setChildren(List<${className}> children) {
		this.children = children;
	}	
	public int hashCode() {
		return new HashCodeBuilder()
		<#list table.pkColumns as column>
			<#if !table.compositeId>
			.append(get${column.columnName}())
			</#if>
		</#list>
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof ${className} == false) return false;
		if(this == obj) return true;
		${className} other = (${className})obj;
		return new EqualsBuilder()
			<#list table.pkColumns as column>
				<#if !table.compositeId>
			.append(get${column.columnName}(),other.get${column.columnName}())
				</#if>
			</#list>
			.isEquals();
	}
}