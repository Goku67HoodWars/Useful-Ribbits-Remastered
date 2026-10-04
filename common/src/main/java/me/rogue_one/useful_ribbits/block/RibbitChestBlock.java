package me.rogue_one.useful_ribbits.block;

import me.rogue_one.useful_ribbits.block.entity.RibbitChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RibbitChestBlock extends Block implements EntityBlock {
   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

   public RibbitChestBlock(BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   @Override
   protected RenderShape getRenderShape(BlockState state) {
      // Rendered by the GeckoLib RibbitChestRenderer (animated lid); the static model is item-only.
      return RenderShape.INVISIBLE;
   }

   @Override
   protected boolean propagatesSkylightDown(BlockState state) {
      return true;
   }

   @Override
   protected VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   @Override
   protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(FACING);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   protected BlockState rotate(BlockState state, Rotation rot) {
      return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
      if (entity instanceof ServerPlayer player && world.getBlockEntity(pos) instanceof RibbitChestBlockEntity chest) {
         player.openMenu(chest);
      }

      return InteractionResult.SUCCESS;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new RibbitChestBlockEntity(pos, state);
   }

   @Override
   protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
      if (world.getBlockEntity(pos) instanceof RibbitChestBlockEntity be) {
         Containers.dropContents(world, pos, be);
         world.updateNeighbourForOutputSignal(pos, this);
      }

      super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos, Direction direction) {
      return world.getBlockEntity(pos) instanceof RibbitChestBlockEntity be ? AbstractContainerMenu.getRedstoneSignalFromBlockEntity(be) : 0;
   }
}
