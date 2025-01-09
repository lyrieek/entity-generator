package com.lyrieek.eg.config;

public class DefaultSet {

	private String subClass;
	private String seq;
	private String packageName = "com";

	public String getSubClass() {
		return subClass;
	}

	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	/**
	 * 由于生成了虚假的父类, 不能支持生成后加载类
	 */
	public boolean getGeneratedLoad() {
		return false;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		if (packageName == null) {
			return;
		}
		this.packageName = packageName;
	}
}
