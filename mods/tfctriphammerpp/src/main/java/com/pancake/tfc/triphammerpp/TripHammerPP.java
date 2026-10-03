package com.pancake.tfc.triphammerpp;

import com.pancake.tfc.triphammerpp.mixin.TFCTripHammerBlockEntityAccessor;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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

    public static final int MAX_PROGRESS = 10;

    public static final float STANDARD_PROGRESS_PER_HIT = 20f;

    public static final Vec3 CENTER_OF_ORIGIN = new Vec3(.5, .5, .5);



    private static final TagKey<Item> PICKAXES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "pickaxes"));

    public static boolean isTargetEmpty(Level level, BlockPos targetPos, BlockEntity target) {
        BlockState targetBlockState = level.getBlockState(targetPos);
        return targetBlockState.isAir();
    }

    public static boolean isTargetRock(Level level, BlockPos targetPos, BlockEntity targetEntity) {

        BlockState targetBlockState = level.getBlockState(targetPos);

        return Helpers.isBlock(targetBlockState, TFCTags.Blocks.STONES_RAW)
                || Helpers.isBlock(targetBlockState, TFCTags.Blocks.STONES_HARDENED)
                || Helpers.isBlock(targetBlockState, TFCTags.Blocks.STONES_SMOOTH);
    }

    public static boolean canItemWorkAnvil(ItemStack item) {
        return Helpers.isItem(item, TFCTags.Items.TOOLS_HAMMER);
    }

    public static boolean canItemBreakRock(ItemStack item) {
        return Helpers.isItem(item, PICKAXES);
    }

    public static void handleRock(Level level,
                                  BlockPos pos,
                                  BlockPos targetPos,
                                  TripHammerBlockEntity tripHammer,
                                  ItemStack toolItem,
                                  Rotation rotation) {


        TFCTripHammer.LOGGER.debug("[TFCTripHammer++] handling rock target");

        BlockState targetBlockState = level.getBlockState(targetPos);

        // ROCK SMASH isn't the right sound, it's for knapping I think, we need the vanilla mining sound maybe ?
        level.playSound(null, pos, TFCSounds.ROCK_SMASH.get(), SoundSource.BLOCKS, 0.4f, 0.2f);

        if (workOnBreakingTheRock(level, targetPos, targetBlockState, tripHammer))
        {
            Helpers.damageItem(toolItem, level);
            tripHammer.markForSync();
        }
        if (toolItem.isEmpty())
        {
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS);
        }
        ((TFCTripHammerBlockEntityAccessor) tripHammer).tfctriphammerpp$setCooldownTicks(
                Mth.ceil(0.8f * Mth.TWO_PI / rotation.positiveSpeed())
        );

        // Update client if the hammer broke
        tripHammer.checkForLastTickSync();


    }

    public static int getDestroyProgress(TripHammerBlockEntity tripHammer){
        TFCTripHammerBlockEntityAccessor accessor =
                (TFCTripHammerBlockEntityAccessor) tripHammer;

        return accessor.tfctriphammerpp$getDestroyProgress();
    }

    public static void setDestroyProgress(TripHammerBlockEntity tripHammer, int newDestroyProgress){
        TFCTripHammerBlockEntityAccessor accessor =
                (TFCTripHammerBlockEntityAccessor) tripHammer;

        accessor.tfctriphammerpp$setDestroyProgress(newDestroyProgress);
    }

    public static void incrementDestroyProgress(TripHammerBlockEntity tripHammer, int amount){
        setDestroyProgress(tripHammer, getDestroyProgress(tripHammer) + amount);
    }

    private static boolean workOnBreakingTheRock(
            Level level,
            BlockPos breakingPos,
            BlockState stateToBreak,
            TripHammerBlockEntity tripHammer
    ) {

        int tripHammerBreakerId = 0; // TODO

        float blockHardness = stateToBreak.getDestroySpeed(level, breakingPos);
        TFCTripHammer.LOGGER.debug("[TFCTripHammer++] blockHardness: {}", blockHardness);

        TFCTripHammer.LOGGER.debug("[TFCTripHammer++] current progress: {}", getDestroyProgress(tripHammer));
        incrementDestroyProgress(tripHammer, Mth.clamp((int) (STANDARD_PROGRESS_PER_HIT / blockHardness), 1, MAX_PROGRESS - getDestroyProgress(tripHammer)));
        TFCTripHammer.LOGGER.debug("[TFCTripHammer++] current progress incremented: {}", getDestroyProgress(tripHammer));
        level.destroyBlockProgress(tripHammerBreakerId, breakingPos, getDestroyProgress(tripHammer));

        level.playSound(null, breakingPos, stateToBreak.getSoundType()
                .getHitSound(), SoundSource.BLOCKS, .25f, 1);

        if (getDestroyProgress(tripHammer) >= MAX_PROGRESS) {
            breakBlock(level, stateToBreak, breakingPos);
            TFCTripHammer.LOGGER.debug("[TFCTripHammer++] Rock broken ");

            setDestroyProgress(tripHammer, 0);
            level.destroyBlockProgress(tripHammerBreakerId, breakingPos, -1);
            return true;
        }

        return false;

    }

    public static void breakBlock(Level level, BlockState stateToBreak, BlockPos breakingPos) {
        if (!optimiseCobbleGen(stateToBreak))
            onBlockBroken(level, stateToBreak, breakingPos);

    }

    public static boolean optimiseCobbleGen(BlockState stateToBreak){
        return false; // TODO
    }

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
