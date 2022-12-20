package com.rabbitminers.extendedgears.mixin;

import com.rabbitminers.extendedgears.basecog.ICustomCogWheel;
import com.rabbitminers.extendedgears.config.ECConfig;
import com.rabbitminers.extendedgears.network.BreakCogwheelPacket;
import com.rabbitminers.extendedgears.network.Packets;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(KineticTileEntity.class)
public abstract class MixinKineticTileEntity {
    @Shadow @Nullable public BlockPos source;

    @Shadow public abstract boolean hasSource();

    @Shadow protected float speed;
    @Shadow protected float capacity;
    public BlockPos pos;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void KineticTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.pos = pos;
    }
    @Inject(at = @At("TAIL"), method = "tick", remap = false)
    private void tick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getBlockState(pos).getBlock() instanceof CogWheelBlock cogWheel) {
            if (cogWheel instanceof ICustomCogWheel)
                return;
            if (Math.abs(speed) > ECConfig.COGWHEEL_LIMITATIONS.maxSpruceRPM.get()
                    || capacity > ECConfig.COGWHEEL_LIMITATIONS.maxSpruceSU.get())
                Packets.breakCogwheelServerSide(new BreakCogwheelPacket(pos));
        }
    }
}