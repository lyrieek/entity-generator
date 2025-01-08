package com.lyrieek.eg.config;

import com.lyrieek.eg.ClassInfo;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RedInk {

	private final Map<String, List<String>> data;
	private ClassLoader classLoader;

	public RedInk(File folder, String filePath) {
		try (InputStream input = new FileInputStream(Paths.get(folder.getPath(), filePath).toFile())) {
			Yaml yaml = new Yaml();
			data = yaml.load(input);
			if (data == null) {
				throw new RuntimeException("no red-ink");
			}
		} catch (Exception e) {
			throw new RuntimeException("no red-ink", e);
		}
	}

	public List<String> get(String key) {
		return data.get(key);
	}

	public boolean isEmpty(String key) {
		return !data.containsKey(key);
	}

	public boolean matchPre(String content) {
		if (isEmpty("_pre")) {
			return false;
		}
		for (String item : data.get("_pre")) {
			if (content.startsWith(item)) {
				return true;
			}
		}
		return false;
	}

	public String getSubClassStr(ClassInfo classInfo) {
		if (classInfo.getSubClass() != null) {
			return classInfo.getSubClass();
		}
		if (classInfo.getTableName().endsWith("_LOG") && data.containsKey("_default_log_sub")) {
			return get("_default_log_sub").get(0);
		}
		if (data.containsKey("_default_sub")) {
			return get("_default_sub").get(0);
		}
		return null;
	}

	public Class<?> getSubClass(ClassInfo classInfo) {
		try {
			if (classInfo.getSubClass() != null) {
				return getClassLoader().loadClass(classInfo.getSubClass());
			}
			if (classInfo.getTableName().endsWith("_LOG") && data.containsKey("_default_log_sub")) {
				return getClassLoader().loadClass(get("_default_log_sub").get(0));
			}
			if (data.containsKey("_default_sub")) {
				return getClassLoader().loadClass(get("_default_sub").get(0));
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return Object.class;
	}

	public String getPackageName() {
		if (isEmpty("_package_name")) {
			return "com";
		}
		return get("_package_name").get(0);
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return Objects.requireNonNullElse(classLoader, ClassLoader.getSystemClassLoader());
	}

	public boolean getGeneratedLoad() {
		return false;
	}
}
