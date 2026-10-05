package com.pancake.tfc.triphammerpp.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pancake.tfc.triphammerpp.pickaxerendering.*;
import com.pancake.tfc.triphammerpp.TripHammerPP;
import net.dries007.tfc.client.render.blockentity.TripHammerBlockEntityRenderer;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.common.blocks.TripHammerBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(TripHammerBlockEntityRenderer.class)
public class TripHammerBlockEntityRendererMixin
{

    // The interface point touching both the shaft and the eye, at the center of the shaft
    // X: lateral, should be symmetrical
    // +Y: up
    // -Y: down
    // +Z: towards the trip hammer
    // -Z: towards the target
    private static final float PICKAXE_SHAFT_EYE_INTERFACE_X = 8f;
    private static final float PICKAXE_SHAFT_EYE_INTERFACE_Y = 14f;
    private static final float PICKAXE_SHAFT_EYE_INTERFACE_Z = -10f;

    private static final PickaxeDimensions PICKAXE_HEAD =
            new PickaxeDimensions(
                    4,
                    new PickaxeShaftDimensions(
                            3,  // width
                            20,  // length
                            1 // lengthAfterTheEye
                    ),
                    new PickaxeEyeDimensions(
                            1, // thicknessAroundTheShaft
                            2 // length
                    ),
                    new PickaxePointyEndDimensions(
                            //
                            7, // length
                            2, // backwardSlopePixels
                            1  // tipWidth (a square)
                    ),
                    new PickaxeAdzeDimensions(
                            7, // length,
                            1, // backwardSlopePixels,
                            6, // adzeBladeWidth (x width)
                            1 // adzeBladeThickness (z thickness)
                    )
            );

    private static final Map<Item, ResourceLocation> PICKAXE_TEXTURES =
            RenderHelpers.mapOf(map -> TFCItems.METAL_ITEMS.entrySet()
                    .stream()
                    .filter(e -> e.getValue().containsKey(Metal.ItemType.PICKAXE))
                    .forEach(e -> map.accept(
                            e.getValue().get(Metal.ItemType.PICKAXE),
                            Helpers.identifier(
                                    "block/metal/smooth/"
                                            + e.getKey().getSerializedName()
                            )
                    )
                    ));

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

        tfctriphammerpp$renderPickaxe(
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

    private static void tfctriphammerpp$renderPickaxe(
            TripHammerBlockEntity hammer,
            float partialTick,
            PoseStack stack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay,
            ItemStack item
    ) {

        final BlockState state = hammer.getBlockState();
        final Level level = hammer.getLevel();
        final Rotation rotation = hammer.getRotation();

        if (level == null || !(state.getBlock() instanceof TripHammerBlock))
            return;

        final ResourceLocation pickaxeTexture = PICKAXE_TEXTURES.get(item.getItem());
        if (pickaxeTexture == null)
        {
            return;
        }

        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());
        final float px = 1f / 16f;

        stack.pushPose();

        stack.translate(0.5f, 0.5f, 0.5f);
        stack.mulPose(Axis.YP.rotationDegrees(180f - 90f * state.getValue(TripHammerBlock.FACING).get2DDataValue()));
        stack.translate(-0.5f, -0.5f, -0.5f);

        final float angle = rotation == null ? 0 : hammer.getRealRotationDegrees(rotation, partialTick);
        final boolean isNegative = rotation != null && rotation.speed() != rotation.positiveSpeed();
        float pivotAngle = TripHammerBlockEntityRendererAccessor.tfctriphammerpp$invokeGetPivotAngle(
                isNegative, angle, 50, 180, 3);

        final float pivotX = 8f * px;
        final float pivotY = 14f * px;
        final float pivotZ = 3f * px;

        if (pivotAngle != 0f && rotation != null && rotation.positiveDirection() == state.getValue(TripHammerBlock.FACING).getClockWise())
        {
            stack.translate(pivotX, pivotY, pivotZ);
            stack.mulPose(Axis.XN.rotationDegrees(-pivotAngle));
            stack.translate(-pivotX, -pivotY, -pivotZ);
        }


        renderPickaxe(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                pickaxeTexture,
                PICKAXE_HEAD
        );



        stack.popPose();
    }


    private static void renderPickaxe(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeDimensions dimensions
    ) {
        renderPickaxeShaft(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                dimensions
        );

        renderPickaxeEye(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                texture,
                dimensions
        );

        renderPickaxePointyEnd(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                texture,
                dimensions
        );

        renderPickaxeAdze(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                texture,
                dimensions
        );

        // TODO Adze will go here later.

        renderPickaxeShaftNose(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                dimensions
        );

    }


    private static void renderPickaxeShaft(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            PickaxeDimensions dimensions
    ) {
        final float px = 1f / 16f;

        // at the pickaxe head
        float minX = PICKAXE_SHAFT_EYE_INTERFACE_X - ((dimensions.shaft().width()+0.0f)/2);
        float minY = PICKAXE_SHAFT_EYE_INTERFACE_Y - ((dimensions.shaft().width()+0.0f)/2);
        float minZ = PICKAXE_SHAFT_EYE_INTERFACE_Z ;
        // at the trip hammer
        float maxX = PICKAXE_SHAFT_EYE_INTERFACE_X + ((dimensions.shaft().width()+0.0f)/2);
        float maxY = PICKAXE_SHAFT_EYE_INTERFACE_Y + ((dimensions.shaft().width()+0.0f)/2);
        float maxZ = PICKAXE_SHAFT_EYE_INTERFACE_Z + dimensions.shaft().length();

        RenderHelpers.renderTexturedCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(
                        TripHammerBlockEntityRenderer.ROD_TEXTURE
                ),
                packedLight,
                packedOverlay,

                minX * px,
                minY * px,
                minZ * px,

                maxX * px,
                maxY * px,
                maxZ * px,

                false
        );
    }

    // The bit of wood that sticks out after the pickaxe eye
    private static void renderPickaxeShaftNose(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            PickaxeDimensions dimensions
    ) {
        final float px = 1f / 16f;

        // at the end
        float minX = PICKAXE_SHAFT_EYE_INTERFACE_X - ((dimensions.shaft().width()+0.0f)/2);
        float minY = PICKAXE_SHAFT_EYE_INTERFACE_Y - ((dimensions.shaft().width()+0.0f)/2);
        float minZ = PICKAXE_SHAFT_EYE_INTERFACE_Z - dimensions.eye().length() - dimensions.shaft().lengthAfterTheEye();
        // closer to the trip hammer
        float maxX = PICKAXE_SHAFT_EYE_INTERFACE_X + ((dimensions.shaft().width()+0.0f)/2);
        float maxY = PICKAXE_SHAFT_EYE_INTERFACE_Y + ((dimensions.shaft().width()+0.0f)/2);
        float maxZ = PICKAXE_SHAFT_EYE_INTERFACE_Z - dimensions.eye().length();

        RenderHelpers.renderTexturedCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(
                        TripHammerBlockEntityRenderer.ROD_TEXTURE
                ),
                packedLight,
                packedOverlay,

                minX * px,
                minY * px,
                minZ * px,

                maxX * px,
                maxY * px,
                maxZ * px,

                false
        );
    }

    private static void renderPickaxeEye(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeDimensions dimensions
    ) {
        final float px = 1f / 16f;

        final PickaxeShaftDimensions shaft =
                dimensions.shaft();

        final PickaxeEyeDimensions eye =
                dimensions.eye();

        final float eyeWidth =
                shaft.width()
                        + 2f * eye.thicknessAroundTheShaft();

        // at the shaft-head interface
        float maxX = PICKAXE_SHAFT_EYE_INTERFACE_X + (eyeWidth/2);
        float maxY = PICKAXE_SHAFT_EYE_INTERFACE_Y + (eyeWidth/2);
        float maxZ = PICKAXE_SHAFT_EYE_INTERFACE_Z ;

        // at the end of the pickaxe
        float minX = PICKAXE_SHAFT_EYE_INTERFACE_X - (eyeWidth/2);
        float minY = PICKAXE_SHAFT_EYE_INTERFACE_Y - (eyeWidth/2);
        float minZ = PICKAXE_SHAFT_EYE_INTERFACE_Z - eye.length();

        RenderHelpers.renderTexturedCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(texture),
                packedLight,
                packedOverlay,

                minX * px,
                minY * px,
                minZ * px,

                maxX * px,
                maxY * px,
                maxZ * px,

                false
        );
    }

    private static void renderPickaxePointyEnd(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeDimensions dimensions
    ) {
        final float px = 1f / 16f;

        final PickaxePointyEndDimensions point =
                dimensions.pointyEnd();

        final float eyeHalfThickness = ( dimensions.shaft().width() + 2 * dimensions.eye().thicknessAroundTheShaft() ) / 2f;

        final float eyeBottomY = PICKAXE_SHAFT_EYE_INTERFACE_Y - eyeHalfThickness;

        stack.pushPose();

        stack.translate(
                PICKAXE_SHAFT_EYE_INTERFACE_X * px,
                eyeBottomY * px,
                PICKAXE_SHAFT_EYE_INTERFACE_Z * px
        );

        RenderHelpers.renderTexturedTrapezoidalCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(texture),
                packedLight,
                packedOverlay,

                // P = narrow tip
                -point.tipWidth()/2f * px,
                point.tipWidth()/2f * px,
                ( point.backwardSlopePixels() - point.tipWidth() )* px,
                ( point.backwardSlopePixels() )*px,

                // Q = wide base
                -eyeHalfThickness * px,
                (eyeHalfThickness) * px,
                (0f - dimensions.eye().length()) * px,
                (0f ) * px,

                // Base -> tip
                -point.length() * px,
                0f,

                2 * eyeHalfThickness,
                point.length(),
                dimensions.eye().length(),

                false
        );

        stack.popPose();
    }


    private static void renderPickaxeAdze(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeDimensions dimensions
    ) {
        final float px = 1f / 16f;

        final PickaxeAdzeDimensions adze =
                dimensions.adze();

        final float eyeHalfThickness = ( dimensions.shaft().width() + 2 * dimensions.eye().thicknessAroundTheShaft() ) / 2f;

        final float eyeTopY = PICKAXE_SHAFT_EYE_INTERFACE_Y + eyeHalfThickness;

        stack.pushPose();

        stack.translate(
                PICKAXE_SHAFT_EYE_INTERFACE_X * px,
                eyeTopY * px,
                PICKAXE_SHAFT_EYE_INTERFACE_Z * px
        );

        RenderHelpers.renderTexturedTrapezoidalCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(texture),
                packedLight,
                packedOverlay,

                // P = wide base
                -eyeHalfThickness * px,
                (eyeHalfThickness) * px,
                (0f - dimensions.eye().length()) * px,
                (0f ) * px,

                // Q = narrow blade
                -adze.bladeWidth()/2f * px,
                adze.bladeWidth()/2f * px,
                ( adze.backwardSlopePixels() - adze.bladeThickness() )* px,
                ( adze.backwardSlopePixels() )*px,

                // Base -> tip
                0f,
                adze.length() * px,

                2 * eyeHalfThickness,
                adze.length(),
                dimensions.eye().length(),

                false
        );

        stack.popPose();

    }

}