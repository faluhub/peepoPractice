package me.falu.peepopractice.core.writer;

import com.google.gson.*;
import me.falu.peepopractice.PeepoPractice;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultFileWriter {
    public static final DefaultFileWriter INSTANCE = new DefaultFileWriter();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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
                try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("writer/" + resource)) {
                    if (stream != null) {
                        JsonParser parser = new JsonParser();
                        JsonElement defaultElement = parser.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        if (!destination.exists()) {
                            if (!destination.getParentFile().exists()) {
                                destination.getParentFile().mkdirs();
                            }
                            destination.createNewFile();
                            FileWriter writer = new FileWriter(destination);
                            writer.write(GSON.toJson(defaultElement));
                            writer.flush();
                            writer.close();
                        } else if (defaultElement.isJsonObject()) {
                            JsonElement element = parser.parse(FileUtils.readFileToString(destination, StandardCharsets.UTF_8));
                            if (element.isJsonObject()) {
                                JsonObject defaultObject = defaultElement.getAsJsonObject();
                                JsonObject object = element.getAsJsonObject();
                                boolean hasChanged = false;
                                for (Map.Entry<String, JsonElement> entry : defaultObject.entrySet()) {
                                    if (!object.has(entry.getKey())) {
                                        object.add(entry.getKey(), entry.getValue());
                                        hasChanged = true;
                                    }
                                }
                                if (hasChanged) {
                                    FileWriter writer = new FileWriter(destination);
                                    writer.write(GSON.toJson(object));
                                    writer.flush();
                                    writer.close();
                                }
                            }
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
