package schmoller.tubes;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import schmoller.tubes.api.Payload;
import schmoller.tubes.api.SizeMode;
import schmoller.tubes.api.TubeItem;
import schmoller.tubes.api.helpers.RenderHelper;
import schmoller.tubes.api.interfaces.IFilter;

public class AnyFilter implements IFilter
{
	public static ResourceLocation texture = new ResourceLocation("tubes", "textures/gui/anyFilter.png");
	private int mMax;
	private int mValue;
	
	public AnyFilter(int value) { this(value, 64); }
	
	public AnyFilter(int value, int max)
	{
		assert(max > 0);
		mValue = value;
		mMax = max;
	}
	
	@Override
	public String getType()
	{
		return "any";
	}
	
	@Override
	public Class<? extends Payload> getPayloadType()
	{
		return null;
	}
	
	@Override
	public boolean matches( Payload payload, SizeMode mode )
	{
		if(mValue > payload.maxSize() && payload.size() == payload.maxSize())
			return true;
		
		switch(mode)
		{
		case Max:
			return true;
		case Exact:
			return payload.size() == mValue;
		case GreaterEqual:
			return payload.size() >= mValue;
		case LessEqual:
			return payload.size() <= mValue;
		}
		
		return true;
	}
	@Override
	public boolean matches( TubeItem item, SizeMode mode )
	{
		return matches(item.item, mode);
	}
	
	@Override
	public void increase( boolean useMax, boolean shift )
	{
		mValue += (shift ? 10 : 1);
		if(mValue > mMax && useMax)
			mValue = mMax;
	}

	@Override
	public void decrease( boolean shift )
	{
		mValue -= (shift ? 10 : 1);
		
		if(mValue < 0)
			mValue = 0;
	}

	@Override
	public int getMax()
	{
		return mMax;
	}
	
	@Override
	public int size()
	{
		return mValue;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFilter( int x, int y )
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		RenderHelper.renderRect(x, y, 16, 16, 0, 0, 1, 1);
		
		String text = String.valueOf(mValue);
		
        int fwidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        
        GL11.glPushMatrix();
        
        GL11.glTranslated(x + 17 - fwidth, y + 9, 0);
        if(fwidth > 16)
        {
        	GL11.glTranslatef(fwidth / 2 - 1, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f - 1, 0);
        	GL11.glScalef(0.5f, 0.5f, 0.5f);
        }
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, 0, 0, 16777215);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        GL11.glPopMatrix();
	}

	@Override
	public void write( NBTTagCompound tag )
	{
		tag.setInteger("val", mValue);
		tag.setInteger("max", mMax);
	}
	
	@Override
	public void write( MCDataOutput output )
	{
		output.writeShort(mValue);
		output.writeShort(mMax);
	}
	
	public static AnyFilter from(NBTTagCompound tag)
	{
		return new AnyFilter(tag.getInteger("val"), tag.getInteger("max"));
	}
	
	public static AnyFilter from(MCDataInput input)
	{
		return new AnyFilter(input.readShort(), input.readShort());
	}
	
	@Override
	public IFilter copy()
	{
		return new AnyFilter(mValue, mMax);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getTooltip( List<String> current )
	{
		return Arrays.asList(StatCollector.translateToLocal("gui.filter.any"));
	}

	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof AnyFilter))
			return false;
		
		AnyFilter other = (AnyFilter)obj;
		
		return mValue == other.mValue && mMax == other.mMax;
	}
}
