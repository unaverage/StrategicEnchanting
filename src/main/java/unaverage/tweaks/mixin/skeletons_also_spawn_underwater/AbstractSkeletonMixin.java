package unaverage.tweaks.mixin.skeletons_also_spawn_underwater;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonMixin extends HostileEntity {
    protected AbstractSkeletonMixin(EntityType<? extends HostileEntity> entityType, World world) {super(entityType, world);}

    @Shadow public abstract void equipStack(EquipmentSlot slot, ItemStack stack);
    @Shadow @Final private BowAttackGoal<AbstractSkeletonEntity> bowAttackGoal;
    @Shadow @Final private MeleeAttackGoal meleeAttackGoal;

    @Unique
    ProjectileAttackGoal tridentGoal = new ProjectileAttackGoal((RangedAttackMob)this, 1.0, 40, 10.0F) {
        public boolean canStart() {
            return super.canStart() && AbstractSkeletonMixin.this.getMainHandStack().isOf(Items.TRIDENT);
        }

        public void start() {
            super.start();
            AbstractSkeletonMixin.this.setAttacking(true);
            AbstractSkeletonMixin.this.setCurrentHand(Hand.MAIN_HAND);
        }

        public void stop() {
            super.stop();
            AbstractSkeletonMixin.this.clearActiveItem();
            AbstractSkeletonMixin.this.setAttacking(false);
        }
    };

    @Inject(
        method = "initEquipment",
        at = @At("TAIL")
    )
    void spawnWithTridentIfUnderwater(Random random, LocalDifficulty localDifficulty, CallbackInfo ci){
        if (!this.getWorld().getFluidState(this.getBlockPos()).isIn(FluidTags.WATER)) return;

        this.equipStack(
            EquipmentSlot.MAINHAND, Items.TRIDENT.getDefaultStack()
        );
    }

    @Inject(
        method = "attack",
        at = @At("HEAD"),
        cancellable = true
    )
    void attackWithTrident(LivingEntity target, float pullProgress, CallbackInfo ci){
        if (!this.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.TRIDENT)) return;

        //copy and paste of drowned.attack()
        TridentEntity tridentEntity = new TridentEntity(this.getWorld(), this, new ItemStack(Items.TRIDENT));
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - tridentEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        tridentEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(tridentEntity);

        ci.cancel();
    }

    @Inject(
        method = "updateAttackType",
        at = @At("TAIL")
    )
    void switchToTridentGoal(CallbackInfo ci){
        if (this.getWorld() != null && !this.getWorld().isClient) {
            if (!this.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.TRIDENT)) return;

            this.goalSelector.remove(this.bowAttackGoal);
            this.goalSelector.remove(this.meleeAttackGoal);
            this.goalSelector.add(4, tridentGoal);
        }
    }
}
