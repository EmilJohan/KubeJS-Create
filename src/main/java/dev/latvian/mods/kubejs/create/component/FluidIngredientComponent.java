package dev.latvian.mods.kubejs.create.component;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidIngredientComponent implements RecipeComponent<FluidIngredient> {
	public static final FluidIngredientComponent FLUID_INGREDIENT = new FluidIngredientComponent();

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof FluidIngredient || from instanceof FluidStack;
	}

	@Override
	public Codec<FluidIngredient> codec() {
		return FluidIngredient.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(FluidIngredient.class);
	}
}
