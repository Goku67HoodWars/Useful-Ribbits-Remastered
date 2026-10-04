package me.rogue_one.useful_ribbits.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import me.rogue_one.useful_ribbits.config.UsefulRibbitsConfig;
import me.rogue_one.useful_ribbits.init.UsefulRibbitsModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class MinerRibbitEntity extends Animal implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(MinerRibbitEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(MinerRibbitEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(MinerRibbitEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public String animationprocedure = "empty";
   private String prevAnim = "empty";

   public MinerRibbitEntity(EntityType<MinerRibbitEntity> type, Level world) {
      super(type, world);
      this.setPersistenceRequired();
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "miner_ribbit");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return this.entityData.get(TEXTURE);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new OpenDoorGoal(this, true));
      this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(3, new FloatGoal(this));
   }

   @Override
   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return UsefulRibbitsConfig.get().ambientCroaks ? UsefulRibbitsModSounds.RIBBIT_AMBIANT : null;
   }

   @Override
   public int getAmbientSoundInterval() {
      return Math.max(20, UsefulRibbitsConfig.get().ambientCroakInterval);
   }

   @Override
   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(UsefulRibbitsModSounds.RIBBIT_STEP, 0.15F, 1.0F);
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource ds) {
      return UsefulRibbitsModSounds.RIBBIT_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return UsefulRibbitsModSounds.RIBBIT_DEATH;
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
      return null;
   }

   @Override
   public boolean isFood(ItemStack stack) {
      return false;
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putString("Texture", this.getTexture());
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setTexture(input.getStringOr("Texture", "miner_ribbit"));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.15)
         .add(Attributes.MAX_HEALTH, 15.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 0.0)
         .add(Attributes.FOLLOW_RANGE, 16.0);
   }

   private PlayState movementPredicate(AnimationTest<MinerRibbitEntity> event) {
      if (this.animationprocedure.equals("empty")) {
         return event.isMoving() ? event.setAndContinue(RawAnimation.begin().thenLoop("walk")) : event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
      }
      return PlayState.STOP;
   }

   private PlayState procedurePredicate(AnimationTest<MinerRibbitEntity> event) {
      AnimationController<MinerRibbitEntity> controller = event.controller();
      if (!this.animationprocedure.equals("empty") && controller.hasAnimationFinished()
         || !this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty")) {
         if (!this.animationprocedure.equals(this.prevAnim)) {
            controller.reset();
         }

         controller.setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
         if (controller.hasAnimationFinished()) {
            this.animationprocedure = "empty";
            controller.reset();
         }
      } else if (this.animationprocedure.equals("empty")) {
         this.prevAnim = "empty";
         return PlayState.STOP;
      }

      this.prevAnim = this.animationprocedure;
      return PlayState.CONTINUE;
   }

   public String getSyncedAnimation() {
      return this.entityData.get(ANIMATION);
   }

   public void setAnimation(String animation) {
      this.entityData.set(ANIMATION, animation);
   }

   @Override
   public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("movement", 4, this::movementPredicate));
      controllers.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
