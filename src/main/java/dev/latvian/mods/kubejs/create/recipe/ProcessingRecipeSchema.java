package dev.latvian.mods.kubejs.create.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.latvian.mods.kubejs.create.component.EitherRecipeComponent2;
import dev.latvian.mods.kubejs.create.component.FluidIngredientComponent;
import dev.latvian.mods.kubejs.create.component.ProcessingOutputComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.EitherRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentWithParent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ProcessingRecipeSchema {
	RecipeKey<List<Either<FluidStack, ProcessingOutput>>> RESULTS = new EitherRecipeComponent2<>(
			FluidStackComponent.FLUID_STACK,
			ProcessingOutputComponent.PROCESSING_OUTPUT
	).asList().key("results", ComponentRole.OTHER);

	RecipeKey<List<Either<Ingredient, FluidIngredient>>> INGREDIENTS = new EitherRecipeComponent2<>(
			IngredientComponent.INGREDIENT,
			FluidIngredientComponent.FLUID_INGREDIENT
	).asList().key("ingredients", ComponentRole.INPUT);

//	RecipeKey<List<Either<FluidStack, ItemStack>>> INGREDIENTS_UNWRAPPED = new RecipeComponentWithParent<List<Either<FluidStack, ItemStack>>>() {
//
//		@Override
//		public RecipeComponent<List<Either<FluidStack, ItemStack>>> parentComponent() {
//			return INGREDIENTS.component;
//		}
//
//		@Override
//		public Codec<List<Either<FluidStack, ItemStack>>> codec() {
//			return RecipeComponentWithParent.super.codec().xmap(
//					e -> e.stream().flatMap(this::unwrapItemStacks).toList(),
//					Function.identity()
//			);
//		}
//
//		private Stream<Either<FluidStack, ItemStack>> unwrapItemStacks(
//				Either<FluidStack, ItemStack> either
//		) {
//			if (either.left().isPresent()) {
//				return Stream.of(either);
//			} else if (either.right().isPresent()) {
//				ItemStack stack = either.right().get();
//				List<Either<FluidStack, ItemStack>> list = new ArrayList<>();
//				for (int i = 0; i < stack.getCount(); i++) {
//					list.add(Either.right(stack.copyWithCount(1)));
//				}
//				return list.stream();
//			} else {
//				return Stream.of();
//			}
//		}
//	}.key("ingredients", ComponentRole.INPUT);

	RecipeKey<TickDuration> PROCESSING_TIME = TimeComponent.TICKS.key("processing_time", ComponentRole.OTHER)
			.optional(new TickDuration(100L));

	// specifically for crushing, cutting, and milling
	RecipeKey<TickDuration> PROCESSING_TIME_REQUIRED = TimeComponent.TICKS.key("processing_time_required", ComponentRole.OTHER)
			.optional(new TickDuration(100L));

	RecipeKey<HeatCondition> HEAT_REQUIREMENT = EnumComponent.of("heat_requirement", HeatCondition.class, HeatCondition.CODEC).key("heatRequirement", ComponentRole.OTHER).defaultOptional().allowEmpty();

	RecipeKey<Boolean> KEEP_HELD_ITEM = BooleanComponent.BOOLEAN.key("keepHeldItem", ComponentRole.OTHER).optional(false);

	// TODO: set factories
	RecipeSchema ITEM_APPLICATION = new RecipeSchema(RESULTS, INGREDIENTS, PROCESSING_TIME, HEAT_REQUIREMENT, KEEP_HELD_ITEM);

	// TODO: reimplement INGREDIENTS_UNWRAPPED
//	RecipeSchema PROCESSING_UNWRAPPED = new RecipeSchema(RESULTS, INGREDIENTS_UNWRAPPED, PROCESSING_TIME, HEAT_REQUIREMENT);

	RecipeSchema PROCESSING_WITH_TIME = new RecipeSchema(RESULTS, INGREDIENTS, PROCESSING_TIME_REQUIRED, HEAT_REQUIREMENT);

	RecipeSchema PROCESSING_DEFAULT = new RecipeSchema(RESULTS, INGREDIENTS, PROCESSING_TIME, HEAT_REQUIREMENT);
}
