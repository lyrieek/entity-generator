package com.lyrieek.eg.config;

import com.lyrieek.eg.ClassInfo;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RedInk {

	private final Map<String, List<String>> data;

	public RedInk(String filePath) {
		try (InputStream input = new FileInputStream(Paths.get(
				Objects.requireNonNull(ClassLoader.getSystemClassLoader()
						.getResource(filePath)).toURI()).toFile())) {
			Yaml yaml = new Yaml();
			data = yaml.load(input);
			if (data == null) {
				throw new RuntimeException("no red-ink");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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

	public static void main(String[] args) {
		System.out.println(new RedInk("red-ink.yml").get("_pre"));
	}

	public Class<?> getSubClass(ClassInfo classInfo) {
		try {
			if (classInfo.getSubClass() != null) {
				return Class.forName(classInfo.getSubClass());
			}
			if (classInfo.getTableName().endsWith("_LOG") && data.containsKey("_default_log_sub")) {
				return Class.forName(get("_default_log_sub").get(0));
			}
			if (data.containsKey("_default_sub")) {
				return Class.forName(get("_default_sub").get(0));
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
}
