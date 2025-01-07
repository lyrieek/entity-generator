package com.lyrieek.eg.config;

import com.lyrieek.eg.ClassInfo;
import com.lyrieek.eg.TextProcessor;
import com.lyrieek.eg.ibatis.FieldInfo;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParserCache {

	public static List<ClassInfo> parseYaml(String filePath) throws Exception {
		return parseYaml(Paths.get(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(filePath)).toURI()));
	}

	public static List<ClassInfo> parseYaml(Path path) throws Exception {
		List<ClassInfo> classInfoList = new ArrayList<>();
		try (InputStream input = new FileInputStream(path.toFile())) {
			Yaml yaml = new Yaml();
			Map<String, Map<String, Map<String, Object>>> map = yaml.load(input);
			for (Map.Entry<String, Map<String, Map<String, Object>>> entityEntry : map.entrySet()) {
				classInfoList.add(getClassInfo(entityEntry));
			}
		}
		return classInfoList;
	}

	private static ClassInfo getClassInfo(Map.Entry<String, Map<String, Map<String, Object>>> entityEntry) {
		String className = entityEntry.getKey();
		Map<String, Map<String, Object>> propertiesData = entityEntry.getValue();

		ClassInfo classInfo = new ClassInfo();
		classInfo.setTableName(className);
		List<FieldInfo> fs = new ArrayList<>();

		if (propertiesData != null) {
			for (Map.Entry<String, Map<String, Object>> propertyEntry : propertiesData.entrySet()) {
				String fieldName = propertyEntry.getKey();
				Map<String, Object> propertyDetails = propertyEntry.getValue();
				if (fieldName.startsWith("_")) {
					if (propertyDetails.get("seq") != null) {
						classInfo.setSeq(propertyDetails.get("seq").toString());
					}
					continue;
				}
				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.setName(TextProcessor.camel(fieldName));
				if (propertyDetails != null) {
					String fieldType = Objects.toString(propertyDetails.get("type"), "string");
					fieldInfo.setType(getClassType(fieldType));
					fieldInfo.setPrimaryKey((Boolean) propertyDetails.get("primaryKey"));
				} else {
					fieldInfo.setType(String.class);
				}
				fs.add(fieldInfo);
			}
		}

		classInfo.setFields(fs);
		return classInfo;
	}

	private static Class<?> getClassType(String typeName) {
		switch (typeName) {
			case "int":
				return int.class;
			case "Integer":
				return Integer.class;
			case "String":
				return String.class;
			case "Long":
			case "NUMBER":
				return Long.class;
			case "Boolean":
				return Boolean.class;
			case "Date":
			case "DATE":
				return LocalDateTime.class;
			case "clob":
				return Clob.class;
			// 添加其他类型
			default:
				try {
					return Class.forName(typeName);
				} catch (ClassNotFoundException e) {
					return String.class;
				}
		}
	}

}
