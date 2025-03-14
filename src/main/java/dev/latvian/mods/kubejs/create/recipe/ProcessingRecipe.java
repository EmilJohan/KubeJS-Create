package dev.latvian.mods.kubejs.create.recipe;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

import java.util.Map;

public class ProcessingRecipe extends KubeRecipe {
	public KubeRecipe heated() {
		return setValue(ProcessingRecipeSchema.HEAT_REQUIREMENT, HeatCondition.HEATED);
	}

	public KubeRecipe superheated() {
		return setValue(ProcessingRecipeSchema.HEAT_REQUIREMENT, HeatCondition.SUPERHEATED);
	}
}
