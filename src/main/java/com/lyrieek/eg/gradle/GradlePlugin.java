package com.lyrieek.eg.gradle;

import com.lyrieek.eg.ClassInfo;
import com.lyrieek.eg.ResArray;
import com.lyrieek.eg.Transcribing;
import com.lyrieek.eg.config.EGEnv;
import com.lyrieek.eg.config.ParserCache;
import com.lyrieek.eg.config.RedInk;
import com.lyrieek.eg.ibatis.ClassGenerator;
import net.bytebuddy.dynamic.DynamicType;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class GradlePlugin implements Plugin<Project> {

	private String dbConfigPath;
	private EGEnv egEnv;

	@Override
	public void apply(Project project) {
		GradlePlugin entityGenerator = project.getExtensions().create("entityGenerator", GradlePlugin.class);
		String build = project.getLayout().getBuildDirectory().get().getAsFile().getPath();
		Path cache = Paths.get(build, "cache.yml");
		RedInk redInk = new RedInk(project.getRootDir(), "src/main/resources/red-ink.yml");
		redInk.setClassLoader(project.getBuildscript().getClassLoader());

		TaskProvider<Task> generateCacheByDB = project.getTasks().register("generateCacheByDB", task -> {
			task.doLast(taskAction -> {
				egEnv = new EGEnv(project.getRootDir(), entityGenerator.getDbConfigPath());
				Transcribing tran = new Transcribing(cache);
				Map<String, ResArray> tables = tran.getTables(egEnv, redInk);
				tran.write(tables);
				System.out.printf("generate cache crc32:[%s]%n", tran.getCRC32());
			});
			task.setGroup("other");
		});

		TaskProvider<?> autoGenerator = project.getTasks().register("entityGenerator", task -> {
			if (!Files.exists(cache)) {
				task.dependsOn(generateCacheByDB);
			}
			task.doLast(taskAction -> {
				System.out.println(redInk.getClassLoader());
				if (!Files.exists(cache)) {
					System.err.println("cache file loss");
					return;
				}
				File output = Paths.get(build, "generated").toFile();
				for (ClassInfo classInfo : ParserCache.parseYaml(cache)) {
					ClassGenerator.generateClass(redInk, classInfo, output).ifPresent(loadedClass ->
							System.out.println("Generated class: " + loadedClass.getName()));
				}
			});
			task.setGroup("build");
		});

		// 将打印任务添加到编译任务之前
		project.getTasks().named("compileJava").configure(task -> task.dependsOn(autoGenerator));
	}

	public String getDbConfigPath() {
		return dbConfigPath;
	}

	public void setDbConfigPath(String dbConfigPath) {
		this.dbConfigPath = dbConfigPath;
	}
}
