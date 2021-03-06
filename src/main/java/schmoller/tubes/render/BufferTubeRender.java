package schmoller.tubes.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import schmoller.tubes.AdvRender.FaceMode;
import schmoller.tubes.api.TubeDefinition;
import schmoller.tubes.api.helpers.CommonHelper;
import schmoller.tubes.api.helpers.TubeHelper;
import schmoller.tubes.api.interfaces.IDirectionalTube;
import schmoller.tubes.api.interfaces.ITube;
import schmoller.tubes.definitions.TypeBufferTube;
import schmoller.tubes.types.BufferTube;

public class BufferTubeRender extends NormalTubeRender
{
	private ITube mTube;
	@Override
	public void renderStatic( TubeDefinition type, ITube tube, World world, int x, int y, int z )
	{
		int connections = tube.getConnections();
		int direction = ((IDirectionalTube)tube).getFacing();
		
		mRender.resetTransform();
		mRender.enableNormals = false;
		mRender.setLightingFromBlock(world, x, y, z);
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.faceMode = FaceMode.Both;
		
		mRender.setLocalLights(0.5f, 1.0f, 0.8f, 0.8f, 0.6f, 0.6f);
		
		mRender.translate(x, y, z);
		
		connections -= (connections & (1 << direction));
		
		int invCons = 0;
		
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				if(TubeHelper.renderAsInventoryConnection(world, x, y, z, i))
					invCons |= (1 << i);
			}
		}
		
		mTube = tube;
		
		int tubeCons = connections - invCons;

		renderCenter(((IDirectionalTube)tube).getFacing());
		renderConnections(tubeCons, type);
		
		renderInventoryConnections(1 << ((IDirectionalTube)tube).getFacing(), type);
	}
	
	@Override
	protected void renderConnections( int connections, TubeDefinition def )
	{
		mRender.setIcon(TypeBufferTube.center);
		
		for(int i = 0; i < 6; ++i)
		{
			if((connections & (1 << i)) != 0)
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				BufferTube other = CommonHelper.getMultiPart(mTube.world(), mTube.x() + dir.offsetX, mTube.y() + dir.offsetY, mTube.z() + dir.offsetZ, BufferTube.class);
				
				if(other != null && other.getFacing() != (i ^ 1))
					continue;
				
				mRender.resetTextureRotation();
				switch(i)
				{
				case 0: // Down
					mRender.setupBox(0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
					break;
				case 1: // Up
					mRender.setTextureRotation(0, 0, 2, 2, 2, 2);
					mRender.setupBox(0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
					break;
				case 2: // North
					mRender.setTextureRotation(0, 2, 0, 0, 0, 0);
					mRender.setupBox(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
					break;
				case 3: // South
					mRender.setTextureRotation(2, 0, 0, 0, 0, 0);
					mRender.setupBox(0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
					break;
				case 4: // West
					mRender.setTextureRotation(0, 0, 2, 0, 0, 0);
					mRender.setupBox(0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
					break;
				case 5: // East
					mRender.setTextureRotation(0, 0, 2, 0, 0, 0);
					mRender.setupBox(0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
					break;
				}
				
				mRender.drawFaces(63 - (1 << (i ^ 1))  - (1 << i));
			}
		}
		
		mRender.resetTextureRotation();
	}
	
	private void renderCenter(int facing)
	{
		mRender.setIcon(TypeBufferTube.center);
		mRender.drawBox(63 - (1 << facing), 0.125f, 0.125f, 0.125f, 0.875f, 0.875f, 0.875f);
		mRender.setIcon(TypeBufferTube.centerExport);
		mRender.drawBox(1 << facing, 0.125f, 0.125f, 0.125f, 0.875f, 0.875f, 0.875f);
	}
	
	@Override
	protected void renderCore( int connections, TubeDefinition def, int col ) {}
	
	@Override
	public void renderItem( TubeDefinition type, ItemStack item )
	{
		mRender.resetTransform();
		mRender.enableNormals = true;
		mRender.resetTextureFlip();
		mRender.resetTextureRotation();
		mRender.resetLighting(15728880);
		mRender.faceMode = FaceMode.Normal;
		
		mRender.setLocalLights(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
		
		Tessellator tes = Tessellator.instance;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		tes.startDrawingQuads();
		
		renderCenter(3);
		
		mRender.setIcon(type.getCenterIcon());
		mRender.drawBox(63, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		
		renderInventoryConnections(1 << 3, type);
		
		tes.draw();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
