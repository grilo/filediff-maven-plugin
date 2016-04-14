# filediff-maven-plugin

I haven't published it in any public repository, but feel free to use it all the same.

Attaches itself to the validate stage (before compiling).

Example usage:
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>es.ingbank.qa</groupId>
	<artifactId>test</artifactId>
	<version>1.0-SNAPSHOT</version>

	<build>
		<plugins>
			<plugin>
				<groupId>es.ingbank.qa</groupId>
				<artifactId>filediff-maven-plugin</artifactId>
				<version>1.0-SNAPSHOT</version>
				<configuration>
                    <oldFile>original.java</oldFile>
                    <newFile>new.java</newFile>
                    <showDiff>true</showDiff>
                    <abortOnDiff>true</abortOnDiff>
				</configuration>
				<executions>
					<execution>
						<goals>
							<goal>diff</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
</project>
```
