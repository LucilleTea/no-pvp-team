package net.lucilletea.noPvpTeam.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(BucketItem.class)
public class MixinBucketItem {
    @Shadow @Final private Fluid fluid;

    // Prevent bucket placement if it's in or near another player, and at least one of you has pvp off.
    @Inject(method = "placeFluid(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/hit/BlockHitResult;)Z",
    at = @At(value = "HEAD"), cancellable = true)
    private void mixinPlaceFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult blockHitResult, CallbackInfoReturnable<Boolean> cir) {
        if(player == null| !(this.fluid.isIn(FluidTags.LAVA))) {
            return; // Dispensers exempt, non-lava exempt.
        }
        Predicate<PlayerEntity> alwaysTrue = i -> true;
        List<PlayerEntity> list = world.getEntitiesByType(EntityType.PLAYER, new Box(pos).expand(0.5, 0, 0.5), alwaysTrue);
        if (list.isEmpty()) {
            return;
        } else {
            for (PlayerEntity player2 : list) {
                if (!player.shouldDamagePlayer(player2)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
