package com.lyrieek.eg.ibatis;

import org.apache.ibatis.type.JdbcType;

public class FieldInfo {
	private String name;
	private Class<?> type;
	private JdbcType jdbcType;
	private Boolean primaryKey;

	public void setName(String name) {
		this.name = name;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean getPrimaryKey() {
		if (primaryKey == null) {
			return false;
		}
		return primaryKey;
	}

	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public JdbcType getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(JdbcType jdbcType) {
		this.jdbcType = jdbcType;
	}
}
