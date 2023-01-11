package traben.entity_model_features.mixin.renderers;


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SpiderEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.models.EMF_CustomModel;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.vanilla_part_mapping.VanillaMappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
    @Shadow
    public abstract M getModel();
    @Shadow
    protected M model;

    @Shadow @Final protected List<FeatureRenderer<T, M>> features;

//    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",shift = At.Shift.AFTER)
//            , locals = LocalCapture.CAPTURE_FAILSOFT)
//    private void injected(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci, float h, float j, float k, float m, float l, float n, float o) {
//        //System.out.println("rendered");
//        //here can redirect model rendering
//        if (true/*livingEntity instanceof SheepEntity || livingEntity instanceof VillagerEntity*/){
//            EMFData emfData = EMFData.getInstance();
//            int typeHash =this.hashCode(); // livingEntity.getType().hashCode();
//
//            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(getModel().getLayer(getTexture(livingEntity))/*RenderLayer.getEntityCutoutNoCull(getTexture(livingEntity))*/);
//
//            if (!emfData.JEMPATH_CustomModel.containsKey(typeHash)){
//                String entityTypeName =livingEntity.getType().getName().getString().toLowerCase().replace("\s","_");
//
//                emfData.createEMFModel(entityTypeName,typeHash,getModel());
//                //temp while testing so only runs once
//                //JEMPATH_CustomModel.put(entityTypeName,null);
//            }
//            //System.out.println("rendered3");
//            if (emfData.JEMPATH_CustomModel.containsKey(typeHash)){
//                if (emfData.JEMPATH_CustomModel.get(typeHash) != null){
//
//                    //render model,
//                    //System.out.println("rendering");
//                    //JEMPATH_CustomModel.get(modelID).animate();
//
//
//                    //if (vanillaPartsByType.containsKey(typeHash)) {
//
//                        //VanillaMappings.VanillaMapper vanillaPartSupplier = VanillaMappings.getVanillaModelPartsMapSupplier(typeHash,getModel());
//                    emf$CustomModel =emfData.JEMPATH_CustomModel.get(typeHash);
//                        //EMFAnimationProcessor.animateThisModel(vanillaParts,model,livingEntity);
//                    emf$CustomModel.setAngles(livingEntity, o, n, l, k, m);//,vanillaPartSupplier, (EntityModel<LivingEntity>) getModel());
//                        //System.out.println("rendered");
//                        //model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
//                    //}
//                }
//            }
//        }
//    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void emf$InjectModel(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if(!emf$checked) {
            emf$checked = true;
            EMFData emfData = EMFData.getInstance();
            int typeHash = this.hashCode(); // livingEntity.getType().hashCode();

            if (!emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
                String entityTypeName = livingEntity.getType().getName().getString().toLowerCase().replace("\s", "_");
                emfData.createEMFModel(entityTypeName, typeHash, getModel());
            }
            if (emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
                if (emfData.JEMPATH_CustomModel.get(typeHash) != null) {

                    //might cause compat issues
                    EMF_CustomModel<LivingEntity> emf = emfData.JEMPATH_CustomModel.get(typeHash);
                    emf$originalModel = this.model;
                    emf$newModel = (M)emf;

                }
            }
        }
        if(emf$newModel != null){
            this.model =  emf$newModel;
        }
    }
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void emf$ReturnModel(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if(emf$originalModel != null){
            this.model =  emf$originalModel;
        }
    }

    private M emf$originalModel = null;
    private M emf$newModel = null;
    private boolean emf$checked = false;

//    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
//    private void injected(CallbackInfoReturnable<M> cir) {
//        //ensure player position modifiers still work
//        if(originalModel != null && originalModel instanceof PlayerEntityModel<?>){
//            cir.setReturnValue(originalModel);
//        }
//    }

//    EMF_CustomModel<LivingEntity> emf$CustomModel= null;

//    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
//    private void EMF_VanillaRenderPrevent(EntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float k, float l, float m, float n) {
//
//
//            if(emf$CustomModel != null){
//                emf$CustomModel.render(matrixStack, vertexConsumer, i, j, k, l, m, n);
//            }else{
//                instance.render(matrixStack, vertexConsumer, i, j, k, l, m, n);
//            }


           // if(vanillaModel == null) {
               // vanillaModel = instance;
                //vanillaParts.clear();
                //System.out.println("is quadped = "+(instance instanceof QuadrupedEntityModel));
               {

                }
                //System.out.println(vanillaParts);
           // }
//            for (ModelPart part: vanillaParts.values()) {
//                part.visible = false;
//            }
           // if (vanillaParts.get("head") != null ) vanillaParts.get("head").visible = true;
           // instance.render(matrixStack, vertexConsumer, i, j, k, l, m, n);


//    }
}