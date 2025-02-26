package com.lyrieek.eg.conn;

import com.lyrieek.eg.ResArray;
import com.lyrieek.eg.config.RedInk;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicInfo {

	private final DBConn conn;
	protected String schema;

	public BasicInfo(DBConn conn) {
		this.conn = conn;
		this.schema = conn.getSchema();
	}

	public Map<String, ResArray> list(RedInk redInk) {
		Map<String, ResArray> map = new LinkedHashMap<>();
		Map<String, List<String>> cons = conn.queryKeyArray(getCons(), "TABLE_NAME", "NAME");
		conn.query(getTableQuery(), ResArray.strMap("TABLE_NAME")).forEach(e -> {
			String tableName = e.get("TABLE_NAME").toString();
			if (redInk.shouldFilter(tableName)) {
				return;
			}
			ResArray cols = conn.query(getCols(tableName), new HashMap<>() {{
				put("COLUMN_NAME", String.class);
				put("DATA_TYPE", String.class);
				//put("DATA_LENGTH", Integer.class);
			}});
			cols.removeIf(item ->
					redInk.filterField(tableName.endsWith("_LOG"), item.get("COLUMN_NAME").toString()));
			if (cons.containsKey(tableName)) {
				for (LinkedHashMap<String, Object> item : cols) {
					String columnName = item.get("COLUMN_NAME").toString();
					if (cons.get(tableName).contains(columnName)) {
						item.put("primaryKey", true);
						if (cons.size() == 1) {
							break;
						}
					}
				}
			}
			map.put(tableName, cols);
		});
		return map;
	}

	abstract String getTableQuery();

	abstract String getCols(String tableName);

	abstract String getCons();

}
