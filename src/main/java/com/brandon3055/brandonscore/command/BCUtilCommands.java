package com.brandon3055.brandonscore.command;

import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.LogHelperBC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 23/06/2017.
 */
public class BCUtilCommands extends CommandBase {

    @Override
    public String getName() {
        return "bcore_util";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/bcore_util help";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            help(sender);
            return;
        }

        try {


            String function = args[0];

            if (function.toLowerCase().equals("nbt")) {
                functionNBT(server, sender, args);
            }
            else if (function.equals("regenchunk")) {
                regenChunk(server, sender, args);
            }
            else if (function.equals("")) {

            }
            else if (function.equals("")) {

            }
            else if (function.equals("")) {

            }
            else if (function.equals("")) {

            }
            else {
                help(sender);
            }

        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new CommandException(e.getMessage());
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, "nbt", "regenchunk");
    }

    private void help(ICommandSender sender) {
        ChatHelper.message(sender, "The following are a list of Brandon's Core Utility Commands", new Style().setColor(TextFormatting.AQUA).setUnderlined(true));
        ChatHelper.message(sender, "/bcore_util nbt", TextFormatting.BLUE);
        ChatHelper.message(sender, "-Prints the NBT tag of the stack you are holding to chat and to the console.", TextFormatting.GRAY);
        ChatHelper.message(sender, "/bcore_util regenchunk [radius]", TextFormatting.BLUE);
        ChatHelper.message(sender, "-Regenerates the chunk(s) at your position.", TextFormatting.GRAY);
//        ChatHelper.message(sender, "/bcore_util", TextFormatting.BLUE);
//        ChatHelper.message(sender, "-", TextFormatting.GRAY);
//        ChatHelper.message(sender, "/bcore_util", TextFormatting.BLUE);
//        ChatHelper.message(sender, "-", TextFormatting.GRAY);
//        ChatHelper.message(sender, "/bcore_util", TextFormatting.BLUE);
//        ChatHelper.message(sender, "-", TextFormatting.GRAY);
    }


    private void functionNBT(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ItemStack stack = HandHelper.getMainFirst(player);
        if (stack.isEmpty()) {
            throw new CommandException("You are not holding an item!");
        }
        else if (!stack.hasTagCompound()) {
            throw new CommandException("That stack has no NBT tag!");
        }

        NBTTagCompound compound = stack.getTagCompound();
        LogHelperBC.logNBT(compound);
        StringBuilder builder = new StringBuilder();
        LogHelperBC.buildNBT(builder, compound, "", "Tag", false);
        String[] lines = builder.toString().split("\n");
        DataUtils.forEach(lines, s -> ChatHelper.message(sender, s, TextFormatting.GOLD));
    }


    //I dont like restarting minecraft! These are here so i can implement new functions runtime and just rename them later.
    private void regenChunk(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int rad = 0;
        if (args.length > 1) {
            rad = parseInt(args[1]);
        }

        LogHelperBC.dev(rad);

        for (int xOffset = -rad; xOffset <= rad; xOffset++) {
            for (int yOffset = -rad; yOffset <= rad; yOffset++) {
                WorldServer world = (WorldServer) sender.getEntityWorld();
                EntityPlayer player = getCommandSenderAsPlayer(sender);
                int chunkX = (int) player.chunkCoordX + xOffset;
                int chunkZ = (int) player.chunkCoordZ + yOffset;

                Chunk oldChunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                ChunkProviderServer chunkProviderServer = world.getChunkProvider();
                IChunkGenerator chunkGenerate = chunkProviderServer.chunkGenerator;

                Chunk newChunk = chunkGenerate.generateChunk(chunkX, chunkZ);

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < world.getHeight(); y++) {
                            BlockPos chunkPos = new BlockPos(x, y, z);
                            BlockPos absPos = new BlockPos(x + (chunkX * 16), y, z + (chunkZ * 16));
                            IBlockState newState = newChunk.getBlockState(chunkPos);
                            world.setBlockState(absPos, newState);

                            TileEntity tileEntity = newChunk.getTileEntity(chunkPos, Chunk.EnumCreateEntityType.IMMEDIATE);
                            if (tileEntity != null) {
                                world.setTileEntity(absPos, tileEntity);
                            }
                        }
                    }
                }

                oldChunk.setTerrainPopulated(false);
                oldChunk.populate(chunkProviderServer, chunkGenerate);

                PlayerChunkMap playerChunkMap = world.getPlayerChunkMap();
                if (playerChunkMap == null) {
                    return;
                }

                oldChunk.setModified(true);
                oldChunk.generateSkylightMap();

                PlayerChunkMapEntry watcher = playerChunkMap.getEntry(oldChunk.x, oldChunk.z);
                if (watcher != null) {
                    watcher.sendPacket(new SPacketChunkData(oldChunk, 65535));
                }
            }
        }
    }

    private void randomFunction2(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    }

    private void randomFunction3(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    }

    private void randomFunction4(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    }

    private void randomFunction5(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    }

}
