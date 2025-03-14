package dev.latvian.mods.kubejs.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NestedRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SequencedAssemblyRecipeSchema {
	RecipeKey<List<ItemStack>> RESULTS = ItemStackComponent.ITEM_STACK.asList().key("results", ComponentRole.OUTPUT);

	RecipeKey<ItemStack> INGREDIENT = ItemStackComponent.ITEM_STACK.key("ingredient", ComponentRole.INPUT);

	RecipeKey<List<KubeRecipe>> SEQUENCE = NestedRecipeComponent.RECIPE.asList().key("sequence", ComponentRole.INPUT);

	RecipeKey<ItemStack> TRANSITIONAL_ITEM = ItemStackComponent.ITEM_STACK.key("transitionalItem", ComponentRole.OTHER
	).optional(type -> AllItems.INCOMPLETE_PRECISION_MECHANISM.asStack(1));

	RecipeKey<Integer> LOOPS = NumberComponent.INT.key("loops", ComponentRole.OTHER).optional(4);

	class SequencedAssemblyRecipeJS extends KubeRecipe {
		@Override
		public void afterLoaded() {
			super.afterLoaded();
		}

		@Override
		public @Nullable Recipe<?> getOriginalRecipe() {
			return super.getOriginalRecipe();
		}
	}

	RecipeSchema SCHEMA = new RecipeSchema( RESULTS, INGREDIENT, SEQUENCE, TRANSITIONAL_ITEM, LOOPS).factory(new KubeRecipeFactory(
			AllRecipeTypes.SEQUENCED_ASSEMBLY.getId(),
			SequencedAssemblyRecipeJS.class,
			SequencedAssemblyRecipeJS::new
	));
}
