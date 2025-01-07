package com.lyrieek.eg.ibatis;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyrieek.eg.ClassInfo;
import com.lyrieek.eg.config.RedInk;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import org.apache.ibatis.type.JdbcType;

import java.io.File;
import java.lang.reflect.Modifier;
import java.sql.Clob;
import java.util.Objects;

public class ClassGenerator {

	public static Class<?> generateClass(RedInk redInk, ClassInfo classInfo) throws Exception {
		ByteBuddy byteBuddy = new ByteBuddy();
		AnnotationDescription tableName = AnnotationDescription.Builder.ofType(TableName.class)
				.define("value", classInfo.getTableName()).build();
		AnnotationDescription keySequence = AnnotationDescription.Builder.ofType(KeySequence.class)
				.define("value", Objects.toString(classInfo.getSeq(), "AUTO_ID_SEQ")).build();
		ByteBuddy with = byteBuddy.with(AnnotationValueFilter.Default.SKIP_DEFAULTS);
		DynamicType.Builder<?> builder = with.subclass(redInk.getSubClass(classInfo))
				.name(classInfo.getFullName(redInk.getPackageName())).annotateType(tableName);
		builder = builder.annotateType(keySequence);
		for (FieldInfo field : classInfo.getFields()) {
			Class<?> type = field.getType();
			if (Clob.class.equals(type)) {
				type = String.class;
				field.setJdbcType(JdbcType.CLOB);
			}
			DynamicType.Builder.FieldDefinition.Optional<?> valuable = builder.defineField(field.getName(), type, Visibility.PRIVATE);
			if (field.getPrimaryKey()) {
				valuable = valuable.annotateField(AnnotationDescription.Builder.ofType(TableId.class).build());
			}
			if (field.getJdbcType() != null) {
				valuable = valuable.annotateField(AnnotationDescription.Builder.ofType(TableField.class).define("jdbcType", field.getJdbcType()).build());
			}
			builder = valuable;
			builder = builder
					.defineMethod("get" + capitalize(field.getName()), type, Modifier.PUBLIC)
					.intercept(FieldAccessor.ofField(field.getName()))
					.defineMethod("set" + capitalize(field.getName()), void.class, Modifier.PUBLIC)
					.withParameter(type)
					.intercept(FieldAccessor.ofField(field.getName()));
		}

		DynamicType.Unloaded<?> dynamicType = builder.make();
		dynamicType.saveIn(new File("build/generated"));
		return dynamicType.load(ClassGenerator.class.getClassLoader()).getLoaded();
	}

	private static String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}


}
