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

	@Parameter(property = "original", required = true)
	private String original;

	@Parameter(property = "files", required = true)
	private String[] files;

	@Parameter(property = "showDiff", defaultValue = "true", required = false)
	private String showDiff;

	@Parameter(property = "abortOnDiff", defaultValue = "true", required = false)
	private String abortOnDiff;

	private Log log = getLog();
	private int diffCount = 0;

	public void execute() throws MojoExecutionException {

		// Sanity Check
		if (!new File(original).canRead()) {
			log.error("Unable to read old file. Ensure the path is correct and has enough permissions to be read.");
			System.exit(1);
		}
		log.info(" (original) " + original);

		for (String f : files) {

			if (!new File(f).canRead()) {
				log.error("Unable to read new file. Ensure the path is correct and has enough permissions to be read.");
				System.exit(1);
			}
		}

		// Load and parse the data
		List<String> originalLines = fileToLines(original);

		for (String f : files) {
			List<String> lines = fileToLines(f);
			Patch<String> p = DiffUtils.diff(originalLines, lines);
			List<String> differences = DiffUtils.generateUnifiedDiff(original, f, originalLines, p, 3);

			if (differences.size() > 0) {
				log.info(" (different) " + f);
				if (Boolean.valueOf(showDiff)) {
					for (String line : differences) {
						System.out.println(line);
					}
				}
			} else {
				log.info(" (identical) " + f);
			}

			if (differences.size() > 0) {
				diffCount += 1;
			}
		}
		log.warn("Differences found in (" + diffCount + ") files.");

		if (Boolean.valueOf(abortOnDiff) && diffCount > 0) {
			log.error("Aborting...");
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
