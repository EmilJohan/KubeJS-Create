package dev.latvian.mods.kubejs.create.platform;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.core.FluidStackKJS;
import dev.latvian.mods.kubejs.create.events.SpecialFluidHandlerEvent;
import dev.latvian.mods.kubejs.create.events.SpecialSpoutHandlerEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class FluidIngredientHelper {
	public static FluidIngredient toFluidIngredient(FluidStackKJS fluidStack) {
		return FluidIngredient.fromFluidStack(fluidStack.kjs$self());
	}

	public static SimpleRegistry.Provider<Fluid, OpenPipeEffectHandler> createEffectHandler(FluidIngredient fluidIngredient, SpecialFluidHandlerEvent.PipeHandler handler) {
		return new SimpleRegistry.Provider<>() {
			final FluidIngredient filter = fluidIngredient;
			Set<Fluid> validFluids = null;

			final OpenPipeEffectHandler internalHandler = (level, aabb, fluid) -> {
				if (filter.test(fluid)) {
					handler.apply(level, aabb, fluid);
				}
			};

			@Override
			public @Nullable OpenPipeEffectHandler get(Fluid fluidIn) {
				if (validFluids.contains(fluidIn)) {
					return internalHandler;
				} else {
					return null;
				}
			}

			private Set<Fluid> getValidFluids() {
				if (validFluids == null) {
					Set<Fluid> set = new HashSet<>();
					for (FluidStack fluidStack : fluidIngredient.getMatchingFluidStacks()) {
						Fluid fluid = fluidStack.getFluid();
						set.add(fluid);
					}
					validFluids = set;
				}
				return validFluids;
			}

			@Override
			public void onRegister(Runnable invalidate) {
				NeoForge.EVENT_BUS.addListener((TagsUpdatedEvent event) -> {
					if (event.shouldUpdateStaticData()) {
						invalidate.run();
						filter.matchingFluidStacks = null;
						validFluids = null;
					}
				});
			}
		};
	}

	public static SimpleRegistry.Provider<Block, BlockSpoutingBehaviour> createSpoutingHandler(BlockStatePredicate block, SpecialSpoutHandlerEvent.SpoutHandler handler) {
		final BlockSpoutingBehaviour internalHandler = (world, pos, spout, availableFluid, simulate) -> {
			if (!block.test(world.getBlockState(pos))) {
				return 0;
			}
			return (int) handler.fillBlock(new BlockContainerJS(world, pos), availableFluid, simulate);
		};
		return blockIn -> block.testBlock(blockIn) ? internalHandler : null;
	}
}
