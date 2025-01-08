package com.lyrieek.eg.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class EGEnv {
	private final Properties prop = new Properties();

	public EGEnv(File folder, String file) {
		try {
			Path path = null;
			if (file != null && !file.isEmpty()) {
				path = Paths.get(folder.getPath(), file);
			}
			if (path == null || !Files.exists(path)) {
				path = Paths.get(folder.getPath(), "src\\main\\resources\\spring.properties");
			}
			if (!Files.exists(path)) {
				path = Paths.get(folder.getPath(), "src\\main\\webapp\\WEB-INF\\classes\\spring.properties");
			}
			prop.load(Files.newInputStream(Path.of(path.toUri())));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String val(String key) {
		return prop.getProperty(key, "");
	}

	public Properties getProp() {
		return prop;
	}

	public int getPropCount() {
		return prop.size();
	}
	
}
