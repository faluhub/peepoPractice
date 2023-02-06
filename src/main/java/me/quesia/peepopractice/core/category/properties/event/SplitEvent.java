package me.quesia.peepopractice.core.category.properties.event;

import com.google.gson.JsonObject;
import com.redlimerl.speedrunigt.option.SpeedRunOption;
import com.redlimerl.speedrunigt.option.SpeedRunOptions;
import com.redlimerl.speedrunigt.timer.InGameTimer;
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

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;

public class SplitEvent {
    private PracticeCategory category;

    public void complete(boolean completed) {
        new Thread(() -> this.endSplit(completed)).start();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void endSplit(boolean completed) {
        if (this.category == null) { return; }

        PracticeWriter writer = PracticeWriter.PB_WRITER;
        JsonObject object = writer.get();

        InGameTimer timer = InGameTimer.getInstance();
        if (timer.isCompleted()) { return; }
        long igt = timer.getInGameTime();
        InGameTimer.complete();

        boolean isPb;
        if (this.hasPb()) {
            long pb = object.get(this.category.getId()).getAsLong();
            isPb = igt <= pb;
        } else {
            isPb = true;
        }
        if (isPb && completed) {
            writer.put(this.category.getId(), igt);
            writer.write();
        }

        String time = getTimeString(igt);
        MinecraftClient client = MinecraftClient.getInstance();

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
            if (client.getServer() != null) {
                ServerPlayerEntity serverPlayerEntity = client.getServer().getPlayerManager().getPlayer(client.player.getUuid());
                if (serverPlayerEntity != null) {
                    serverPlayerEntity.setGameMode(GameMode.SPECTATOR);
                    float yaw = 0.0F;
                    float pitch = 0.0F;
                    BlockPos pos = client.getServer().getOverworld().getSpawnPos();
                    RegistryKey<World> registryKey = client.getServer().getOverworld().getRegistryKey();
                    if (this.category.hasWorldProperties() && this.category.getWorldProperties().hasWorldRegistryKey()) {
                        registryKey = this.category.getWorldProperties().getWorldRegistryKey();
                    }
                    if (this.category.hasPlayerProperties()) {
                        if (this.category.getPlayerProperties().getSpawnAngle() != null) {
                            Vec2f angle = this.category.getPlayerProperties().getSpawnAngle();
                            yaw = angle.x;
                            pitch = angle.y;
                        }
                        if (this.category.getPlayerProperties().getSpawnPos() != null) {
                            pos = this.category.getPlayerProperties().getSpawnPos();
                        }
                    }

                    while (((ServerWorldAccessor) serverPlayerEntity.getServerWorld()).getInEntityTick()) {}
                    PeepoPractice.log("Done waiting for entity tick");
                    serverPlayerEntity.teleport(client.getServer().getWorld(registryKey), pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
                    serverPlayerEntity.addScoreboardTag("completed");

                    if (completed) {
                        new Thread(() -> {
                            for (int i = 0; i < 5; i++) {
                                try {
                                    if (client.player != null) {
                                        while (((ServerWorldAccessor) serverPlayerEntity.getServerWorld()).getInEntityTick()) {}
                                        client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 3.0F, 0.5F + .2F * (i + i / 4.0F));
                                        try { TimeUnit.MILLISECONDS.sleep(180); }
                                        catch (InterruptedException ignored) {}
                                    }
                                } catch (ConcurrentModificationException ignored) {}
                            }
                            if (isPb) {
                                try {
                                    if (client.player != null) {
                                        while (((ServerWorldAccessor) serverPlayerEntity.getServerWorld()).getInEntityTick()) {}
                                        TimeUnit.MILLISECONDS.sleep(180);
                                        client.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 3.0F, 1.0F);
                                    }
                                } catch (InterruptedException | ConcurrentModificationException ignored) {}
                            }
                        }).start();
                    } else {
                        new Thread(() -> {
                            for (int i = 0; i < 2; i++) {
                                try {
                                    if (client.player != null) {
                                        while (((ServerWorldAccessor) serverPlayerEntity.getServerWorld()).getInEntityTick()) {}
                                        client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 3.0F, 1.2F - i * 0.5F);
                                        try { TimeUnit.MILLISECONDS.sleep(180); }
                                        catch (InterruptedException ignored) {}
                                    }
                                } catch (ConcurrentModificationException ignored) {}
                            }
                        }).start();
                    }
                }
            }
        }
    }

    public boolean hasPb() {
        return PracticeWriter.PB_WRITER.get().has(this.category.getId());
    }

    public long getPbLong() {
        return PracticeWriter.PB_WRITER.get().get(this.category.getId()).getAsLong();
    }

    public String getPbString() {
        return getTimeString(this.getPbLong());
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
