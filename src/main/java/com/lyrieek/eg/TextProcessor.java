package com.lyrieek.eg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {

	public static String camel(String input) {
		String text = pascal(input);
		return text.substring(0, 1).toLowerCase() + text.substring(1);
	}

	public static String pascal(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		// 正则表达式匹配连续的大写字母或单个大写字母后跟下划线
		Pattern pattern = Pattern.compile("([A-Z]+)(_)?");
		Matcher matcher = pattern.matcher(input);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			String group = matcher.group(1);
			String replacement;
			if (group.length() > 1) {
				// 连续的大写字母开头的，转为首字母大写，其余小写
				replacement = group.charAt(0) + group.substring(1).toLowerCase();
			} else {
				replacement = group;
			}
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		return sb.toString().replace("_", "");
	}

}
