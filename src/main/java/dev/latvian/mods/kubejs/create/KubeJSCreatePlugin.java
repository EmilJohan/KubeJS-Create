package dev.latvian.mods.kubejs.create;

import com.mojang.serialization.JsonOps;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.latvian.mods.kubejs.core.FluidStackKJS;
import dev.latvian.mods.kubejs.create.component.EitherRecipeComponent2;
import dev.latvian.mods.kubejs.create.component.FluidIngredientComponent;
import dev.latvian.mods.kubejs.create.component.ProcessingOutputComponent;
import dev.latvian.mods.kubejs.create.events.BoilerHeaterHandlerEvent;
import dev.latvian.mods.kubejs.create.events.CreateEvents;
import dev.latvian.mods.kubejs.create.events.SpecialFluidHandlerEvent;
import dev.latvian.mods.kubejs.create.events.SpecialSpoutHandlerEvent;
import dev.latvian.mods.kubejs.create.recipe.ItemApplicationRecipe;
import dev.latvian.mods.kubejs.create.recipe.ProcessingRecipe;
import dev.latvian.mods.kubejs.create.recipe.ProcessingRecipeSchema;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.Registries;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSCreatePlugin implements KubeJSPlugin {

	private static KubeRecipeFactory getFactory(AllRecipeTypes recipeType) {
		return new KubeRecipeFactory(recipeType.getId(), ProcessingRecipe.class, ProcessingRecipe::new);
	}

	private static final Map<AllRecipeTypes, KubeRecipeFactory> FACTORIES = Map.of(
			AllRecipeTypes.ITEM_APPLICATION, new KubeRecipeFactory(AllRecipeTypes.ITEM_APPLICATION.getId(), ItemApplicationRecipe.class, ItemApplicationRecipe::new)
	);

	private static final Map<AllRecipeTypes, RecipeSchema> SCHEMAS = Map.of(
			AllRecipeTypes.DEPLOYING, ProcessingRecipeSchema.ITEM_APPLICATION,
			AllRecipeTypes.ITEM_APPLICATION, ProcessingRecipeSchema.ITEM_APPLICATION,
//			AllRecipeTypes.MIXING, ProcessingRecipeSchema.PROCESSING_UNWRAPPED,
//			AllRecipeTypes.COMPACTING, ProcessingRecipeSchema.PROCESSING_UNWRAPPED,
			AllRecipeTypes.CRUSHING, ProcessingRecipeSchema.PROCESSING_WITH_TIME,
			AllRecipeTypes.CUTTING, ProcessingRecipeSchema.PROCESSING_WITH_TIME,
			AllRecipeTypes.MILLING, ProcessingRecipeSchema.PROCESSING_WITH_TIME
	);

	@Override
	public void registerBuilderTypes(BuilderTypeRegistry registry) {
//		registry.of(Registries.ITEM, cb -> {
//			cb.add("create:sequenced_assembly", SequencedAssemblyItemBuilder.class, SequencedAssemblyItemBuilder::new);
//		});
	}

	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(CreateEvents.GROUP);
	}

	@Override
	public void afterInit() {
		CreateEvents.BOILER_HEATER.post(ScriptType.STARTUP, new BoilerHeaterHandlerEvent());
		CreateEvents.SPECIAL_FLUID.post(ScriptType.STARTUP, new SpecialFluidHandlerEvent());
		CreateEvents.SPECIAL_SPOUT.post(ScriptType.STARTUP, new SpecialSpoutHandlerEvent());
	}

	@Override
	public void registerTypeWrappers(TypeWrapperRegistry registry) {
		registry.register(FluidIngredient.class, this::wrapFluidIngredient);
		registry.register(ProcessingOutput.class, ProcessingOutputComponent.Wrapper::of);
	}

	private FluidIngredient wrapFluidIngredient(Context cx, Object from, TypeInfo target) {
		if (from instanceof FluidStackKJS fluidStackKJS) {
			return FluidIngredient.fromFluidStack(fluidStackKJS.kjs$self());
		} else if (from instanceof Map<?,?> map && (map.containsKey("fluid") || map.containsKey("fluidTag"))) {
			return FluidIngredient.CODEC.decode(JsonOps.INSTANCE, MapJS.json(cx, map)).getOrThrow().getFirst();
		} else {
			var registries = RegistryAccessContainer.of(cx);
			return FluidIngredient.fromFluidStack(FluidWrapper.wrap(registries, from));
		}
	}

	@Override
	public void registerRecipeComponents(RecipeComponentFactoryRegistry registry) {
		registry.register(FluidIngredientComponent.FLUID_INGREDIENT);
		registry.register(ProcessingOutputComponent.PROCESSING_OUTPUT);
		registry.register("either2", EitherRecipeComponent2.FACTORY);
	}

	@Override
	public void registerRecipeFactories(RecipeFactoryRegistry registry) {
		for (var createRecipeType : AllRecipeTypes.values()) {
			if (!(createRecipeType.getSerializer() instanceof ProcessingRecipeSerializer<?> serializer)) {
				continue;
			}

			registry.register(
					FACTORIES.getOrDefault(
							createRecipeType,
							getFactory(createRecipeType)
					)
			);
		}
	}

	@Override
	public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
		registry.register(AllRecipeTypes.MECHANICAL_CRAFTING.getId(), registry.namespace("minecraft").get("shaped").schema);

//		registry.register(AllRecipeTypes.SEQUENCED_ASSEMBLY.getId(), SequencedAssemblyRecipeSchema.SCHEMA);

		for (var createRecipeType : AllRecipeTypes.values()) {
			if (createRecipeType.getSerializer() instanceof ProcessingRecipeSerializer<?> serializer) {
				var factory = FACTORIES.getOrDefault(createRecipeType, getFactory(createRecipeType));
				var schema = SCHEMAS.getOrDefault(createRecipeType, ProcessingRecipeSchema.PROCESSING_DEFAULT).factory(factory);

				registry.register(
						createRecipeType.getId(),
						schema
				);
			}
		}
	}
}