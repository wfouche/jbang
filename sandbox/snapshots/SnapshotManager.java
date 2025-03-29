///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.github.wfouche.tulip:tulip-runtime:2.1.6
//DEPS org.springframework.boot:spring-boot-starter-web:3.4.4
//DEPS org.slf4j:slf4j-api:2.0.17
//DEPS ch.qos.logback:logback-core:1.5.18
//DEPS ch.qos.logback:logback-classic:1.5.18

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class SnapshotManager {

    private static final String indexFileText = "{\n    \"description\": \"__DESC__\",\n    \"timestamp\": \"__TIMESTAMP__\"\n}\n";

    public static void sha1HashFile(String filepath, String outputFilepath) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] fileBytes = Files.readAllBytes(Paths.get(filepath));
            sha1.update(fileBytes);
            String hashValue = bytesToHex(sha1.digest());

            try (BufferedWriter outF = new BufferedWriter(new FileWriter(outputFilepath))) {
                outF.write(hashValue);
            }

            System.out.println("    " + filepath + " --> " + outputFilepath);

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found at " + filepath);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static List<String> getSources(String scriptFilename) {
        List<String> sources = new ArrayList<>();
        try (BufferedReader file = new BufferedReader(new FileReader(scriptFilename))) {
            String line;
            String token = "//SOURCES ";
            while ((line = file.readLine()) != null) {
                if (line.length() > token.length() && line.startsWith(token)) {
                    sources.addAll(Arrays.asList(line.split("\\s+")).subList(1, line.split("\\s+").length));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sources;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String mainScriptFilename = args[0];
        String srcDir = new File(mainScriptFilename).getParent();
        String description = args[1];
        String dateTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());

        System.out.println("\nSource script   : " + mainScriptFilename);
        System.out.println("Description     : " + description);

        String mainSnapshotDirname = mainScriptFilename.substring(0, mainScriptFilename.lastIndexOf('.'));

        // Create the snapshot folder if required
        File snapshotDir = new File(mainSnapshotDirname);
        if (!snapshotDir.isDirectory()) {
            snapshotDir.mkdir();
        }

        // Calculate the next snapshotId to be used
        File[] dirList = snapshotDir.listFiles();
        Set<Integer> intSet = new HashSet<>();
        if (dirList != null) {
            for (File dirname : dirList) {
                intSet.add(Integer.parseInt(dirname.getName()));
            }
        }
        int snapshotId = intSet.isEmpty() ? 1 : Collections.max(intSet) + 1;

        // Create the snapshot folder
        String destDir = mainSnapshotDirname + "/" + snapshotId;
        System.out.println("\nSnapshot folder : " + destDir);
        new File(destDir).mkdir();

        // Copy files
        System.out.println("\nSources files;");
        List<String> srcFiles = new LinkedList<>();
        srcFiles.add(mainScriptFilename);
        System.out.println("   " + mainScriptFilename);
        for (String file : getSources(mainScriptFilename)) {
            String filename;
            if (srcDir.length() > 0) {
                filename = srcDir + "/" + file;
            } else {
                filename = file;
            }
            srcFiles.add(filename);
            System.out.println("   " + filename);
        }

        // Copy files
        System.out.println("\nSnapshot started.");
        for (String srcFile : srcFiles) {
            System.out.println("    " + srcFile + " --> " + destDir + "/");
            try {
                Files.copy(Paths.get(srcFile), Paths.get(destDir + "/" + new File(srcFile).getName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sha1DestFile = destDir + '/' + new File(srcFile).getName() + ".sha1";
            sha1HashFile(srcFile, sha1DestFile);
        }

        String srcFile = destDir + '/' + "00index.json";
        String dstFile = srcFile + ".sha1";
        System.out.println("    " + srcFile);
        try (BufferedWriter idxFile = new BufferedWriter(new FileWriter(srcFile))) {
            idxFile.write(indexFileText.replace("__DESC__", description).replace("__TIMESTAMP__", dateTimestamp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sha1HashFile(srcFile, dstFile);
        System.out.println("\nSnapshot done.");
    }

}
