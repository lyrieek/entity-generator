package com.lyrieek.eg.ibatis;

import com.baomidou.mybatisplus.annotation.*;
import com.lyrieek.eg.ClassInfo;
import com.lyrieek.eg.config.DefaultSet;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.*;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import org.apache.ibatis.type.JdbcType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.sql.Clob;
import java.util.Objects;
import java.util.Optional;

public class ClassGenerator {

	public static Optional<Class<?>> generateClass(DefaultSet dSet, ClassInfo classInfo, File folder) {
		try (DynamicType.Unloaded<?> classItem = ClassGenerator.generateClass(dSet, classInfo)) {
			classItem.saveIn(folder);
			if (dSet.getGeneratedLoad()) {
				return Optional.of(classItem.load(ClassLoader.getSystemClassLoader()).getLoaded());
			} else {
				System.out.println("Generated class: " + classItem.getTypeDescription().getName());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return Optional.empty();
	}

	public static DynamicType.Unloaded<?> generateClass(DefaultSet dSet, ClassInfo classInfo) {
		String name = classInfo.getFullName(dSet.getPackageName());
		ByteBuddy byteBuddy = new ByteBuddy()
				.with(TypeValidation.DISABLED).with(AnnotationValueFilter.Default.SKIP_DEFAULTS);
		AnnotationDescription tableName = AnnotationDescription.Builder.ofType(TableName.class)
				.define("value", classInfo.getTableName()).build();
		DynamicType.Builder<?> builder = byteBuddy.subclass(dSet.getSupClass())
				.name(name).annotateType(tableName);
		String seq = Objects.toString(classInfo.getSeq(), dSet.getSeq());
		if (seq != null) {
			builder = builder.annotateType(AnnotationDescription.Builder.ofType(KeySequence.class)
					.define("value", seq).build());
		}
		if (dSet.getSuperConstructorArg() != null) {
			builder = builder
					.defineConstructor(Visibility.PUBLIC)
					.withParameter(dSet.getSuperConstructorArg())
					.intercept(MethodCall.invoke(dSet.getSuperConstructor()).onSuper().withArgument(0));
		} else {
			builder = builder
					.defineConstructor(Visibility.PUBLIC)
					.intercept(MethodCall.invoke(dSet.getSuperConstructor()).onSuper());
		}
		for (FieldInfo field : classInfo.getFields()) {
			Class<?> type = Objects.requireNonNullElse(dSet.getType(name, field.getName()), field.getType());
			if (Clob.class.equals(type)) {
				type = String.class;
				field.setJdbcType(JdbcType.CLOB);
			}
			DynamicType.Builder.FieldDefinition.Optional<?> valuable = builder.defineField(field.getName(), type, Visibility.PRIVATE);
			if (field.getPrimaryKey()) {
				valuable = valuable.annotateField(
						AnnotationDescription.Builder.ofType(TableId.class).define("type", IdType.INPUT).build());
			}
			if (field.getJdbcType() != null) {
				valuable = valuable.annotateField(
						AnnotationDescription.Builder.ofType(TableField.class).define("jdbcType", field.getJdbcType()).build());
			}
			builder = valuable;
			builder = builder
					.defineMethod("get" + capitalize(field.getName()), type, Modifier.PUBLIC)
					.intercept(FieldAccessor.ofField(field.getName()))
					.defineMethod("set" + capitalize(field.getName()), void.class, Modifier.PUBLIC)
					.withParameter(type)
					.intercept(FieldAccessor.ofField(field.getName()));
		}

		return builder.make();
	}

	private static String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}


}
