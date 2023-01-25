package me.wurgo.peepopractice.mixin.access;

import com.google.gson.JsonElement;
import net.minecraft.loot.LootManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(LootManager.class)
public interface LootManagerAccessor {
    @Invoker("apply") void invokeApply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler);
}
