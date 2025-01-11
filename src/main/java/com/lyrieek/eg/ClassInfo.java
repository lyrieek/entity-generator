package com.lyrieek.eg;

import com.lyrieek.eg.ibatis.FieldInfo;

import java.util.List;

public class ClassInfo {
	private String tableName;
	private String seq;
	private boolean isLog;
	private List<FieldInfo> fields;

	public void setTableName(String tableName) {
		this.tableName = tableName;
		this.isLog = tableName.endsWith("_LOG");
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

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean log) {
		isLog = log;
	}
}
