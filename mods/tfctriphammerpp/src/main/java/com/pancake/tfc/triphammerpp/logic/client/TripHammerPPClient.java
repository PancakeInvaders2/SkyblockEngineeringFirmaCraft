package com.pancake.tfc.triphammerpp.logic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pancake.tfc.triphammerpp.rendering.*;
import com.z1z1y2.tfcfasttriphammer.rotation.CrossBladedAxleBlockEntity;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.render.blockentity.TripHammerBlockEntityRenderer;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.common.blocks.TripHammerBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

    public class TripHammerPPClient {

        public static final float PX = 1f / 16f;

        // The interface point touching both the shaft and the eye, at the center of the shaft
        // X: lateral, should be symmetrical
        // +Y: up
        // -Y: down
        // +Z: towards the trip hammer
        // -Z: towards the target
        public static final float PICKAXE_SHAFT_EYE_INTERFACE_X = 8f;
        public static final float PICKAXE_SHAFT_EYE_INTERFACE_Y = 14f;
        public static final float PICKAXE_SHAFT_EYE_INTERFACE_Z = -12f;

        public static final PickaxeHolderDimensions PICKAXE_HOLDER = new PickaxeHolderDimensions(
                3,
                30
        );

        public static final PickaxeDimensions PICKAXE_HEAD =
                new PickaxeDimensions(
                        4,
                        new PickaxeShaftDimensions(
                                2,  // width
                                29,  // length
                                1 // lengthAfterTheEye
                        ),
                        new PickaxeEyeDimensions(
                                1f, // thicknessAroundTheShaft
                                2 // length
                        ),
                        new PickaxePointyEndDimensions(
                                //
                                8, // length
                                2f, // backwardSlopePixels
                                1  // tipWidth (a square)
                        ),
                        new PickaxeAdzeDimensions(
                                7, // length,
                                2f, // backwardSlopePixels,
                                4, // adzeBladeWidth (x width)
                                0.5f // adzeBladeThickness (z thickness)
                        )
                );

        public static final Map<Item, ResourceLocation> PICKAXE_TEXTURES =
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

        public static void tfctriphammerpp$renderPickaxe(
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

            stack.pushPose();

            stack.translate(0.5f, 0.5f, 0.5f);
            stack.mulPose(Axis.YP.rotationDegrees(180f - 90f * state.getValue(TripHammerBlock.FACING).get2DDataValue()));
            stack.translate(-0.5f, -0.5f, -0.5f);

            // in [0-360]
            float angle = rotation == null ? 0 : hammer.getRealRotationDegrees(rotation, 1.0f);

//            TFCTripHammer.LOGGER.debug(
//                    "[TFCTripHammer++] render pos={} angle0={} angle1={} speed={} direction={}",
//                    hammer.getBlockPos(),
//                    rotation == null ? null : rotation.angle(0.0F),
//                    rotation == null ? null : rotation.angle(1.0F),
//                    rotation == null ? null : rotation.speed(),
//                    rotation == null ? null : rotation.direction()
//            );

            final boolean isNegative = rotation != null && rotation.speed() != rotation.positiveSpeed();

            float pivotAngle = getPivotAngle(
                    hammer, isNegative, angle, 50, 180, 3);

            final float pivotX = 8f * PX;
            final float pivotY = 14f * PX;
            final float pivotZ = 3f * PX;

            if (pivotAngle != 0f && rotation != null && rotation.positiveDirection() == state.getValue(TripHammerBlock.FACING).getClockWise())
            {
                stack.translate(pivotX, pivotY, pivotZ);
                stack.mulPose(Axis.XN.rotationDegrees(-pivotAngle));
                stack.translate(-pivotX, -pivotY, -pivotZ);
            }




            renderPickaxeAndHolder(
                    stack,
                    buffer,
                    packedLight,
                    packedOverlay,
                    pickaxeTexture,
                    PICKAXE_HOLDER,
                    PICKAXE_HEAD
            );



            stack.popPose();
        }

        private static boolean isCrossBladedAxle(TripHammerBlockEntity hammer) {
            Level level = hammer.getLevel();
            BlockPos pos = hammer.getBlockPos();

            if(level == null || pos == null){
                return false;
            }

            BlockEntity above = level.getBlockEntity(pos.above());
            return above instanceof CrossBladedAxleBlockEntity;
        }


    public static float originalGetPivotAngle(boolean isNegative, float angle, float startAngleOffset, float apexAngle, float endAngleOffset)
    {
        if (isNegative)
        {
            // Angle goes high to low
            final float pivotStart = apexAngle + startAngleOffset;
            final float pivotEnd = apexAngle - endAngleOffset;
            if (angle > apexAngle && angle < pivotStart)
            {
                return 45 - Mth.map(angle, apexAngle, pivotStart, 0f, 45f);
            }
            else if (angle > pivotEnd && angle < apexAngle)
            {
                return Mth.map(angle, pivotEnd, apexAngle, 0f, 45f);
            }
        }
        else
        {
            // Angle goes low to high
            final float pivotStart = apexAngle - startAngleOffset;
            final float pivotEndPositive = apexAngle + endAngleOffset;
            if (angle > pivotStart && angle < apexAngle)
            {
                return Mth.map(angle, pivotStart, apexAngle, 0f, 45f);
            }
            else if (angle > apexAngle && angle < pivotEndPositive)
            {
                return 45f - Mth.map(angle, apexAngle, pivotEndPositive, 0f, 45f);
            }
        }
        return 0f;
    }

    public static float getPivotAngle(
            TripHammerBlockEntity hammer,
            boolean isNegative,
            float angle,
            float windowSize,
            float target,
            float narrowOffset) {

        float[] targets = isCrossBladedAxle(hammer)
                ? new float[]{0f, 90f, 180f, 270f}
                : new float[]{180f};
        for(float t : targets) {
            float result = computePivotAngle(isNegative, angle, windowSize, t, narrowOffset);
            if (result != 0.0F) {
                return result;
            }
        }

        return 0.0F;
    }
    private static float computePivotAngle(
            boolean isNegative,
            float angle,
            float windowSize,
            float target,
            float narrowOffset
    ) {
        // Signed angular difference from the target, in [-180, 180).
        float relativeAngle = Mth.wrapDegrees(angle - target);

        if (isNegative) {
            if (relativeAngle > 0 && relativeAngle < windowSize) {
                return 45.0F - Mth.map(
                        relativeAngle, 0.0F, windowSize, 0.0F, 45.0F
                );
            }

            if (relativeAngle < 0 && relativeAngle > -narrowOffset) {
                return Mth.map(
                        relativeAngle, -narrowOffset, 0.0F, 0.0F, 45.0F
                );
            }
        } else {
            if (relativeAngle < 0 && relativeAngle > -windowSize) {
                return Mth.map(
                        relativeAngle, -windowSize, 0.0F, 0.0F, 45.0F
                );
            }

            if (relativeAngle > 0 && relativeAngle < narrowOffset) {
                return 45.0F - Mth.map(
                        relativeAngle, 0.0F, narrowOffset, 0.0F, 45.0F
                );
            }
        }

        return 0.0F;
    }


    public static void renderPickaxeAndHolder(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeHolderDimensions pickaxeHolder,
            PickaxeDimensions dimensions
    ) {
        // the pickaxe hits the side of the block that is 2 blocks away horizontally, and one block up vertically

        // the horizontal section of the shaft, on which the bladed axle pushes down to lift the pickaxe
        renderPickaxeHolder(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                pickaxeHolder
        );


        stack.pushPose();
        stack.translate(0f * PX, 22f * PX, -34f * PX);
        stack.mulPose(Axis.XN.rotationDegrees(-60)); // rotate the whole pickaxe
        stack.translate(-0f * PX, -0f * PX, 7.5f * PX);

        // TODO the 2 sections of handle are not connecting right now,
        //  need to adjust the lengths and take the angle into account
        //  Maybe even create a pickaxe holder so that the shaft isn't so absurdly long and bent

        // the handle going through the eye
        // and through the horizontal pickaxe holder
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

        renderPickaxeShaftNose(
                stack,
                buffer,
                packedLight,
                packedOverlay,
                dimensions
        );

        stack.popPose();

    }

    // the horizontal shaft on which the bladed axle pushes down
    public static void renderPickaxeHolder(PoseStack stack,
                                            VertexConsumer buffer,
                                            int packedLight,
                                            int packedOverlay,
                                            PickaxeHolderDimensions holder) {

        int remainingLength = holder.length();

        // we can't go past 16 px, or the rod texture becomes a different wood for some reason ?
        int cuboidLength = remainingLength%16;
        float minX = PICKAXE_SHAFT_EYE_INTERFACE_X - ((holder.thickness())/2f);
        float maxX = PICKAXE_SHAFT_EYE_INTERFACE_X + ((holder.thickness())/2f);
        float minY = PICKAXE_SHAFT_EYE_INTERFACE_Y - ((holder.thickness())/2f);
        float maxY = PICKAXE_SHAFT_EYE_INTERFACE_Y + ((holder.thickness())/2f);

        float maxZ = 10f;
        float minZ = maxZ - cuboidLength;

        while(remainingLength > 0) {


            RenderHelpers.renderTexturedCuboid(stack, buffer, RenderHelpers.blockTexture(TripHammerBlockEntityRenderer.ROD_TEXTURE), packedLight, packedOverlay,
                    minX * PX,
                    minY * PX,
                    minZ * PX,
                    maxX * PX,
                    maxY * PX,
                    maxZ * PX,
                    true);
            maxZ = minZ;
            minZ -= cuboidLength;
            remainingLength -= 16;
            cuboidLength = remainingLength %16; // next iteration
        }

    }

    // the angled portion of the shaft, going from the horizontal portion to the pickaxe eye
    public static void renderPickaxeShaft(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            PickaxeDimensions dimensions
    ) {

        PickaxeShaftDimensions shaft = dimensions.shaft();

        int remainingLength = shaft.length();

        // we can't go past 16 px, or the rod texture becomes a different wood for some reason ?
        int cuboidLength = remainingLength%16;
        float minX = PICKAXE_SHAFT_EYE_INTERFACE_X - ((shaft.width())/2f);
        float maxX = PICKAXE_SHAFT_EYE_INTERFACE_X + ((shaft.width())/2f);
        float minY = PICKAXE_SHAFT_EYE_INTERFACE_Y - ((shaft.width())/2f);
        float maxY = PICKAXE_SHAFT_EYE_INTERFACE_Y + ((shaft.width())/2f);

        float minZ = PICKAXE_SHAFT_EYE_INTERFACE_Z ;
        float maxZ = minZ + cuboidLength ; // dimensions.shaft().length();


        while(remainingLength > 0) {


            RenderHelpers.renderTexturedCuboid(stack, buffer, RenderHelpers.blockTexture(TripHammerBlockEntityRenderer.ROD_TEXTURE), packedLight, packedOverlay,
                    minX * PX,
                    minY * PX,
                    minZ * PX,
                    maxX * PX,
                    maxY * PX,
                    maxZ * PX,
                    true);

            minZ = maxZ;
            maxZ += cuboidLength;
            remainingLength -= 16;
            cuboidLength = remainingLength %16; // next iteration
        }


    }

    // The bit of wood that sticks out after the pickaxe eye
    public static void renderPickaxeShaftNose(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            PickaxeDimensions dimensions
    ) {

        // at the end
        float minX = PICKAXE_SHAFT_EYE_INTERFACE_X - ((dimensions.shaft().width())/2f);
        float minY = PICKAXE_SHAFT_EYE_INTERFACE_Y - ((dimensions.shaft().width())/2f);
        float minZ = PICKAXE_SHAFT_EYE_INTERFACE_Z - dimensions.eye().length() - dimensions.shaft().lengthAfterTheEye();
        // closer to the trip hammer
        float maxX = PICKAXE_SHAFT_EYE_INTERFACE_X + ((dimensions.shaft().width())/2f);
        float maxY = PICKAXE_SHAFT_EYE_INTERFACE_Y + ((dimensions.shaft().width())/2f);
        float maxZ = PICKAXE_SHAFT_EYE_INTERFACE_Z - dimensions.eye().length();

        RenderHelpers.renderTexturedCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(
                        TripHammerBlockEntityRenderer.ROD_TEXTURE
                ),
                packedLight,
                packedOverlay,

                minX * PX,
                minY * PX,
                minZ * PX,

                maxX * PX,
                maxY * PX,
                maxZ * PX,

                true
        );
    }

    public static void renderPickaxeEye(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeDimensions dimensions
    ) {

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

                minX * PX,
                minY * PX,
                minZ * PX,

                maxX * PX,
                maxY * PX,
                maxZ * PX,

                true
        );
    }

    public static void renderPickaxePointyEnd(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeDimensions dimensions
    ) {

        final PickaxePointyEndDimensions point =
                dimensions.pointyEnd();

        final float eyeHalfThickness = ( dimensions.shaft().width() + 2 * dimensions.eye().thicknessAroundTheShaft() ) / 2f;

        final float eyeBottomY = PICKAXE_SHAFT_EYE_INTERFACE_Y - eyeHalfThickness;

        stack.pushPose();

        stack.translate(
                PICKAXE_SHAFT_EYE_INTERFACE_X * PX,
                eyeBottomY * PX,
                PICKAXE_SHAFT_EYE_INTERFACE_Z * PX
        );

        RenderHelpers.renderTexturedTrapezoidalCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(texture),
                packedLight,
                packedOverlay,

                // P = narrow tip
                -point.tipWidth()/2f * PX,
                point.tipWidth()/2f * PX,
                ( point.backwardSlopePixels() - point.tipWidth() )* PX,
                ( point.backwardSlopePixels() )*PX,

                // Q = wide base
                -eyeHalfThickness * PX,
                (eyeHalfThickness) * PX,
                (0f - dimensions.eye().length()) * PX,
                (0f ) * PX,

                // Base -> tip
                -point.length() * PX,
                0f,

                2 * eyeHalfThickness,
                point.length(),
                dimensions.eye().length(),

                false
        );

        stack.popPose();
    }


    public static void renderPickaxeAdze(
            PoseStack stack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            ResourceLocation texture,
            PickaxeDimensions dimensions
    ) {

        final PickaxeAdzeDimensions adze =
                dimensions.adze();

        final float eyeHalfThickness = ( dimensions.shaft().width() + 2 * dimensions.eye().thicknessAroundTheShaft() ) / 2f;

        final float eyeTopY = PICKAXE_SHAFT_EYE_INTERFACE_Y + eyeHalfThickness;

        stack.pushPose();

        stack.translate(
                PICKAXE_SHAFT_EYE_INTERFACE_X * PX,
                eyeTopY * PX,
                PICKAXE_SHAFT_EYE_INTERFACE_Z * PX
        );

        RenderHelpers.renderTexturedTrapezoidalCuboid(
                stack,
                buffer,
                RenderHelpers.blockTexture(texture),
                packedLight,
                packedOverlay,

                // P = wide base
                -eyeHalfThickness * PX,
                (eyeHalfThickness) * PX,
                (0f - dimensions.eye().length()) * PX,
                (0f ) * PX,

                // Q = narrow blade
                -adze.bladeWidth()/2f * PX,
                adze.bladeWidth()/2f * PX,
                ( adze.backwardSlopePixels() - adze.bladeThickness() )* PX,
                ( adze.backwardSlopePixels() )*PX,

                // Base -> tip
                0f,
                adze.length() * PX,

                2 * eyeHalfThickness,
                adze.length(),
                dimensions.eye().length(),

                false
        );

        stack.popPose();

    }
}
