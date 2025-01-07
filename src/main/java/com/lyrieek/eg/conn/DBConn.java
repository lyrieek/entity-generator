package com.lyrieek.eg.conn;

import com.lyrieek.eg.config.EGEnv;
import com.lyrieek.eg.ResArray;

import java.io.Closeable;
import java.sql.*;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBConn implements Closeable {
	private final EGEnv env;
	private Connection conn;
	private String schema;
	private LocalTime time;

	public DBConn(EGEnv env) {
		this.env = env;
	}

	public void start() {
		String url = env.val("jdbc.url");
		String user = env.val("jdbc.username");
		String password = env.val("jdbc.password");
		try {
			conn = DriverManager.getConnection(url, user, password);
			if (conn == null) {
				System.out.println("无法连接到数据库!");
				return;
			}
			time = LocalTime.now();
			System.out.println("成功连接到数据库!");
			schema = user.toUpperCase();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, List<String>> queryKeyArray(String sql, String key, String arrayKey) {
		Map<String, List<String>> res = new LinkedHashMap<>();
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				String itemKey = rs.getString(key);
				List<String> array = res.get(itemKey);
				if (array == null) {
					array = new ArrayList<>();
				}
				array.add(rs.getString(arrayKey));
				res.put(itemKey, array);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	public ResArray query(String sql, Map<String, Class<?>> columnTypes) {
		ResArray results = new ResArray();
		try (Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					String columnName = rsmd.getColumnName(i);
					Class<?> columnType = columnTypes.get(columnName);

					Object value = null;
					if (columnType != null) { // 如果 Map 中指定了类型，则尝试按类型获取
						try {
							if (columnType == String.class) {
								value = rs.getString(i);
							} else if (columnType == Integer.class || columnType == int.class) {
								value = rs.getInt(i);
							} else if (columnType == Long.class || columnType == long.class) {
								value = rs.getLong(i);
							} else if (columnType == Double.class || columnType == double.class) {
								value = rs.getDouble(i);
							} else if (columnType == Float.class || columnType == float.class) {
								value = rs.getFloat(i);
							} else if (columnType == Boolean.class || columnType == boolean.class) {
								value = rs.getBoolean(i);
							} else if (columnType == java.sql.Date.class) {
								value = rs.getDate(i);
							} else if (columnType == java.sql.Timestamp.class) {
								value = rs.getTimestamp(i);
							} else {
								value = rs.getObject(i); // 其他类型使用 getObject
							}
						} catch (SQLException e) {
							// 处理类型转换异常，例如数据库中的 NULL 值
							System.err.println("列 " + columnName + " 类型转换异常: " + e.getMessage());
							value = null;
						}
					} else {
						value = rs.getObject(i);
					}
					results.put(columnName, value);
				}
				results.packed();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return results;
	}

	public String getSchema() {
		return schema;
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
				System.out.printf("数据库连接关闭了, 本次操作了%d毫秒%n",
						ChronoUnit.MILLIS.between(time, LocalTime.now()));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
