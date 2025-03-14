package dev.latvian.mods.kubejs.create.recipe;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;

public class ItemApplicationRecipe extends ProcessingRecipe {
	public KubeRecipe keepHeldItem() {
		return setValue(ProcessingRecipeSchema.KEEP_HELD_ITEM, true);
	}
}
