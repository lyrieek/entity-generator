package com.lyrieek.eg;

import com.lyrieek.eg.config.EGEnv;
import com.lyrieek.eg.config.ParserCache;
import com.lyrieek.eg.config.RedInk;
import com.lyrieek.eg.ibatis.ClassGenerator;
import net.bytebuddy.dynamic.DynamicType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws Exception {
		Path path = Paths.get("build/entity.yml");//Paths.get("build/%s.yml".formatted(tran.getCRC32()));
		File resource = new File("./src/main/resources");
		RedInk redInk = new RedInk(resource, "red-ink.yml");

		Transcribing tran = new Transcribing(path);
		Map<String, ResArray> tables = tran.getTables(new EGEnv(resource, "spring.properties"), redInk);
		tran.write(tables);
		File output = new File("build/generated");
		for (ClassInfo classInfo : ParserCache.parseYaml(path)) {
			ClassGenerator.generateClass(redInk, classInfo, output).ifPresent(loadedClass ->
					System.out.println("Generated class: " + loadedClass.getName()));
		}
	}

}