package me.falu.peepopractice;

import com.google.gson.JsonParser;
import me.falu.peepopractice.core.PracticeWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DefaultFileWriter {
    public static final DefaultFileWriter INSTANCE = new DefaultFileWriter();

    @SuppressWarnings({ "ResultOfMethodCallIgnored", "BlockingMethodInNonBlockingContext" })
    public void writeDefaultFiles() {
        try {
            File folder = FabricLoader.getInstance().getConfigDir().resolve(PeepoPractice.MOD_NAME).toFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            List<String> resources = this.getResourceFiles("writer", "");
            for (String resource : resources) {
                File destination = folder.toPath().resolve(resource).toFile();
                if (!destination.exists()) {
                    if (!destination.getParentFile().exists()) {
                        destination.getParentFile().mkdirs();
                    }
                    destination.createNewFile();
                    try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("writer/" + resource)) {
                        if (stream != null) {
                            FileWriter writer = new FileWriter(destination);
                            writer.write(PracticeWriter.GSON.toJson(new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))));
                            writer.flush();
                            writer.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    private List<String> getResourceFiles(String path, String prefix) throws IOException {
        List<String> filenames = new ArrayList<>();

        InputStream in = this.getResourceAsStream(path);
        InputStreamReader sr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(sr);
        String resource;

        while ((resource = br.readLine()) != null) {
            if (!resource.contains(".")) {
                String newPath = path + "/" + resource;
                List<String> children = this.getResourceFiles(newPath, resource + "/");
                for (String child : children) {
                    filenames.add(prefix + child);
                }
            } else {
                filenames.add(prefix + resource);
            }
        }

        br.close();
        sr.close();
        in.close();

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        InputStream in = this.getContextClassLoader().getResourceAsStream(resource);
        return in == null ? this.getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
