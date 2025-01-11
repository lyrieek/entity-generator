package com.lyrieek.eg;

import com.lyrieek.eg.config.EGEnv;
import com.lyrieek.eg.config.RedInk;
import com.lyrieek.eg.conn.DBConn;
import com.lyrieek.eg.conn.OracleBasicInfo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * copy db meta to cache yml
 */
public class Transcribing {

	private final CRC32 crc32 = new CRC32();
	Path output;

	public Transcribing(Path output) {
		this.output = output;
	}

	public Map<String, ResArray> getTables(EGEnv env, RedInk redInk) {
		try (DBConn conn = new DBConn(env)) {
			conn.start();
			OracleBasicInfo info = new OracleBasicInfo(conn);
			return info.list(redInk);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void write(Map<String, ResArray> tables) {
		String body = mapToContent(tables);
		try {
			Files.deleteIfExists(output);
			Files.writeString(output, "# CRC32:%s\n\n%s".formatted(getCRC32(), body));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String mapToContent(Map<String, ResArray> tables) {
		crc32.reset();
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, ResArray> table : tables.entrySet()) {
			builder.append(table.getKey()).append(":\n");
			for (LinkedHashMap<String, Object> item : table.getValue()) {
				builder.append("  %s:%n".formatted(item.get("COLUMN_NAME")));
				if (item.get("primaryKey") != null) {
					builder.append("    primaryKey: true\n");
				}
				String type = item.get("DATA_TYPE").toString();
				if (!type.startsWith("VARCHAR")) {
					builder.append("    type: %s\n".formatted(type));
					Object length = item.get("DATA_LENGTH");
					if (length != null && !"CLOB,BLOB,DATE".contains(type)) {
						builder.append("    length: %s\n".formatted(length));
					}
					Object precision = item.get("DATA_PRECISION");
					if (precision != null) {
						builder.append("    precision: %s\n".formatted(precision));
					}
				}
				crc32.update(item.toString().getBytes(StandardCharsets.UTF_8));
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	public String getCRC32() {
		return Long.toHexString(crc32.getValue());
	}

}
