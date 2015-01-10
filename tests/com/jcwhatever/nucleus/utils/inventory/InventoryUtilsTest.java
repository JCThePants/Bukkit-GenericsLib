package com.jcwhatever.nucleus.utils.inventory;

import com.jcwhatever.dummy.DummyInventory;
import com.jcwhatever.dummy.DummyServer;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;
import com.jcwhatever.nucleus.utils.items.ItemStackComparer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class InventoryUtilsTest {

    private ItemStack[] getInventoryStack() {
        return new ItemStack[] {
                new ItemStackBuilder(Material.WOOD).amount(5).build(),
                null,
                new ItemStackBuilder(Material.GLASS).amount(64).build(),
                new ItemStackBuilder(Material.STONE).amount(5).build(),
                new ItemStackBuilder(Material.GRASS).amount(32).build(),
                null,
                null,
                null,
                null
        };
    }

    private Inventory getInventory() {

        DummyInventory inventory = new DummyInventory(null, InventoryType.CHEST, 9);
        inventory.setContents(getInventoryStack());
        return inventory;
    }

    @BeforeClass
    public static void testStartup() {
        Bukkit.setServer(new DummyServer());
    }

    @Test
    public void testAdd() throws Exception {

        ItemStack[] stacks = getInventoryStack();

        ItemStack toAdd = new ItemStackBuilder(Material.PAPER).amount(5).build();

        List<ItemStack> itemStackList =
                InventoryUtils.add(stacks, toAdd);

        Assert.assertEquals(0, itemStackList.size());

        Assert.assertEquals(toAdd, stacks[1]);
    }

    @Test
    public void testGetMax() throws Exception {
        ItemStack[] stacks = getInventoryStack();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        int max = InventoryUtils.getMax(stacks, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals((5 * 64) + 32, max);
    }

    @Test
    public void testGetMax1() throws Exception {
        Inventory inventory = getInventory();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        int max = InventoryUtils.getMax(inventory, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals((5 * 64) + 32, max);
    }

    @Test
    public void testHasRoom() throws Exception {
        ItemStack[] contents = getInventoryStack();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        boolean result = InventoryUtils.hasRoom(contents, toCheck, ItemStackComparer.getTypeComparer(), toCheck.getAmount());

        Assert.assertEquals(true, result);

        result = InventoryUtils.hasRoom(contents, toCheck, ItemStackComparer.getTypeComparer(), 1000);

        Assert.assertEquals(false, result);
    }

    @Test
    public void testHasRoom1() throws Exception {
        Inventory inventory = getInventory();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        boolean result = InventoryUtils.hasRoom(inventory, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals(true, result);

        result = InventoryUtils.hasRoom(inventory, toCheck, 1000);

        Assert.assertEquals(false, result);
    }

    @Test
    public void testCount() throws Exception {

        ItemStack[] contents = getInventoryStack();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        int total = InventoryUtils.count(contents, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals(32, total);
    }

    @Test
    public void testCount1() throws Exception {

        Inventory inventory = getInventory();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        int total = InventoryUtils.count(inventory, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals(32, total);
    }

    @Test
    public void testHas() throws Exception {

        ItemStack[] contents = getInventoryStack();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        boolean result = InventoryUtils.has(contents, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals(true, result);


        toCheck = new ItemStackBuilder(Material.REDSTONE_BLOCK).amount(5).build();

        result = InventoryUtils.has(contents, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals(false, result);
    }

    @Test
    public void testHas1() throws Exception {

        Inventory inventory = getInventory();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        boolean result = InventoryUtils.has(inventory, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals(true, result);


        toCheck = new ItemStackBuilder(Material.REDSTONE_BLOCK).amount(5).build();

        result = InventoryUtils.has(inventory, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertEquals(false, result);
    }

    @Test
    public void testHas2() throws Exception {

        ItemStack[] contents = getInventoryStack();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        boolean result = InventoryUtils.has(contents, toCheck, ItemStackComparer.getTypeComparer(), 5);

        Assert.assertEquals(true, result);


        result = InventoryUtils.has(contents, toCheck, ItemStackComparer.getTypeComparer(), 33);

        Assert.assertEquals(false, result);
    }

    @Test
    public void testHas3() throws Exception {

        Inventory inventory = getInventory();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(5).build();

        boolean result = InventoryUtils.has(inventory, toCheck, ItemStackComparer.getTypeComparer(), 5);

        Assert.assertEquals(true, result);


        result = InventoryUtils.has(inventory, toCheck, ItemStackComparer.getTypeComparer(), 33);

        Assert.assertEquals(false, result);
    }

    @Test
    public void testGetAll() throws Exception {

        ItemStack[] contents = getInventoryStack();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(32).build();

        ItemStack[] result = InventoryUtils.getAll(contents, toCheck, ItemStackComparer.getTypeComparer());

        Assert.assertArrayEquals(new ItemStack[] { toCheck }, result);
    }

    @Test
    public void testGetAll1() throws Exception {

        Inventory inventory = getInventory();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(32).build();

        ItemStack[] result = InventoryUtils.getAll(inventory, toCheck);

        Assert.assertArrayEquals(new ItemStack[] { toCheck }, result);
    }

    @Test
    public void testRemove() throws Exception {
        ItemStack[] contents = getInventoryStack();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(32).build();

        List<ItemStack> result = InventoryUtils.remove(contents, toCheck);

        ItemStack[] resultArray = result.toArray(new ItemStack[result.size()]);

        Assert.assertArrayEquals(new ItemStack[] { toCheck }, resultArray);
    }

    @Test
    public void testRemove1() throws Exception {
        Inventory inventory = getInventory();

        ItemStack toCheck = new ItemStackBuilder(Material.GRASS).amount(32).build();

        List<ItemStack> result = InventoryUtils.remove(inventory, toCheck);

        ItemStack[] resultArray = result.toArray(new ItemStack[result.size()]);

        Assert.assertArrayEquals(new ItemStack[] { toCheck }, resultArray);
    }

    @Test
    public void testClearAll() throws Exception {
        Inventory inventory = getInventory();

        InventoryUtils.clearAll(inventory);

        ItemStack[] result = ArrayUtils.removeNull(inventory.getContents());

        Assert.assertEquals(0, result.length);
    }

    @Test
    public void testRepairAll() throws Exception {
        Inventory inventory = getInventory();

        ItemStack broken = new ItemStackBuilder(Material.ANVIL).durability(50).build();

        inventory.setItem(0, broken);

        InventoryUtils.repairAll(inventory);

        Assert.assertEquals(-32768, inventory.getItem(0).getDurability());
    }

    @Test
    public void testIsEmpty() throws Exception {

        Inventory inventory = getInventory();

        Assert.assertEquals(false, InventoryUtils.isEmpty(inventory));

        inventory.clear();

        Assert.assertEquals(true, InventoryUtils.isEmpty(inventory));
    }
}