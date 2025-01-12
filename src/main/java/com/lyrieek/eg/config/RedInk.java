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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public Map<String, List<String>> getCover() {
		return data.entrySet().stream().filter(entry -> !entry.getKey().startsWith("_"))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public boolean matchPre(String content) {
		if (isEmpty("_pre")) {
			return !isExclude();
		}
		for (String item : data.get("_pre")) {
			if (content.startsWith(item)) {
				return isExclude();
			}
		}
		return !isExclude();
	}

	public boolean isExclude() {
		return get("_spec").contains("exclude");
	}

	public String defaultSingleVal(boolean isLog, String key) {
		if (isLog && data.containsKey("_default_log_" + key)) {
			return get("_default_log_" + key).get(0);
		}
		if (data.containsKey("_default_" + key)) {
			return get("_default_" + key).get(0);
		}
		return null;
	}

	public boolean filterField(boolean isLog, String columnName) {
		if (isLog && data.containsKey("_default_log_exclude_fields")) {
			return get("_default_log_exclude_fields").contains(columnName);
		} else if (data.containsKey("_default_exclude_fields")) {
			return get("_default_exclude_fields").contains(columnName);
		}
		return false;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return Objects.requireNonNullElse(classLoader, ClassLoader.getSystemClassLoader());
	}

}
