/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.io.File;
import java.util.Arrays;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.internal.jvm.Jvm;

/**
 *
 * @author Dave Syer
 */
public class MinimalPlugin implements Plugin<Project> {

	public void apply(final Project target) {
		target.getTasks().withType(Jar.class, new Action<Jar>() {

			@Override
			public void execute(Jar jar) {
				createCopyTask(target, jar);
				createExecTask(target, jar);
			}

		});
	}

	protected void createCopyTask(Project project, Jar jar) {
		Copy copy = project.getTasks().create("minimalPrepare", Copy.class);
		copy.dependsOn("bootRepackage");
		copy.from(jar.getOutputs().getFiles());
		copy.into(new File(project.getBuildDir(), "root"));
	}

	private void createExecTask(final Project project, final Jar jar) {
		final Exec exec = project.getTasks().create("minimal", Exec.class);
		exec.dependsOn("minimalPrepare");
		exec.doFirst(new Action<Task>() {
			@SuppressWarnings("unchecked")
			@Override
			public void execute(Task task) {
				Copy copy = (Copy) project.getTasks().getByName("minimalPrepare");
				exec.setWorkingDir(copy.getOutputs().getFiles().getSingleFile());
				exec.setCommandLine(Jvm.current().getJavaExecutable());
				exec.args(Arrays.asList("-jar", jar.getArchiveName()));
			}
		});
	}
}
