package info.jbcs.minecraft.vending;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityVendingMachine extends TileEntity implements IInventory, ISidedInventory {
	public String ownerName = "";
	public ItemStack sold = null;
	public ItemStack bought = null;
	boolean advanced = false;
	boolean infinite = false;

	private static final int[] side0 = new int[] { };

	InventoryStatic inventory = new InventoryStatic(11) {
		@Override
		public String getInventoryName() {
			return "Vending Machine";
		}

		@Override
		public void onInventoryChanged() {
			if (worldObj == null) {
				return;
			}

			if (!ItemStack.areItemStacksEqual(sold, getSoldItem()) || !ItemStack.areItemStacksEqual(bought, getBoughtItem())) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				sold = getSoldItem();

				if (sold != null) {
					sold = sold.copy();
				}

				bought = getBoughtItem();

				if (bought != null) {
					bought = bought.copy();
				}
			}
		}

		@Override
		public void markDirty() {

		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != TileEntityVendingMachine.this) {
				return false;
			} else {
				return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
			}
		}
	};

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory() + (advanced ? -1 : 0);
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	public ItemStack getSoldItem() {
		return inventory.getStackInSlot(9);
	}

	public ItemStack getBoughtItem() {
		return inventory.getStackInSlot(10);
	}

	public void setBoughtItem(ItemStack stack) {
		inventory.setInventorySlotContents(10, stack);
	}

	public boolean doesStackFit(ItemStack itemstack) {
		for (int i = 0; i < 9; i++) {
			if (inventory.items[i] == null) {
				return true;
			}

			if (inventory.items[i].getItem() != itemstack.getItem() && inventory.items[i].isStackable()) {
				continue;
			}

			if (inventory.items[i].stackSize + itemstack.stackSize > inventory.items[i].getMaxStackSize()) {
				continue;
			}

			if ((inventory.items[i].getHasSubtypes() && inventory.items[i].getItemDamage() != itemstack.getItemDamage())) {
				continue;
			}

			return true;
		}

		return false;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (advanced && i == 10) {
			return;
		}

		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		inventory.clear();
		inventory.readFromNBT(nbttagcompound);
		ownerName = nbttagcompound.getString("owner");
		advanced = nbttagcompound.getBoolean("advanced");
		infinite = nbttagcompound.getBoolean("infinite");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);
		nbttagcompound.setString("owner", ownerName);
		nbttagcompound.setBoolean("advanced", advanced);
		nbttagcompound.setBoolean("infinite", infinite);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound var1 = new NBTTagCompound();
		this.writeToNBT(var1);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, var1);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 10) {
			return false;
		}

		return true;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, int par3) {
		return this.isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) {
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side0;
	}
}


