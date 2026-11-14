package io.github.chakyl.splendidslimes.item.ItemProjectile;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ItemProjectileType {

    private List<Holder<Item>> items = new ArrayList<>();

    private float drag = 0.99f;
    private float velocityMultiplier = 1;
    private float gravityMultiplier = 1;

    private Predicate<EntityHitResult> preEntityHit = e -> false; // True if hit should be canceled
    private Predicate<EntityHitResult> onEntityHit = e -> false; // True if shouldn't recover projectile
    private BiPredicate<LevelAccessor, BlockHitResult> onBlockHit = (w, ray) -> false;

    protected ItemProjectileType() {
    }

    public List<Holder<Item>> getItems() {
        return items;
    }

    public float getDrag() {
        return drag;
    }

    public float getVelocityMultiplier() {
        return velocityMultiplier;
    }

    public float getGravityMultiplier() {
        return gravityMultiplier;
    }

    public boolean preEntityHit(EntityHitResult ray) {
        return preEntityHit.test(ray);
    }

    public boolean onEntityHit(EntityHitResult ray) {
        return onEntityHit.test(ray);
    }

    public boolean onBlockHit(LevelAccessor world, BlockHitResult ray) {
        return onBlockHit.test(world, ray);
    }

    public static class Builder {

        protected ResourceLocation id;
        protected ItemProjectileType result;

        public Builder(ResourceLocation id) {
            this.id = id;
            this.result = new ItemProjectileType();
        }
        public Builder drag(float drag) {
            result.drag = drag;
            return this;
        }

        public Builder velocity(float velocity) {
            result.velocityMultiplier = velocity;
            return this;
        }

        public Builder gravity(float modifier) {
            result.gravityMultiplier = modifier;
            return this;
        }


        public Builder preEntityHit(Predicate<EntityHitResult> callback) {
            result.preEntityHit = callback;
            return this;
        }

        public Builder onEntityHit(Predicate<EntityHitResult> callback) {
            result.onEntityHit = callback;
            return this;
        }

        public Builder onBlockHit(BiPredicate<LevelAccessor, BlockHitResult> callback) {
            result.onBlockHit = callback;
            return this;
        }

        public Builder addItems(ItemLike... items) {
            for (ItemLike provider : items) {
                result.items.add(BuiltInRegistries.ITEM.wrapAsHolder(provider.asItem()));
            }
            return this;
        }

        public ItemProjectileType register() {
            return result;
        }

        public ItemProjectileType registerAndAssign(ItemLike... items) {
            addItems(items);
            register();
            return result;
        }

    }

}