package com.lyrieek.eg;

import com.lyrieek.eg.ibatis.FieldInfo;

import java.util.List;

public class ClassInfo {
	private String tableName;
	private String seq;
	private String subClass;
	private List<FieldInfo> fields;

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}

	public String getTableName() {
		return this.tableName;
	}

	public List<FieldInfo> getFields() {
		return fields;
	}

	public String getFullName(String packageName) {
		return packageName + "." + TextProcessor.pascal(tableName);
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getSubClass() {
		return subClass;
	}

	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}
}
