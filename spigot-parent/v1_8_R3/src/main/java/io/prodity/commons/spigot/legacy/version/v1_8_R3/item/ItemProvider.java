package io.prodity.commons.spigot.legacy.version.v1_8_R3.item;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemProvider implements io.prodity.commons.spigot.legacy.item.ItemProvider {

	@Override
	public Object toCraftItemStack(ItemStack itemStack) {
		return CraftItemStack.asCraftCopy(itemStack);
	}

	@Override
	public ItemStack toBukkitItemStack(Object itemStack) {
		if (itemStack instanceof CraftItemStack) {
			return (CraftItemStack) itemStack;
		}

		return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_8_R3.ItemStack) itemStack);
	}

}
