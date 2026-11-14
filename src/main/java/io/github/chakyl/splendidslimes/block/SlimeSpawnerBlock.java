package io.github.chakyl.splendidslimes.block;

import com.mojang.serialization.MapCodec;
import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.splendidslimes.blockentity.SlimeSpawnerBlockEntity;
import io.github.chakyl.splendidslimes.data.SlimeBreed;
import io.github.chakyl.splendidslimes.registry.ModElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class SlimeSpawnerBlock extends Block implements TickingEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public SlimeSpawnerBlock(Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, Direction.UP).setValue(FACING, pContext.getNearestLookingDirection().getOpposite()).setValue(OPEN, false);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SlimeSpawnerBlockEntity(pPos, pState);
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult hitResult) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (pHand == InteractionHand.MAIN_HAND && entity instanceof SlimeSpawnerBlockEntity) {
                SlimeSpawnerBlockEntity spawnerEntity = (SlimeSpawnerBlockEntity) entity;
                ItemStack heldItem = pPlayer.getItemInHand(pHand);
                // TODO: Components
                if (heldItem.getItem() == ModElements.Items.SPLENDID_SLIME_SPAWN_EGG.value() && heldItem.has(ModElements.Components.SLIME_BREED)) {
                    DynamicHolder<SlimeBreed> plortType = heldItem.get(ModElements.Components.SLIME_BREED);
                    if (plortType != null && plortType.isBound()) {
                        if (!pPlayer.isCreative()) heldItem.shrink(1);
                        pLevel.playSound(pPlayer, pPos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        spawnerEntity.setSlimeType(plortType.getId().toString());
                        return ItemInteractionResult.SUCCESS;
                    }
                }
                if (pPlayer.isCreative() && heldItem.getItem() == ModElements.Items.PLORT.value()) {
                    pLevel.playSound(pPlayer, pPos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    spawnerEntity.setSlimeCount(spawnerEntity.getSlimeCount() + 1);
                    return ItemInteractionResult.SUCCESS;
                }
                if (pPlayer.isCreative() && heldItem.getItem() == ModElements.Items.SLIME_HEART.value()) {
                    int currentCount = spawnerEntity.getSlimeCount();
                    pLevel.playSound(pPlayer, pPos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (currentCount > 1) {
                        spawnerEntity.setSlimeCount(currentCount - 1);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
            return ItemInteractionResult.FAIL;
        } else {
            return ItemInteractionResult.SUCCESS;
        }
    }



    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

}