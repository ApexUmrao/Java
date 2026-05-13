package com.apex.maven;

import java.io.*;
import java.util.*;

public class PomGenerator {

    public static void main(String[] args) {
    	
    	  // Project metadata (make this configurable)
        String groupId = "Egypt_LOS";
        String artifactId = "Egypt_LOS";
        String version = "1.0";
        String packaging = "jar";
        String fileName = "LOS_EG";
    	
        // Paths to lib folder and pom folder
        String libFolderPath = "D:/GIT/Aproj2_KFH_Egypt_CR_26/Code/Egypt/RLOS Java Code Offshore/RLOS Java Code Offshore/lib"; // Specify the path to the lib folder
        String pomFilePath = "D:/GIT/Aproj2_KFH_Egypt_CR_26/Code/Egypt/RLOS Java Code Offshore/RLOS Java Code Offshore/pom.xml"; // Specify the path to the pom.xml file

        // List to hold all dependencies
        List<String> dependencies = new ArrayList<>();

        // Get dependencies from lib folder (JAR files)
        File libFolder = new File(libFolderPath);
        if (libFolder.exists() && libFolder.isDirectory()) {
            dependencies.addAll(getDependenciesFromLibFolder(libFolder));
        } else {
            System.out.println("The provided lib folder path is not a valid directory.");
            return;
        }

        // Check if pom.xml already exists
        File pomFile = new File(pomFilePath);
        if (pomFile.exists()) {
            // If pom.xml exists, append the new dependencies from lib folder
            appendDependenciesToPom(pomFile, dependencies);
        } else {
            // If pom.xml does not exist, create a new one with the dependencies
            generateNewPom(pomFile, dependencies, groupId, artifactId, version, packaging, fileName);
        }
    }

    // Get dependencies from JAR files in the lib folder
    private static List<String> getDependenciesFromLibFolder(File libFolder) {
        List<String> dependencies = new ArrayList<>();
        File[] files = libFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files != null) {
            for (File jar : files) {
                String jarName = jar.getName();
                // Assuming JAR files are named like: library-name-version.jar
                String[] parts = jarName.split("-(?=[^\\d]+\\d)"); // Split at version part
//                if (parts.length >= 2) {
                	String groupId = jarName.substring(0, jarName.length()-4); // Default groupId or logic for groupId could be added here
//                    String artifactId = parts[0];

                    String version = "1.0.0"; // Default version or logic for versioning could be added here
                    
                    // Form the dependency with systemPath
                    String dependency = String.format(
                            "<dependency>" +
                                    "<groupId>"+groupId+"</groupId>" +
                                    "<artifactId>%s</artifactId>" +
                                    "<version>%s</version>" +
                                    "<scope>system</scope>" +
                                    "<systemPath>${basedir}/lib/%s</systemPath>" +
                            "</dependency>", groupId, version, jarName);

                    dependencies.add(dependency);
//                }
            }
        }
        return dependencies;
    }

    // Append new dependencies to an existing pom.xml file
    private static void appendDependenciesToPom(File pomFile, List<String> dependencies) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pomFile));
            StringBuilder pomContent = new StringBuilder();
            String line;
            boolean inDependenciesSection = false;

            // Read the existing pom.xml
            while ((line = reader.readLine()) != null) {
                pomContent.append(line).append("\n");

                // Find where the dependencies section starts
                if (line.contains("<dependencies>")) {
                    inDependenciesSection = true;
                }

                // If we're inside the dependencies section, append the new dependencies
                if (inDependenciesSection && line.contains("</dependencies>")) {
                    for (String dependency : dependencies) {
                        pomContent.append("    ").append(dependency).append("\n");
                    }
                    inDependenciesSection = false;  // Stop appending after the closing </dependencies> tag
                }
            }

            // If dependencies section was not found, add one at the end
            if (!inDependenciesSection) {
                try (FileWriter writer = new FileWriter(pomFile)) {
                    writer.write(pomContent.toString());
                }
            }

            System.out.println("pom.xml updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generate a new pom.xml file with the dependencies from the lib folder
    private static void generateNewPom(File pomFile, List<String> dependencies, 
                                       String groupId, String artifactId,
                                       String version, String packaging,
                                       String fileName) {
        StringBuilder pomContent = new StringBuilder()
            .append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n")
            .append("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n")
            .append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n")
            .append("         http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n")
            .append("    <modelVersion>4.0.0</modelVersion>\n\n")
            .append("    <groupId>").append(groupId).append("</groupId>\n")
            .append("    <artifactId>").append(artifactId).append("</artifactId>\n")
            .append("    <version>").append(version).append("</version>\n")
//            .append("    <packaging>").append(packaging).append("</packaging>\n\n")
            .append("    <dependencies>\n");

        // Add dependencies from the lib folder
        for (String dep : dependencies) {
            pomContent.append("        ").append(dep).append("\n");
        }

        pomContent.append("    </dependencies>\n\n")
            .append("    <build>\n")
            .append("        <sourceDirectory>src</sourceDirectory>\n")
            .append("        <finalName>")
            .append(          fileName)
            .append("</finalName>\n")
            .append("        <plugins>\n")
            .append("            <plugin>\n")
            .append("                <artifactId>maven-compiler-plugin</artifactId>\n")
            .append("                <version>3.8.1</version>\n")
            .append("                <configuration>\n")
            .append("                    <source>1.8</source>\n")
            .append("                    <target>1.8</target>\n")
            .append("                </configuration>\n")
            .append("            </plugin>\n")
            .append("            <plugin>\n")
            .append("                <groupId>org.sonarsource.scanner.maven</groupId>\n")
            .append("                <artifactId>sonar-maven-plugin</artifactId>\n")
            .append("                <version>4.0.0.4121</version>\n")
            .append("            </plugin>\n")
            .append("        </plugins>\n")
            .append("        <resources>\n")
            .append("            <resource>\n")
            .append("                <directory>${basedir}</directory>\n")
            .append("                <includes>\n")
            .append("                    <include>services.xml</include>\n")
            .append("                </includes>\n")
            .append("                <targetPath>META-INF</targetPath>\n")
            .append("            </resource>\n")
            .append("        </resources>\n")
            .append("    </build>\n\n")
            .append("    <properties>\n")
            .append("        <sonar.java.binaries>target/classes</sonar.java.binaries>\n")
            .append("    </properties>\n")
            .append("</project>\n");

        // Write the generated pom.xml to the file
        try (FileWriter writer = new FileWriter(pomFile)) {
            writer.write(pomContent.toString());
            System.out.println("pom.xml created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
