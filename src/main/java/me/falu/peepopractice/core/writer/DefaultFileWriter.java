package me.falu.peepopractice.core.writer;

import com.google.gson.*;
import me.falu.peepopractice.PeepoPractice;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
            List<String> resources = this.getResourceFiles();
            for (String resource : resources) {
                File destination = folder.toPath().resolve(resource.replace("writer/", "")).toFile();
                try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource)) {
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

    private List<String> getResourceFiles() {
        Reflections reflections = new Reflections("writer", new ResourcesScanner());
        Set<String> resources = reflections.getResources(Pattern.compile(".*\\.json"));
        return List.of(resources.toArray(new String[0]));
    }

    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    public File getResourceAsFile(String resourcePath, String name) {
        try {
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + name);
            if (tempFile.exists()) {
                return tempFile;
            } else {
                boolean ignored = tempFile.createNewFile();
            }
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                PeepoPractice.LOGGER.error("Couldn't open input stream for datapack '{}'.", name);
                return null;
            }
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            PeepoPractice.LOGGER.error("Couldn't get resource '{}' as file:", resourcePath, e);
        }
        return null;
    }
}
