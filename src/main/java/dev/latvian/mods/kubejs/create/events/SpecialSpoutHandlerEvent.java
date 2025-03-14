package dev.latvian.mods.kubejs.create.events;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.create.platform.FluidIngredientHelper;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * @author Prunoideae
 */
public class SpecialSpoutHandlerEvent implements KubeEvent {
	@FunctionalInterface
	public interface SpoutHandler {
		long fillBlock(BlockContainerJS block, FluidStack fluid, boolean simulate);
	}

	public void add(ResourceLocation path, BlockStatePredicate block, SpoutHandler handler) {
		BlockSpoutingBehaviour.BY_BLOCK.registerProvider(FluidIngredientHelper.createSpoutingHandler(block, handler));
	}
}
