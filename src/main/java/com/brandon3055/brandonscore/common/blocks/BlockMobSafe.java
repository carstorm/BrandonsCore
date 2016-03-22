package com.brandon3055.brandonscore.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by brandon3055 on 22/3/2016.
 * This is a base for any block that needs to be resistant to all mobs
 */
public class BlockMobSafe extends BlockBCore {

	public BlockMobSafe(Material material) {
		super(material);
	}

	//region Resistance
	@Override
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity) {return false;}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

	@Override
	public boolean canDropFromExplosion(Explosion explosionIn) {return false;}
	//endregion
}
