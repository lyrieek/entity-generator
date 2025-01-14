package com.lyrieek.eg.config;

import com.lyrieek.eg.ClassInfo;
import com.lyrieek.eg.util.ByteBuddyUtils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;

import java.lang.reflect.Constructor;
import java.util.*;

public class DefaultSet {

	private Class<?> supClass;
	private Class<?> superConstructorArg;
	private String seq;
	private String packageName = "com";
	private final Map<String, List<String>> cover;

	private static ClassLoader CLASS_LOADER;
	private final static Map<String, Class<?>> CACHE_CLASS = new HashMap<>();

	public DefaultSet(ClassInfo classInfo, RedInk redInk) {
		setSuperConstructorArg(redInk.defaultSingleVal(classInfo.isLog(), "super_arg"));
		setSupClass(redInk.defaultSingleVal(classInfo.isLog(), "super"));
		setSeq(redInk.defaultSingleVal(classInfo.isLog(), "seq"));
		setPackageName(redInk.get("_package_name").get(0));
		this.cover = redInk.getCover();
	}

	public Class<?> getSupClass() {
		return supClass;
	}

	/**
	 * get has param super constructor
	 */
	public Constructor<?> getSuperConstructor() {
		try {
			if (getSuperConstructorArg() == null) {
				return getSupClass().getConstructor();
			}
			if (Object.class.equals(getSupClass())) {
				return Object.class.getConstructor();
			}
			return getSupClass().getConstructor(getSuperConstructorArg());
		} catch (NoSuchMethodException e) {
			System.err.printf("super:[%s]; msg:[%s];%n", getSupClass().toString(), e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void setSupClass(String supClass) {
		if (supClass == null || supClass.isEmpty()) {
			this.supClass = Object.class;
			return;
		}
		this.supClass = stringToClass(supClass, superConstructorArg);
	}

	public void setSuperConstructorArg(String superConstructorArg) {
		if (superConstructorArg == null) {
			return;
		}
		this.superConstructorArg = stringToClass(superConstructorArg);
	}

	public Class<?> stringToClass(String classFullName) {
		return stringToClass(classFullName, null);
	}

	public Class<?> stringToClass(String classFullName, Class<?> constructorArg) {
		if (CACHE_CLASS.containsKey(classFullName)) {
			return CACHE_CLASS.get(classFullName);
		}
		ByteBuddy byteBuddy = new ByteBuddy();
		DynamicType.Builder<Object> builder = byteBuddy
				.subclass(Object.class, ConstructorStrategy.Default.NO_CONSTRUCTORS).name(classFullName);
		if (constructorArg != null) {
			builder = builder.defineConstructor(Visibility.PUBLIC)
					.withParameters(constructorArg)
					.intercept(ByteBuddyUtils.getNoopSuper());
		}
		builder = builder.defineConstructor(Visibility.PUBLIC).intercept(ByteBuddyUtils.getNoopSuper());
		try (DynamicType.Unloaded<Object> classMake = builder.make()) {
			DynamicType.Loaded<Object> item = classMake
					.load(Objects.requireNonNullElse(CLASS_LOADER, ClassLoader.getSystemClassLoader()));
//			if (constructorArg != null) {
//				item.saveIn(new File("build/aa"));
//			}
			Class<?> res = item.getLoaded();
			if (!res.getClassLoader().equals(CLASS_LOADER)) {
				CLASS_LOADER = res.getClassLoader();
				System.out.println("change [Class Loader]:" + CLASS_LOADER);
			}
			CACHE_CLASS.put(classFullName, res);
			return res;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	/**
	 * 获取列的对应类型
	 */
	public Class<?> getType(String className, String field) {
		List<String> arr = cover.get(className);
		if (arr == null) {
			arr = cover.get(className.replaceAll("[A-Za-z0-9.]+\\.(?=[A-Z])", ""));
		}
		if (arr == null) {
			return null;
		}
		for (int i = 0; i < arr.size() - 1; i++) {
			if (field.equals(arr.get(i))) {
				// 进入类型修订
				return ParserCache.getClassType(arr.get(i + 1), 0);
			}
		}
		return null;
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

	public Class<?> getSuperConstructorArg() {
		return superConstructorArg;
	}

}
