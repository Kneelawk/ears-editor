package com.kneelawk.earseditor.mixin.impl;

import com.kneelawk.earseditor.mixin.api.PlayerEntityRendererEvents;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile,
                                           @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
    private void onGetSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        Identifier override = PlayerEntityRendererEvents.getSkinTexture((AbstractClientPlayerEntity) (Object) this);
        if (override != null) {
            cir.setReturnValue(override);
        }
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void onGetSkinModel(CallbackInfoReturnable<String> cir) {
        String override = PlayerEntityRendererEvents.getSkinModel((AbstractClientPlayerEntity) (Object) this);
        if (override != null) {
            cir.setReturnValue(override);
        }
    }
}
