package com.pancake.tfc.triphammerpp.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pancake.tfc.triphammerpp.logic.client.TripHammerPPClient;
import com.pancake.tfc.triphammerpp.logic.server.TripHammerPP;
import net.dries007.tfc.client.render.blockentity.TripHammerBlockEntityRenderer;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TripHammerBlockEntityRenderer.class)
public class TripHammerBlockEntityRendererMixin
{

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tfctriphammerpp$renderPickaxe(
            TripHammerBlockEntity hammer,
            float partialTick,
            PoseStack stack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay,
            CallbackInfo ci
    ) {
        ItemStack item = hammer.getInventory().getStackInSlot(0);

        if (!TripHammerPP.isItemAPickaxe(item)) {
            return;
        }

        TripHammerPPClient.tfctriphammerpp$renderPickaxe(
                hammer,
                partialTick,
                stack,
                buffers,
                packedLight,
                packedOverlay,
                item
        );

        ci.cancel();
    }


}