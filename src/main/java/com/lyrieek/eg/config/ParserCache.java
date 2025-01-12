package com.lyrieek.eg.config;

import com.lyrieek.eg.ClassInfo;
import com.lyrieek.eg.TextProcessor;
import com.lyrieek.eg.ibatis.FieldInfo;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ParserCache {

	public static List<ClassInfo> parseYaml(String filePath) {
		try {
			return parseYaml(Paths.get(Objects.requireNonNull(
					ClassLoader.getSystemClassLoader().getResource(filePath)).toURI()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<ClassInfo> parseYaml(Path path) {
		List<ClassInfo> classInfoList = new ArrayList<>();
		try (InputStream input = new FileInputStream(path.toFile())) {
			Yaml yaml = new Yaml();
			Map<String, Map<String, Map<String, Object>>> map = yaml.load(input);
			for (Map.Entry<String, Map<String, Map<String, Object>>> entityEntry : map.entrySet()) {
				classInfoList.add(getClassInfo(entityEntry));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
					fieldInfo.setType(getClassType(Objects.toString(propertyDetails.get("type"), "String"),
							Optional.ofNullable(propertyDetails.get("precision"))
									.map(e -> Integer.parseInt(e.toString())).orElse(0)));
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

	public static Class<?> getClassType(String typeName, int precision) {
		switch (typeName) {
			case "int":
				return int.class;
			case "char":
				return char.class;
			case "double":
				return double.class;
			case "boolean":
				return boolean.class;
			case "Integer":
				return Integer.class;
			case "Character":
				return Character.class;
			case "String":
				return String.class;
			case "NUMBER":
				if (precision == 1) {
					return Boolean.class;
				}
				return Long.class;
			case "Long":
				return Long.class;
			case "Double":
				return Double.class;
			case "Boolean":
				return Boolean.class;
			case "BigDecimal":
				return BigDecimal.class;
			case "LocalDate":
				return LocalDate.class;
			case "LocalTime":
				return LocalTime.class;
			case "Date":
			case "DATE":
				return LocalDateTime.class;
			case "CLOB":
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
