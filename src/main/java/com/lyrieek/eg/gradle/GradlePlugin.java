package com.lyrieek.eg.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class GradlePlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		PluginParam param = project.getExtensions().create("myPluginConfig", PluginParam.class, project);

		TaskProvider<?> printTask = project.getTasks().register("entityGenerator", task -> {
			task.doLast(taskAction -> {
				System.out.println("db config: " + param.getDbConfigPath());
			});
		});

		// 将打印任务添加到编译任务之前
		project.getTasks().named("compileJava").configure(task -> task.dependsOn(printTask));

	}
}
