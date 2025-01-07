package com.lyrieek.eg.config;

import com.lyrieek.eg.Main;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EGEnv {
	private final Properties prop = new Properties();

	public EGEnv(String path){
		try {
			URL resource = Main.class.getClassLoader().getResource(path);
			assert resource != null;
			prop.load(Files.newInputStream(Path.of(resource.toURI())));
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
}
