package net.lucilletea.noPvpTeam.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity {
    public MixinPlayerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * @author LucilleTea
     * @reason The original method is devoted to the friendlyFire functionality.
     * Here we replace it with functionality to prevent PvP if either player has
     * the option enabled on their team.
     */
    @Overwrite
    public boolean shouldDamagePlayer(PlayerEntity player) {
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        AbstractTeam abstractTeam2 = player.getScoreboardTeam();
        if (player.equals(this)) {
            return true; // a player should always be able to damage themself, even if pvp is disabled.
        }
        if (abstractTeam == null) {
            if (abstractTeam2 == null) {
                return true;
            } else {
                return abstractTeam2.isFriendlyFireAllowed();
            }
        } else {
            if (abstractTeam2 == null) {
                return abstractTeam.isFriendlyFireAllowed();
            } else {
                return abstractTeam.isFriendlyFireAllowed() && abstractTeam2.isFriendlyFireAllowed();
            }
        }
    }
}
