//package io.github.chakyl.splendidslimes.block;
//
//import com.mojang.serialization.MapCodec;
//import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
//import io.github.chakyl.splendidslimes.blockentity.SlimeFeederBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.Container;
//import net.minecraft.world.Containers;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.context.BlockPlaceContext;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.HorizontalDirectionalBlock;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition.Builder;
//import net.minecraft.world.level.block.state.properties.BooleanProperty;
//import net.minecraft.world.phys.BlockHitResult;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.Shapes;
//import net.minecraft.world.phys.shapes.VoxelShape;
//
//public class SlimeFeederBlock extends HorizontalDirectionalBlock implements TickingEntityBlock {
//    public static final BooleanProperty HAS_FOOD = BooleanProperty.create("has_food");
//    public static final VoxelShape SHAPE = Shapes.or(box(0, 0, 0, 16, 7, 16));
//
//    public SlimeFeederBlock(Properties props) {
//        super(props);
//        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
//    }
//    @Override
//    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//        return SHAPE;
//    }
//
//    @Override
//    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
//        pBuilder.add(FACING, HAS_FOOD);
//    }
//
//    @Override
//    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
//        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(HAS_FOOD, false);
//    }
//
//    @Override
//    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
//        return new SlimeFeederBlockEntity(pPos, pState);
//    }
//
//
//    @Override
//    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//        if (!level.isClientSide) {
//            BlockEntity block = level.getBlockEntity(pos);
//            if (block instanceof SlimeFeederBlockEntity) {
//                player.openMenu((SlimeFeederBlockEntity) block);
//            }
//        }
//        return InteractionResult.SUCCESS;
//    }
//
//    @Override
//    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
//        if (state.getBlock() != newState.getBlock()) {
//            BlockEntity tileEntity = level.getBlockEntity(pos);
//            if (tileEntity instanceof Container) {
//                Containers.dropContents(level, pos, (Container) tileEntity);
//                level.updateNeighbourForOutputSignal(pos, this);
//            }
//            super.onRemove(state, level, pos, newState, isMoving);
//        }
//    }
//
//
//    @Override
//    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
//        return null;
//    }
//
//}