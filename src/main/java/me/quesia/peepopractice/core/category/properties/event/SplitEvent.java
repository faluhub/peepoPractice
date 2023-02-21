package me.quesia.peepopractice.core.category.properties.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.redlimerl.speedrunigt.option.SpeedRunOption;
import com.redlimerl.speedrunigt.option.SpeedRunOptions;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.TimerStatus;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.mixin.access.ServerWorldAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class SplitEvent {
    public static Map<String, SplitEvent> ID_TO_EVENT = new HashMap<>();
    private PracticeCategory category;

    @SuppressWarnings("StatementWithEmptyBody")
    public void complete(boolean completed) {
        if (this.category == null) { return; }

        new Thread(() -> {
            PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
            JsonObject object = writer.get();

            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null || client.getServer() == null) { return; }
            ServerPlayerEntity serverPlayerEntity = client.getServer().getPlayerManager().getPlayer(client.player.getUuid());
            if (serverPlayerEntity == null || serverPlayerEntity.getScoreboardTags().contains("completed")) { return; }

            InGameTimer timer = InGameTimer.getInstance();
            if (timer.isCompleted() || timer.getStatus().equals(TimerStatus.NONE)) { return; }
            InGameTimer.complete();
            long igt = timer.getInGameTime();

            boolean isPb;
            if (this.hasPb()) {
                long pb = getPbLong();
                isPb = igt <= pb;
            } else {
                isPb = true;
            }
            PeepoPractice.CATEGORY.putCustomValue("isCompletion", completed);
            if (completed) {
                JsonObject categoryObject = new JsonObject();
                JsonArray array = new JsonArray();
                if (object.has(this.category.getId())) {
                    categoryObject = object.get(this.category.getId()).getAsJsonObject();
                    if (categoryObject.has("completions")) {
                        array = categoryObject.get("completions").getAsJsonArray();
                    }
                }
                array.add(igt);
                categoryObject.add("completions", array);
                if (isPb) {
                    categoryObject.addProperty("pb", igt);
                }
                writer.put(this.category.getId(), categoryObject);
                writer.write();
            } else {
                this.incrementFailCount();
            }

            String time = getTimeString(igt);

            if (client.player != null) {
                client.inGameHud.setTitles(
                        completed
                                ? new LiteralText(Formatting.AQUA + "Completed"
                                + (isPb ? Formatting.YELLOW + " (PB!)" : ""))
                                : new LiteralText(Formatting.RED + "Failed"),
                        new LiteralText(Formatting.GRAY + time),
                        10,
                        100,
                        10
                );
                serverPlayerEntity.setGameMode(GameMode.SPECTATOR);
                float yaw = 0.0F;
                float pitch = 0.0F;
                BlockPos pos = client.getServer().getOverworld().getSpawnPos();
                RegistryKey<World> registryKey = client.getServer().getOverworld().getRegistryKey();
                if (this.category.hasWorldProperties() && this.category.getWorldProperties().hasWorldRegistryKey()) {
                    registryKey = this.category.getWorldProperties().getWorldRegistryKey();
                }
                if (this.category.hasPlayerProperties()) {
                    if (this.category.getPlayerProperties().hasSpawnAngle()) {
                        Vec2f angle = this.category.getPlayerProperties().getSpawnAngle();
                        yaw = angle.x;
                        pitch = angle.y;
                    }
                    if (this.category.getPlayerProperties().hasSpawnPos()) {
                        pos = this.category.getPlayerProperties().getSpawnPos();
                    }
                }

                while (((ServerWorldAccessor) serverPlayerEntity.getServerWorld()).getInEntityTick()) {}
                PeepoPractice.log("Done waiting for entity tick");
                serverPlayerEntity.teleport(client.getServer().getWorld(registryKey), pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
                serverPlayerEntity.addScoreboardTag("completed");

                if (completed) {
                    for (int i = 0; i < 5; i++) {
                        if (client.player != null) {
                            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 3.0F, 0.5F + .2F * (i + i / 4.0F));
                            try { TimeUnit.MILLISECONDS.sleep(180); }
                            catch (InterruptedException ignored) {}
                        }
                    }
                    if (isPb) {
                        try {
                            if (client.player != null) {
                                TimeUnit.MILLISECONDS.sleep(180);
                                client.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 3.0F, 1.0F);
                            }
                        } catch (InterruptedException ignored) {}
                    }
                } else {
                    for (int i = 0; i < 2; i++) {
                        if (client.player != null) {
                            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 3.0F, 1.2F - i * 0.5F);
                            try { TimeUnit.MILLISECONDS.sleep(180); }
                            catch (InterruptedException ignored) {}
                        }
                    }
                }
            }
        }).start();
    }

    public boolean hasPb() {
        PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
        JsonObject object = writer.get();
        if (object.has(this.category.getId())) {
            JsonObject categoryObject = object.get(this.category.getId()).getAsJsonObject();
            return categoryObject.has("pb");
        }
        return false;
    }

    public Long getPbLong() {
        PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
        JsonObject object = writer.get();
        if (object.has(this.category.getId())) {
            JsonObject categoryObject = object.get(this.category.getId()).getAsJsonObject();
            if (categoryObject.has("pb")) {
                return categoryObject.get("pb").getAsLong();
            }
        }
        return null;
    }

    public String getPbString() {
        return getTimeString(this.getPbLong());
    }

    public boolean hasCompletedTimes() {
        PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
        JsonObject object = writer.get();
        if (object.has(this.category.getId())) {
            JsonObject categoryObject = object.get(this.category.getId()).getAsJsonObject();
            return categoryObject.has("completions");
        }
        return false;
    }

    public int getCompletionCount() {
        if (this.hasCompletedTimes()) {
            PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
            JsonObject object = writer.get();
            JsonObject categoryObject = object.get(this.category.getId()).getAsJsonObject();
            JsonArray completions = categoryObject.get("completions").getAsJsonArray();
            return completions.size();
        }
        return 0;
    }

    public Long findAverage() {
        if (this.hasCompletedTimes()) {
            PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
            JsonObject object = writer.get();
            JsonObject categoryObject = object.get(this.category.getId()).getAsJsonObject();
            JsonArray completions = categoryObject.get("completions").getAsJsonArray();
            long total = 0;
            for (JsonElement element : completions) {
                total += element.getAsLong();
            }
            return total / completions.size();
        }
        return null;
    }

    public String getAverageString() {
        return getTimeString(this.findAverage());
    }

    public void clearTimes() {
        PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
        JsonObject config = writer.get();
        if (config.has(this.category.getId())) {
            writer.put(this.category.getId(), new JsonObject());
        }
    }

    public void incrementFailCount() {
        PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
        JsonObject config = writer.get();
        JsonObject categoryObject = new JsonObject();
        if (config.has(this.category.getId())) {
            categoryObject = config.get(this.category.getId()).getAsJsonObject();
        }
        int playCount = 1;
        if (categoryObject.has("fail_count")) {
            playCount = categoryObject.get("fail_count").getAsInt() + 1;
        }
        categoryObject.addProperty("fail_count", playCount);
        writer.put(this.category.getId(), categoryObject);
        writer.write();
    }

    public int getFailCount() {
        PracticeWriter writer = PracticeWriter.COMPLETIONS_WRITER;
        JsonObject config = writer.get();
        if (config.has(this.category.getId())) {
            JsonObject categoryObject = config.get(this.category.getId()).getAsJsonObject();
            if (categoryObject.has("fail_count")) {
                return categoryObject.get("fail_count").getAsInt();
            }
        }
        return 0;
    }

    public int getPlayCount() {
        return this.getCompletionCount() + this.getFailCount();
    }

    public static String getTimeString(long igt) {
        String time;
        SpeedRunOptions.TimerDecimals timerDecimals = SpeedRunOption.getOption(SpeedRunOptions.DISPLAY_DECIMALS);
        String millsString = String.format("%03d", igt % 1000).substring(0, timerDecimals.getNumber());
        int seconds = ((int) (igt / 1000)) % 60;
        int minutes = ((int) (igt / 1000)) / 60;
        if (minutes > 59) {
            int hours = minutes / 60;
            minutes = minutes % 60;
            if (timerDecimals == SpeedRunOptions.TimerDecimals.NONE) {
                time = String.format("%d:%02d:%02d", hours, minutes, seconds);
            } else {
                time = String.format("%d:%02d:%02d.%s", hours, minutes, seconds, millsString);
            }
        } else {
            if (timerDecimals == SpeedRunOptions.TimerDecimals.NONE) {
                time = String.format("%02d:%02d", minutes, seconds);
            } else {
                time = String.format("%02d:%02d.%s", minutes, seconds, millsString);
            }
        }
        return time;
    }

    public void setCategory(PracticeCategory category) {
        if (this.category == null) {
            this.category = category;
        }
    }
}
