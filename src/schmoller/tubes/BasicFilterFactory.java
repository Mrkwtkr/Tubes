package schmoller.tubes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import codechicken.lib.data.MCDataInput;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.InventoryHelper;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.api.interfaces.IFilterFactory;

public class BasicFilterFactory implements IFilterFactory
{
	@Override
	public IFilter getFilterFrom( ItemStack heldItem, IFilter existing, int button, boolean shift, boolean ctrl )
	{
		if(existing instanceof ItemFilter)
		{
			ItemStack stack = ((ItemFilter)existing).getItem();
			
			if(InventoryHelper.areItemsEqual(heldItem, stack))
			{
				if (FluidContainerRegistry.isContainer(heldItem))
					return new FluidFilter(FluidContainerRegistry.getFluidForFilledItem(heldItem));
				if (CommonHelper.getDyeIndex(heldItem) != -1)
					return new ColorFilter(CommonHelper.getDyeIndex(heldItem));
				
				return null;
			}
			else
				return new ItemFilter(heldItem, ctrl);
		}
		else if(heldItem != null)
			return new ItemFilter(heldItem, ctrl);
		
		return null;
	}

	@Override
	public IFilter loadFilter( String filterName, NBTTagCompound tag )
	{
		if(filterName.equals("any"))
			return AnyFilter.from(tag);
		if(filterName.equals("item"))
			return ItemFilter.from(tag);
		if(filterName.equals("fluid"))
			return FluidFilter.from(tag);
		if(filterName.equals("color"))
			return ColorFilter.from(tag);
		
		return null;
	}

	@Override
	public IFilter loadFilter( String filterName, MCDataInput input )
	{
		if(filterName.equals("any"))
			return AnyFilter.from(input);
		if(filterName.equals("item"))
			return ItemFilter.from(input);
		if(filterName.equals("fluid"))
			return FluidFilter.from(input);
		if(filterName.equals("color"))
			return ColorFilter.from(input);
		
		return null;
	}

}
