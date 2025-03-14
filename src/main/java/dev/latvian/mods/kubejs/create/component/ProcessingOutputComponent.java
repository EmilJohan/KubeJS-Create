package dev.latvian.mods.kubejs.create.component;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.create.KubeJSCreate;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class ProcessingOutputComponent implements RecipeComponent<ProcessingOutput> {
	public static final ProcessingOutputComponent PROCESSING_OUTPUT = new ProcessingOutputComponent();

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return ItemStackJS.isItemStackLike(from);
	}

	@Override
	public Codec<ProcessingOutput> codec() {
		return ProcessingOutput.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(ProcessingOutput.class);
	}

	private static ProcessingOutput identityForBreakpoint(ProcessingOutput output) {
		return output;
	}

	@Override
	public ProcessingOutput wrap(Context cx, KubeRecipe recipe, Object from) {
		return Wrapper.of(from);
	}

	public interface Wrapper {
		static ProcessingOutput of(Object from) {
			return switch (from) {
				case null -> throw new IllegalArgumentException("Null processing output");
				case ProcessingOutput output -> output;
				case ItemStack is -> new ProcessingOutput(is, 1.0f);
				default -> {
					var str = from.toString();

					if (str.isEmpty()) {
						throw new IllegalArgumentException("Empty processing output");
					}

					var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(str));

					yield new ProcessingOutput(new ItemStack(item), 1.0f);
				}
			};
		}
	}
}
