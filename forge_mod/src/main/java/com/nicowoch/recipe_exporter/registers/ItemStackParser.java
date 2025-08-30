package com.nicowoch.recipe_exporter.registers;

import com.google.common.collect.Lists;
import com.nicowoch.recipe_exporter.logging.Logger;
import com.nicowoch.recipe_exporter.objects.ItemSpecs;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class ItemStackParser extends StackParserBase<ItemStack> {

    @Override
    protected Class<ItemStack> GetStackType() {
        return ItemStack.class;
    }

    @Override
    protected void parseStack(ItemStack stack, ParsingResult result) {
        Item item = stack.getItem();

        result.item.type = "Item";
        result.item.id = Objects.toString(item.getRegistryName());
        result.item.display_name = stack.getDisplayName();
        result.item.metadata = stack.getMetadata();

        result.item.specific_data = new ItemSpecs();
        parseSpecificData(stack, result.item.specific_data);

        result.ingredient.count = stack.getCount();

        result.item.icon_index = getRegisteredIcon(stack);
    }

    private void parseSpecificData(ItemStack stack, ItemSpecs data) {
        Item item = stack.getItem();

        String uid_without_nbt = Objects.requireNonNull(item.getRegistryName()).getNamespace();
        uid_without_nbt += "__" + item.getRegistryName().getPath();
        uid_without_nbt += "__" + stack.getMetadata();

        String nbt = parseNBT(stack);
        String uid =  uid_without_nbt;

        if (!nbt.isEmpty() && !nbt.equals("{}")) {
            uid += "__" + nbt;
        }

        data.add("base_uid", uid_without_nbt);
        data.add("item_uid", uid);
        data.add("damageable", stack.isItemStackDamageable());
        data.add("has_subtypes", stack.getHasSubtypes());
        data.add("stack_limit", stack.getMaxStackSize());
        data.add("max_damage", stack.getMaxDamage());
        data.add("nbt", nbt);
    }

    private String parseNBT(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound() != null) {
            return stack.getTagCompound().toString();
        } else {
            return "{}";
        }
    }

    private int getRegisteredIcon(ItemStack stack) {
        try {
            BufferedImage icon = findIcon(stack);

            if (icon == null) {
                throw new IllegalStateException("Icon not found for ItemStack: " + stack);
            }

            return iconsRegister.registerBufferedImage(icon);
        } catch (Exception e) {
            Logger.error(e, stack);
        }

        return iconsRegister.registerNoImage();
    }

    @Nullable
    private BufferedImage findIcon(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();

        TextureAtlasSprite sprite = findIconSprite(stack, mc);

        if (sprite == null || sprite.getFrameCount() <= 0) {
            return null;
        }

        int[][] frameData =  sprite.getFrameTextureData(0);

        int[] pixels = frameData[0]; // Use mipmap level 0 for full quality

        int width = sprite.getIconWidth();
        int height = sprite.getIconHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        image.setRGB(0, 0, width, height, pixels, 0, width);

        return image;
    }

    @Nullable
    private TextureAtlasSprite findIconSprite(ItemStack stack, Minecraft mc) {
        try {
            List<Supplier<TextureAtlasSprite>> tries = Lists.newArrayList(
                    () -> {
                        IBakedModel model = mc.getRenderItem().getItemModelWithOverrides(stack, null, null);

                        return getTextureAtlasSpriteFromModel(mc, model, stack);
                    },
                    () -> {
                        IBakedModel model = mc.getRenderItem().getItemModelMesher().getItemModel(stack);

                        return getTextureAtlasSpriteFromModel(mc, model, stack);
                    },
                    () -> {
                        if (stack.getItem() instanceof ItemBlock) {
                            Block block = ((ItemBlock) stack.getItem()).getBlock();
                            return mc.getBlockRendererDispatcher().getBlockModelShapes()
                                    .getTexture(block.getDefaultState());
                        }

                        return null;
                    },
                    () -> {
                        if (stack.getItem() instanceof ItemBlock) {
                            Block block = ((ItemBlock) stack.getItem()).getBlock();

                            return mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/" + block.getRegistryName().getPath());
                        }

                        return null;
                    }
            );

            for (Supplier<TextureAtlasSprite> trie : tries) {
                try {
                    return trie.get();
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            Logger.error(e, "Error from IconsRegistry", stack);
        }

        return null;
    }

    private TextureAtlasSprite getTextureAtlasSpriteFromModel(Minecraft mc, IBakedModel model, ItemStack stack) {
        List<BakedQuad> quads = model.getQuads(null, null, 0L);

        if (!quads.isEmpty()) {
            return quads.get(0).getSprite();
        }

        if (model.getParticleTexture() != null) {
            return mc.getTextureMapBlocks().getAtlasSprite(model.getParticleTexture().getIconName());
        }

        if (model.isBuiltInRenderer()) {
            TextureMap textureMap = mc.getTextureMapBlocks();
            Item item =  stack.getItem();

            if (item == Items.BED) {
                return textureMap.getAtlasSprite("minecraft:blocks/bed_head");
            }
            if (item == Items.SHIELD) {
                return textureMap.getAtlasSprite("minecraft:items/shield_base");
            }
            if (item == Items.BANNER) {
                return textureMap.getAtlasSprite("minecraft:blocks/banner_base");
            }
            if (item == Items.SKULL) {
                return textureMap.getAtlasSprite("minecraft:blocks/skull");
            }
            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof BlockChest) {
                    BlockChest.Type type = ((BlockChest) block).chestType;
                    switch (type) {
                        case BASIC:
                            return textureMap.getAtlasSprite("minecraft:blocks/chest");
                        case TRAP:
                            return textureMap.getAtlasSprite("minecraft:blocks/trapped_chest");
                    }
                } else if (block instanceof BlockEnderChest) {
                    return textureMap.getAtlasSprite("minecraft:blocks/ender_chest");
                } else if (block instanceof BlockShulkerBox) {
                    // Use the block registry name to find the sprite
                    String blockName = block.getRegistryName().toString(); // e.g., minecraft:red_shulker_box
                    return mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/" + blockName);
                }
            }
        }

        return null;
    }
}
