package me.wurgo.peepopractice.core.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.wurgo.peepopractice.PeepoPractice;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonDataReader {
    private static final String FILE_SUFFIX;
    private static final int FILE_SUFFIX_LENGTH;
    private static final String DEFAULT_NAMESPACE;
    private static final Gson GSON;
    private static final NamespaceResourceManager RESOURCE_MANAGER;

    static {
        FILE_SUFFIX = ".json";
        FILE_SUFFIX_LENGTH = FILE_SUFFIX.length();
        DEFAULT_NAMESPACE = "minecraft";
        GSON = new Gson();
        RESOURCE_MANAGER = new NamespaceResourceManager(ResourceType.SERVER_DATA, DEFAULT_NAMESPACE);

        RESOURCE_MANAGER.addPack(new DefaultResourcePack());
    }

    public static Map<Identifier, JsonElement> prepareData(ResourceDataType dataType) {
        Map<Identifier, JsonElement> map = Maps.newHashMap();
        int i = dataType.value.length() + 1;

        for (Identifier identifier : RESOURCE_MANAGER.findResources(dataType.value, (stringx) -> stringx.endsWith(".json"))) {
            String string = identifier.getPath();
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(i, string.length() - FILE_SUFFIX_LENGTH));

            try {
                Resource resource = RESOURCE_MANAGER.getResource(identifier);
                Throwable var10 = null;

                try {
                    InputStream inputStream = resource.getInputStream();
                    Throwable var12 = null;

                    try {
                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        Throwable var14 = null;

                        try {
                            JsonElement jsonElement = JsonHelper.deserialize(GSON, reader, JsonElement.class);
                            if (jsonElement != null) {
                                JsonElement jsonElement2 = map.put(identifier2, jsonElement);
                                if (jsonElement2 != null) {
                                    throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
                                }
                            } else {
                                PeepoPractice.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier2, identifier);
                            }
                        } catch (Throwable var62) {
                            var14 = var62;
                            throw var62;
                        } finally {
                            if (var14 != null) {
                                try {
                                    reader.close();
                                } catch (Throwable var61) {
                                    var14.addSuppressed(var61);
                                }
                            } else {
                                reader.close();
                            }
                        }
                    } catch (Throwable var64) {
                        var12 = var64;
                        throw var64;
                    } finally {
                        if (inputStream != null) {
                            if (var12 != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable var60) {
                                    var12.addSuppressed(var60);
                                }
                            } else {
                                inputStream.close();
                            }
                        }

                    }
                } catch (Throwable var66) {
                    var10 = var66;
                    throw var66;
                } finally {
                    if (resource != null) {
                        if (var10 != null) {
                            try {
                                resource.close();
                            } catch (Throwable var59) {
                                var10.addSuppressed(var59);
                            }
                        } else {
                            resource.close();
                        }
                    }

                }
            } catch (IllegalArgumentException | IOException | JsonParseException var68) {
                PeepoPractice.LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, var68);
            }
        }

        return map;
    }
}
