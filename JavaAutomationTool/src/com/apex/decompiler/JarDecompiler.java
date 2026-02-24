package com.apex.decompiler;

import java.io.*;
import java.util.jar.*;
import java.util.Enumeration;

public class JarDecompiler {

    public static void main(String[] args) throws Exception {
    	
    	try {
    	
    		String jarPath ="src/resource/AO.jar";
    		String outputPath = "Decompiled/";
    		File jarFile = new File(jarPath);

    		if (!jarFile.exists()) {
    			System.out.println("Jar file not found.");
    			return;
    		}

    		// Step 1: Extract jar
    		JarFile jar = new JarFile(jarFile);
    		Enumeration<JarEntry> entries = jar.entries();

    		File outputDir = new File(outputPath + "ClassFile");
    		outputDir.mkdir();

    		while (entries.hasMoreElements()) {
    			JarEntry entry = entries.nextElement();
    			File file = new File(outputDir, entry.getName());

    			if (entry.isDirectory()) {
    				file.mkdirs();
    				continue;
    			}

    			file.getParentFile().mkdirs();

    			InputStream is = jar.getInputStream(entry);
    			FileOutputStream fos = new FileOutputStream(file);

    			byte[] buffer = new byte[4096];
    			int bytesRead;

    			while ((bytesRead = is.read(buffer)) != -1) {
    				fos.write(buffer, 0, bytesRead);
    			}

    			fos.close();
    			is.close();
    		}

    		jar.close();

    		System.out.println("Class File extracted successfully");

    		// Step 2: Run CFR decompiler
    		ProcessBuilder pb = new ProcessBuilder(
    				"java", "-jar", "lib/cfr.jar", jarPath, "--outputdir", outputPath + "JavaFile");

    		pb.inheritIO();
    		Process process = pb.start();
    		process.waitFor();

    		System.out.println("Decompilation finished.");

    	}catch (Exception ex) {
    	ex.printStackTrace();
     }
    }
}

