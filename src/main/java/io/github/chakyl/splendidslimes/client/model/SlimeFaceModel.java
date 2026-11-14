package io.github.chakyl.splendidslimes.client.model;


import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;


public class SlimeFaceModel<T extends Entity> extends HierarchicalModel<T> {
    private final ModelPart root;

    public SlimeFaceModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createInnerBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.05F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 8, 8);
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
    }

    public ModelPart root() {
        return this.root;
    }
}


