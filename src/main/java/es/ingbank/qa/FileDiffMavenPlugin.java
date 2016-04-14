package es.ingbank.qa;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import difflib.DiffUtils;
import difflib.Patch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Mojo(name = "diff", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FileDiffMavenPlugin extends AbstractMojo {

	@Parameter(property = "oldFile", required = true)
	private String oldFile;

	@Parameter(property = "newFile", required = true)
	private String newFile;

	@Parameter(property = "showDiff", defaultValue = "true", required = false)
	private String showDiff;

	@Parameter(property = "abortOnDiff", defaultValue = "true", required = false)
	private String abortOnDiff;
	
	private Log log = getLog();

	public void execute() throws MojoExecutionException {
		
		// Sanity Check
		if (!new File(oldFile).canRead()) {
			log.error(
					"Unable to read old file. Ensure the path is correct and has enough permissions to be read.");
			System.exit(1);
		}

		if (!new File(newFile).canRead()) {
			log.error(
					"Unable to read new file. Ensure the path is correct and has enough permissions to be read.");
			System.exit(1);
		}
		
		log.info("Old: " + oldFile);
		log.info("New: " + newFile);

		// Load and parse the data
		List<String> originalLines = fileToLines(oldFile);
		List<String> newLines = fileToLines(newFile);

		Patch<String> p = DiffUtils.diff(originalLines, newLines);
		List<String> differences = DiffUtils.generateUnifiedDiff(oldFile, newFile, originalLines, p, 3);
		
		if (Boolean.valueOf(showDiff)) {
			log.info("unified diff below:");
			for (String line : differences) {
				log.info(line);
			}
		}

		if (Boolean.valueOf(abortOnDiff) && differences.size() > 0) {
			log.error("Differences found. Aborting...");
			System.exit(1);
		}

	}

	public List<String> fileToLines(String f) {
		List<String> lines = new LinkedList<String>();
		String line = "";
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(f));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore ... any errors should already have been
					// reported via an IOException from the final flush.
				}
			}
		}
		return lines;
	}

}
