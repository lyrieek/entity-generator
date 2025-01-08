package com.lyrieek.eg.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class GradlePlugin implements Plugin<Project> {

	private String dbConfigPath;

	@Override
	public void apply(Project project) {
		GradlePlugin entityGenerator = project.getExtensions().create("entityGenerator", GradlePlugin.class);

		TaskProvider<?> printTask = project.getTasks().register("entityGenerator", task -> {
			task.doLast(taskAction -> {
				System.out.println("db config path: " + entityGenerator.getDbConfigPath());
			});
		});

		// 将打印任务添加到编译任务之前
		project.getTasks().named("compileJava").configure(task -> task.dependsOn(printTask));
	}

	public String getDbConfigPath() {
		return dbConfigPath;
	}

	public void setDbConfigPath(String dbConfigPath) {
		this.dbConfigPath = dbConfigPath;
	}
}
