package com.lyrieek.eg;

import com.lyrieek.eg.config.EGEnv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		EGEnv env = new EGEnv();
		env.load("spring.properties");
		Transcribing tran = new Transcribing();
		Map<String, ResArray> tables = tran.getTables(env);
		String body = tran.getString(tables);
		Path path = Paths.get("build/%s.yml".formatted(tran.getCRC32()));
		try {
			Files.deleteIfExists(path);
			Files.writeString(path, body);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}