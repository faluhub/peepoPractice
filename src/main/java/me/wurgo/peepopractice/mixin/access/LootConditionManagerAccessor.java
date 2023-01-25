package me.wurgo.peepopractice.mixin.access;

import com.google.gson.JsonElement;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(LootConditionManager.class)
public interface LootConditionManagerAccessor {
    @Invoker("apply") void invokeApply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler);
}
