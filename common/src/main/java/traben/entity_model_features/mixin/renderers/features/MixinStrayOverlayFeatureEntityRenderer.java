package traben.entity_model_features.mixin.renderers.features;


import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(StrayOverlayFeatureRenderer.class)
public abstract class MixinStrayOverlayFeatureEntityRenderer<T extends MobEntity & RangedAttackMob, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

//
//    @Shadow @Final private SkeletonEntityModel<T> model;
//
    public MixinStrayOverlayFeatureEntityRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }
//
//    @ModifyArgs(
//            method = "Lnet/minecraft/client/render/entity/feature/StrayOverlayFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/MobEntity;FFFFFF)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/StrayOverlayFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V")
//    )
//    private void emf$mixinDrownedLayer(Args args) {
//        //4 is sub arm
//        //5 is sleeve
//        if(emf$originalModel == null) {
//            emf$originalModel = this.model;
//            if (this.getContextModel() instanceof EMFCustomEntityModel) {
//                String entityTypeName = "stray_outer";
//                EMFCustomEntityModel<T> emfModel =  EMFData.getInstance().getModelVariant(null,entityTypeName, this.model);
//                if(emfModel != null){
//                    emf$emfModel = emfModel;
//
//                }
//            }
//        }
//        if(emf$emfModel != null){// && args.get(0) instanceof EMFCustomModel){
//            //return actual vanilla arm
//            args.set(1, emf$emfModel);
//        }
//
//    }
//
//    private  SkeletonEntityModel<T> emf$originalModel;
//    private EMFCustomEntityModel<T> emf$emfModel;


}