package com.lyrieek.eg;

import com.lyrieek.eg.config.EGEnv;
import com.lyrieek.eg.config.ParserCache;
import com.lyrieek.eg.config.RedInk;
import com.lyrieek.eg.ibatis.ClassGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class MainTest {

	@Test
	public void mainTest() throws Exception {
		Path path = Paths.get("build/entity.yml");//Paths.get("build/%s.yml".formatted(tran.getCRC32()));
		File resource = new File("./src/test/resources");
		RedInk redInk = new RedInk(resource, "red-ink.yml");

		Transcribing tran = new Transcribing(path);
		Map<String, ResArray> tables = tran.getTables(new EGEnv(resource, "spring.properties"), redInk);
		tran.write(tables);

		LocalTime time = LocalTime.now();
		File output = new File("build/generated");
		for (ClassInfo classInfo : ParserCache.parseYaml(path)) {
			ClassGenerator.generateClass(redInk.getDefault(classInfo), classInfo, output).ifPresent(loadedClass ->
					System.out.println("Generated class: " + loadedClass.getName()));
		}
		System.out.printf("%dms;%n", ChronoUnit.MILLIS.between(time, LocalTime.now()));
	}

}