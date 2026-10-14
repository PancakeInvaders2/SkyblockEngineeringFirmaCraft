package com.pancake.tfc.triphammerpp.logic.server;

import com.pancake.tfc.triphammerpp.Config;
import com.pancake.tfc.triphammerpp.TripHammerProgressAccess;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.common.blocks.TripHammerBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TripHammerPP {

    // 0: not broken at all, 10: max level of displayable crack, 11: break the block
    public static final float MAX_PROGRESS = 11f;

    public static final Vec3 CENTER_OF_ORIGIN = new Vec3(.5, .5, .5);

    private static final TagKey<Item> PICKAXES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "pickaxes"));


    public static boolean isTargetEmpty(Level level, BlockPos targetPos) {
        BlockState targetBlockState = level.getBlockState(targetPos);
        return targetBlockState.isAir();
    }

    public static boolean isTargetRock(Level level, BlockPos targetPos) {

        BlockState targetBlockState = level.getBlockState(targetPos);

        return Helpers.isBlock(targetBlockState, TFCTags.Blocks.STONES_RAW)
                || Helpers.isBlock(targetBlockState, TFCTags.Blocks.STONES_HARDENED)
                || Helpers.isBlock(targetBlockState, TFCTags.Blocks.STONES_SMOOTH);
    }

    public static boolean isItemAHammer(ItemStack item) {
        return Helpers.isItem(item, TFCTags.Items.TOOLS_HAMMER);
    }

    public static boolean isItemAPickaxe(ItemStack item) {
        return Helpers.isItem(item, PICKAXES);
    }

    public static ItemStack getTool(TripHammerBlockEntity hammer){
        return hammer.getInventory().getStackInSlot(0);
    }

    public static BlockPos getTargetPosition(TripHammerBlockEntity hammer){

        if( isItemAPickaxe(getTool(hammer))){

            // to make the rendering of the pickaxe not collide into the bladed axle as it spins and the pickaxe is lifted,
            // we had to extend and bend up the handle, targeting the side of this block
            return hammer.getBlockPos()
                    .relative(hammer.getBlockState().getValue(TripHammerBlock.FACING))
                    .relative(hammer.getBlockState().getValue(TripHammerBlock.FACING))
                    .relative(hammer.getBlockState().getValue(TripHammerBlock.FACING))
                    .relative(Direction.UP);
        }
        else {
            return hammer.getBlockPos().relative(hammer.getBlockState().getValue(TripHammerBlock.FACING));
        }
    }

    public static void resetProgressIfTargetIsMissing(Level level, TripHammerBlockEntity hammer){

        BlockPos targetPos = getTargetPosition(hammer);

        if (getDestroyProgress(hammer) > 0 && isTargetEmpty(level, targetPos)) {
            resetDestroyProgress(level, hammer, targetPos);
        }
    }

    /**
     * @return true if the rock has been destroyed
     */
    public static boolean handleRock(Level level,
                                  BlockPos pos,
                                  BlockPos targetPos,
                                  TripHammerBlockEntity tripHammer,
                                  ItemStack toolItem,
                                  Rotation rotation) {


        BlockState targetBlockState = level.getBlockState(targetPos);

        boolean rockDestroyed = workOnBreakingTheRock(level, targetPos, targetBlockState, tripHammer, toolItem);

        return rockDestroyed;

    }

    public static float getDestroyProgress(TripHammerBlockEntity tripHammer){
        return ((TripHammerProgressAccess) tripHammer)
                .tfctriphammerpp$getDestroyProgress();
    }

    public static void setDestroyProgress(Level level, TripHammerBlockEntity tripHammer, BlockPos breakingPos, float newDestroyProgress){
        ((TripHammerProgressAccess) tripHammer)
                .tfctriphammerpp$setDestroyProgress(newDestroyProgress);
        int tripHammerBreakerId = ((TripHammerProgressAccess) tripHammer).tfctriphammerpp$getBreakerId();

        int displayedCracking = ((int) newDestroyProgress - 1 );
        // if displayedCracking > 9, the block is about to be destroyed, no need to update
        if(displayedCracking <= 9){
            // -1 : not destroyed at all
            // 0: lowest crack level displayable
            // 9: highest crack level displayable
            level.destroyBlockProgress(tripHammerBreakerId, breakingPos, displayedCracking);
        }


    }

    public static void incrementDestroyProgress(Level level, TripHammerBlockEntity tripHammer, BlockPos breakingPos, float amount){
        setDestroyProgress(level, tripHammer, breakingPos, getDestroyProgress(tripHammer) + amount);
    }

    public static void resetDestroyProgress(Level level, TripHammerBlockEntity tripHammer, BlockPos breakingPos){
        setDestroyProgress(level, tripHammer, breakingPos, 0f);
    }

    /**
     * @return true if the rock has been broken, false otherwise
     */
    private static boolean workOnBreakingTheRock(
            Level level,
            BlockPos breakingPos,
            BlockState stateToBreak,
            TripHammerBlockEntity tripHammer,
            ItemStack toolItem) {

        float blockHardness = stateToBreak.getDestroySpeed(level, breakingPos); // ~8 for hardened stone, 6 for raw stone

        float itemSpeed = toolItem.getDestroySpeed(stateToBreak); // 5.25 for copper pickaxe, 12.0 for red steel pickaxe


        float progress = (Config.PROGRESS_MULTIPLIER.getAsInt() * itemSpeed) / blockHardness;

        incrementDestroyProgress(level, tripHammer, breakingPos, Mth.clamp(progress, 1f, MAX_PROGRESS - getDestroyProgress(tripHammer)));

        level.playSound(null, breakingPos, stateToBreak.getSoundType()
                .getHitSound(), SoundSource.BLOCKS, .25f, 1);

        if (getDestroyProgress(tripHammer) >= MAX_PROGRESS) {
            breakBlock(level, stateToBreak, breakingPos);

            resetDestroyProgress(level, tripHammer, breakingPos);
            return true;
        }

        return false;

    }

    public static void breakBlock(Level level, BlockState stateToBreak, BlockPos breakingPos) {
        onBlockBroken(level, stateToBreak, breakingPos);
    }

    /**
     * Taken from Create's BlockBreakingKineticBlockEntity
     * (Create's code is MIT licenced)
     *
     * @param level
     * @param stateToBreak
     * @param breakingPos
     */
    public static void onBlockBroken(Level level, BlockState stateToBreak, BlockPos breakingPos) {
        Vec3 vec = vecHelperOffsetRandomly(vecHelperGetCenterOf(breakingPos), level.random, .125f);
        blockHelperDestroyBlock(level, breakingPos, 1f, (stack) -> {
            if (stack.isEmpty())
                return;
            if (!level.getGameRules()
                    .getBoolean(GameRules.RULE_DOBLOCKDROPS))
                return;
            if (level.restoringBlockSnapshots)
                return;

            ItemEntity itementity = new ItemEntity(level, vec.x, vec.y, vec.z, stack);
            itementity.setDefaultPickUpDelay();
            itementity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(itementity);
        });
    }




    public static void blockHelperDestroyBlock(Level world, BlockPos pos, float effectChance,
                                    Consumer<ItemStack> droppedItemCallback) {
        blockHelperDestroyBlockAs(world, pos, null, ItemStack.EMPTY, effectChance, droppedItemCallback);
    }

    public static void blockHelperDestroyBlockAs(Level level, BlockPos pos, @Nullable Player player, ItemStack usedTool,
                                      float effectChance, Consumer<ItemStack> droppedItemCallback) {
        FluidState fluidState = level.getFluidState(pos);
        BlockState state = level.getBlockState(pos);

        if (level.random.nextFloat() < effectChance)
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;

        if (player != null) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled())
                return;

            usedTool.mineBlock(level, state, pos, player);
            player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
        }

        if (level instanceof ServerLevel serverLevel && level.getGameRules()
                .getBoolean(GameRules.RULE_DOBLOCKDROPS) && !level.restoringBlockSnapshots
                && (player == null || !player.isCreative())) {
            List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, blockEntity, player, usedTool);

            BlockDropsEvent event = new BlockDropsEvent(serverLevel, pos, state, blockEntity, new ArrayList<>(), player, usedTool);
            NeoForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                if (event.getDroppedExperience() > 0) {
                    state.getBlock().popExperience(serverLevel, pos, event.getDroppedExperience());
                }
            }

            for (ItemStack itemStack : drops)
                droppedItemCallback.accept(itemStack);

            // Simulating IceBlock#playerDestroy. Not calling method directly as it would drop item
            // entities as a side-effect
            Registry<Enchantment> enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            if (state.getBlock() instanceof IceBlock && usedTool.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(Enchantments.SILK_TOUCH)) == 0) {
                if (!level.dimensionType().ultraWarm()) {
                    BlockState below = level.getBlockState(pos.below());
                    if (below.blocksMotion() || below.liquid()) {
                        fluidState = IceBlock.meltsInto().getFluidState();
                    }
                }
            }

            state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, false);
        }

        level.setBlockAndUpdate(pos, fluidState.createLegacyBlock());
    }

    public static Vec3 vecHelperGetCenterOf(Vec3i pos) {
        if (pos.equals(Vec3i.ZERO))
            return CENTER_OF_ORIGIN;
        return Vec3.atLowerCornerOf(pos)
                .add(.5f, .5f, .5f);
    }

    public static Vec3 vecHelperOffsetRandomly(Vec3 vec, RandomSource r, float radius) {
        return new Vec3(vec.x + (r.nextFloat() - .5f) * 2 * radius, vec.y + (r.nextFloat() - .5f) * 2 * radius,
                vec.z + (r.nextFloat() - .5f) * 2 * radius);
    }
}
