package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemDragonHornActive extends Item {

    public ItemDragonHornActive(String name) {
        this.maxStackSize = 1;
        this.setCreativeTab(IceAndFire.TAB);
        this.setUnlocalizedName("iceandfire." + name);
        GameRegistry.registerItem(this, name);
        this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    ItemStack itemstack = entityIn.getActiveItemStack();
                    return itemstack != null && itemstack.getItem() instanceof ItemDragonHornActive ? (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
                }
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
        itemStack.setTagCompound(new NBTTagCompound());
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int f, boolean f1) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            boolean flag = entityplayer.capabilities.isCreativeMode;
            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            if (i < 20){
                return;
            }
            double d0 = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * 1.0D;
            double d1 = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * 1.0D + (double)entityplayer.getEyeHeight();
            double d2 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * 1.0D;
            float f1 = entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * 1.0F;
            float f2 = entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * 1.0F;
            Vec3d vec3d = new Vec3d(d0, d1, d2);
            float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
            float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            float f7 = f4 * f5;
            float f8 = f3 * f5;
            Vec3d vec3d1 = vec3d.addVector((double)f7 * 5.0D, (double)f6 * 5.0D, (double)f8 * 5.0D);
            RayTraceResult raytraceresult = worldIn.rayTraceBlocks(vec3d, vec3d1, true);
            if (raytraceresult == null) {
                return;
            }
            if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                return;
            }else {
                BlockPos pos = raytraceresult.getBlockPos();
                if (this == ModItems.dragon_horn_fire) {
                    EntityFireDragon dragon = new EntityFireDragon(worldIn);
                    dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    if (stack.getTagCompound() != null) {
                        dragon.readEntityFromNBT(stack.getTagCompound());
                    }
                    if (!worldIn.isRemote) {
                        worldIn.spawnEntityInWorld(dragon);
                    }

                }
                if (this == ModItems.dragon_horn_ice) {
                    EntityIceDragon dragon = new EntityIceDragon(worldIn);
                    dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    if (stack.getTagCompound() != null) {
                        dragon.readEntityFromNBT(stack.getTagCompound());
                    }
                    if (!worldIn.isRemote) {
                        worldIn.spawnEntityInWorld(dragon);
                    }
                }
                stack = new ItemStack(ModItems.dragon_horn);
                entityplayer.addStat(StatList.getObjectUseStats(this));
            }
        }

    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer entityplayer, EnumHand hand) {
        double d0 = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * 1.0D;
        double d1 = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * 1.0D + (double)entityplayer.getEyeHeight();
        double d2 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * 1.0D;
        float f = 1.0F;
        float f1 = entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * 1.0F;
        float f2 = entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * 1.0F;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        entityplayer.setActiveHand(hand);
        Vec3d vec3d1 = vec3d.addVector((double)f7 * 5.0D, (double)f6 * 5.0D, (double)f8 * 5.0D);
        RayTraceResult raytraceresult = worldIn.rayTraceBlocks(vec3d, vec3d1, true);
        if (raytraceresult == null) {
            return new ActionResult(EnumActionResult.PASS, itemStackIn);
        }
        if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult(EnumActionResult.PASS, itemStackIn);
        }else {
            BlockPos pos = raytraceresult.getBlockPos();
            if(this == ModItems.dragon_horn_fire){
                EntityFireDragon dragon = new EntityFireDragon(worldIn);
                dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                if(itemStackIn.getTagCompound() != null){
                    dragon.readEntityFromNBT(itemStackIn.getTagCompound());
                }
                if (!worldIn.isRemote) {
                    worldIn.spawnEntityInWorld(dragon);
                }
            }
            if(this == ModItems.dragon_horn_ice){
                EntityIceDragon dragon = new EntityIceDragon(worldIn);
                dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                if(itemStackIn.getTagCompound() != null){
                    dragon.readEntityFromNBT(itemStackIn.getTagCompound());
                }
                if (!worldIn.isRemote) {
                    worldIn.spawnEntityInWorld(dragon);
                }
            }
            entityplayer.addStat(StatList.getObjectUseStats(this));
            return new ActionResult(EnumActionResult.PASS, itemStackIn);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean f) {
        if (stack.getTagCompound() != null) {
            list.add("" + stack.getTagCompound().getBoolean("Gender"));
        }
    }
}
