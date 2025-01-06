package com.lyrieek.eg;

import java.util.*;

public class ResArray extends ArrayList<LinkedHashMap<String, Object>> {

	private LinkedHashMap<String, Object> last;

	public void put(String key, Object val) {
		if (last == null) {
			last = new LinkedHashMap<>();
		}
		last.put(key, val);
	}

	public void packed() {
		if (last != null) {
			add(last);
			last = null;
		}
	}

	public List<String> strArr(String key) {
		List<String> res = new ArrayList<>();
		for (LinkedHashMap<String, Object> item : this) {
			res.add(Objects.toString(item.get(key), ""));
		}
		return res;
	}

	public void printVals() {
		for (LinkedHashMap<String, Object> item : this) {
			for (Map.Entry<String, Object> entry : item.entrySet()) {
				System.out.print(entry.getValue());
				System.out.print(';');
			}
		}
		System.out.println();
	}

	public static HashMap<String, Class<?>> strMap(String... items) {
		HashMap<String, Class<?>> res = new HashMap<>();
		for (String item : items) {
			res.put(item, String.class);
		}
		return res;
	}
}
