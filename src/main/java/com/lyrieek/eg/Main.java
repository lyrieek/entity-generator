package com.lyrieek.eg;

import com.lyrieek.eg.config.EGEnv;
import com.lyrieek.eg.config.ParserCache;
import com.lyrieek.eg.config.RedInk;
import com.lyrieek.eg.ibatis.ClassGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws Exception {
		Path path = Paths.get("build/entity.yml");//Paths.get("build/%s.yml".formatted(tran.getCRC32()));
		RedInk redInk = new RedInk("red-ink.yml");
//		Transcribing tran = new Transcribing();
//		Map<String, ResArray> tables = tran.getTables(new EGEnv("spring.properties"), redInk);
//		String body = tran.getString(tables);
//		try {
//			Files.deleteIfExists(path);
//			Files.writeString(path, body);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		for (ClassInfo classInfo : ParserCache.parseYaml(path)) {
			Class<?> generatedClass = ClassGenerator.generateClass(redInk, classInfo);
			System.out.println("Generated class: " + generatedClass.getName());
		}
	}

}