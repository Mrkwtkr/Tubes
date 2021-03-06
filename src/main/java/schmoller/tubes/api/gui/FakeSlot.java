package schmoller.tubes.api.gui;

import java.util.List;

import schmoller.tubes.api.FilterRegistry;
import schmoller.tubes.api.interfaces.IFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Should be used in an ExtContainer.
 * A slot that does not take items, but ghosts them.
 */
public abstract class FakeSlot extends Slot
{
	private IFilter mFilter;
	private IFilter mLast;
	
	public FakeSlot(IFilter initial, int x, int y)
	{
		super(new InventoryBasic("", false, 1), 0, x, y);
		mFilter = initial;
		mLast = initial;
		inventory.setInventorySlotContents(0, toItem(initial));
	}
	
	public static ItemStack toItem(IFilter filter)
	{
		if(filter == null)
			return null;
		
		ItemStack item = new ItemStack(Blocks.stone, 1, 0);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound data = new NBTTagCompound();
		
		FilterRegistry.getInstance().writeFilter(filter, data);
		tag.setTag("Filter", data);
		item.setTagCompound(tag);
		
		return item;
	}
	
	public static IFilter fromItem(ItemStack item)
	{
		if(item == null || !item.hasTagCompound())
			return null;
		
		if(!item.getTagCompound().hasKey("Filter"))
			return null;
		
		return FilterRegistry.getInstance().readFilter(item.getTagCompound().getCompoundTag("Filter"));
	}
	
	@Override
	public final boolean canTakeStack( EntityPlayer player )
	{
		return false;
	}
	
	public final IFilter getFilter()
	{
		return mFilter;
	}
	
	public final void setFilter(IFilter filter)
	{
		mFilter = filter;
		inventory.setInventorySlotContents(0, toItem(filter));
		setIfChanged();
	}
	
	@Override
	public final ItemStack decrStackSize( int amount )
	{
		return super.decrStackSize(amount);
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return Integer.MAX_VALUE;
	}

	private void setIfChanged()
	{
		if((mFilter != null && (mLast == null || !mFilter.equals(mLast))) || (mFilter == null && mLast != null))
		{
			setValue(mFilter);
			mLast = (mFilter == null ? null : mFilter.copy());
		}
	}
	
	
	@Override
	public void onSlotChanged()
	{
		inventory.setInventorySlotContents(0, toItem(mFilter));
		setIfChanged();
	}
	
	@Override
	public final boolean isItemValid( ItemStack par1ItemStack ) { return false; }
	
	@Override
	public final void onPickupFromSlot( EntityPlayer player, ItemStack stack ) {}
	
	@Override
	public final void putStack( ItemStack item )
	{
		mFilter = fromItem(item);
		inventory.setInventorySlotContents(0, (mFilter == null ? null : item));
		
		setValue(mFilter);
	}
	
	protected abstract void setValue(IFilter filter);

	public List<String> getTooltip(List<String> existing)
	{
		return existing;
	}
	
	public boolean resetFilter() 
	{ 
		setFilter(null);
		return true;
	}
	public boolean shouldRespectSizes() { return true; }
	
	public boolean filterNeedsPayload() { return true; }
	
}
