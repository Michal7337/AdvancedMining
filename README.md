# AdvancedMining  
AdvancedMining is a plugin that adds a custom mining system. It functions like the one seen on Hypixel Skyblock.<br><br>
This plugin is still new so if you find any issues or have a suggestion, please create an Issue.
## How it works
The plugin adds Custom Blocks, which specify properties of the block. 
The required properties are:
* The ID
* The Name
* The Material
* The Strength
* The Hardness

There also are optional properties:
* The Best Tool to mine the block
* A Custom Texture (explained later)
* The Icon Material (explained later)
* The Drops File Id (explained later)
* The Break and Place sounds, which are not implemented as of now

Custom Blocks are stored as JSON files in the `plugins/AdvancedMining/Blocks` directory.<br>
To create a Custom Block you can use the `/advmining block create` command.<br>
To set the optional properties you can use the `/advmining block edit [id]` command.<br>

The **ID** of the block is used to identify the block in commands and other places.<br>
The **Name** is a string in the [MiniMessage Format](https://docs.advntr.dev/minimessage/format) (basically you can use tags like `<red>` or `<bold>`) that will be displayed in places like the Progress Bar.<br>
The **Material** is the block that will be placed in the world.<br>

The Player has 2 stats which are determined by the item they are holding: The **Mining Speed** and the **Breaking Power**. The held item can also specify the **Tool Type**.<br>
To give a tool or set stats to the item you're holding use the `/advmining tool` command.

**The whole mining system works by subtracting the player's Mining Speed from the mined block's Strength value every tick until it reaches 0.**<br>

This means that if the player has 10 Mining Speed and the block he's mining has 500 strength, it will take 50 ticks (2.5 seconds) to mine the block. <br>

To mine a block the player needs to have **Breaking Power** equal to or higher than the block's **hardness** value. <br>

The block can optionally specify the **Tool Type** required to mine it. The tool type is just a string that can be anything. If the block doesn't specify anything, it can be broken by any tool. If it does, only the specified tool type can mine the block. If the player isn't holding any items, their tool type is set to `hand`.<br>

When mining a Custom Block the plugin sets the player's `block_break_speed` attribute to 0 in order to get rid of the client side cracking animation and destroying the block. It then sends the cracking animation to the player when it needs to. With this system block breaking is completly server-side, so players can't use cheats like FastBreak. Currently the system allows a player to break one block at a time. If the player somehow starts mining a block when they are still mining another, it will be ignored.

### Configuration
In the plugin's folder is a file named `config.yml` (as is in pretty much all plugins). In the config you can set:
* `show-progress-bar` - If set to true, a progress bar will be displayed when mining blocks.
* `break-vanilla-blocks` - If set to true, players will be able to break vanilla blocks as they normally would. If set to false, the players will only be able to break Custom Blocks. 
* `cracking-animation-range` - Sets the range in which block cracking animations will be shown to other players when mining a block.

## More Advanced Stuff

### Custom Textures
The **Custom Texture** property you can optionally specify works by summoning an Item Display entity at the block's location. The entity displays an item with the `item_model` data component. The block property itself is just the key:value of the texture. <br>
This means that if you have a give command like this:<br>
`/give ExamplePlayer minecraft:stick[minecraft:item_model="foo:bar"]`<br>
Which gives you a stick with a block texture applied correctly, you set the `texture` property to `foo:bar`. If the property is set the entity will automatically be summoned when the block is placed and removed when the block is broken. Of course this requires a correctly made resource pack.<br>
When a custom block texture is set the **Icon Material** is used for the block break effect and giving the block to a player with the default command.<br>
If you have a custom texture applied to a block the **Material** needs to be set to something transparent (like glass), otherwise the display entity will be black and will not respond to light level changes.<br>
The summoned display entities have the following tags so you can manipulate them easily:
* `advmining_block`
* `advmining_block_{blockId}`
* `advmining_block_loc_{x}_{y}_{z}`

Example of a working custom texture:<br>
[![FTMvqk7.md.png](https://iili.io/FTMvqk7.md.png)](https://freeimage.host/i/FTMvqk7)<br>
[![FTMv3rl.md.png](https://iili.io/FTMv3rl.md.png)](https://freeimage.host/i/FTMv3rl)<br>
[![FTMvK22.md.png](https://iili.io/FTMvK22.md.png)](https://freeimage.host/i/FTMvK22)<br>

### Block Drops
The optional `DropsFile` property is the id of a **Block Drops** file inside the `plugins/AdvancedMining/BlockDrops` directory (it's not the name of the file, but the name you specify when making the drops). The files are in a binary format, so you can't really edit them outside of the in-game command or in code.<br>
The drops is basically a list of items, each having a **Chance** to drop (a value between 0.0 and 1.0), a **Minimum Amount** and a **Maximum Amount**. When a block is broken, if it has a valid drops id specified, it will roll each of these Items with the specified chance and a random amount between the specified min and max values. It will then drop all of the rolled items. <br>
With the system using IDs for referencing these drops, you can set the same drops to multiple blocks.<br>
To create a Drops File use the `/advmining drops create` comand. To add an item to said drop, use `/advmining drops edit {dropId}`.<br>
There is a convienience command `/advmining block edit {BlockId} add-drop-itself` command that adds a guaranteed placeable Custom Block of the edited block to the drops file or creates a new one if it doesn't exist.<br>

## Miscellaneous 

### Default Blocks and Tools
The plugin has a system for setting a normal block type to be a custom block. To set a default block use `/advmining block set-default`. E.g. `/advmining block set-default minecraft:stone stone_block` will make all stone be the `stone_block` Custom Block.<br>
The same can be done with Items and Tools. The command `/advmining tool set-default` sets an Item to always have the specified stats. E.g. `/advmining tool set-default minecraft:breeze_rod 2137 6 pickaxe` will make every breeze rod have 2137 mining speed, 6 breaking power and be the pickaxe tool type. The tool type is optional.<br>
All default blocks and tools can be overriden by placing a custom block or setting the tool with the command.

### Other commands
There are also other commands not mentioned above:<br>
`/advmining block give` gives you a placeable Custom Block<br>
`/advmining block place` places a Custom Block at the given location<br>
`/advmining reload` reloads the plugin's config, all the Blocks, Block Drops and default Tools and Blocks

### Planned / Considered Features
Currently I am considering adding the following features:
* Optional keeping of the mining progress when the player stops mining a block and starts mining it again (with the same tool)
* Optional allowing the player to mine multiple blocks at once when the above feature is enabled
* Configurable progress bar color

I am of course open to suggestions, so if you have any, create an Issue (with the Enhancement tag).

## API For Developers

The plugin has an api that allows developers to customize the plugin's behavior. To use it for now you have to add the plugin jar as a dependency (maybie I'll make a repository at some point). I tried to document the plugin somewhat to make it understandable.
### The Main Things
The main things you will be interacting with are the **CustomBlock** class and the **BlockDrops** class.<br>
The CustomBlock class represents a Custom Block and all of it's properties. It has methods to place and retrieve placed blocks and do some other things. To have a block work you need to either put it in the `CustomBlock.loadedBlocks()` map or use an Event.<br>
The BlockDrops class represents a Block Drops object and all of it's properties. Among other things it has a method to roll the drops it contains. To have a block drop work you need to either put it in the `BlockDrops.loadedDrops()` map or use an Event.<br>

There also is the `BlockDataStorage` class which is used to store data about blocks in the Chunk's `PersistentDataContainer`. You can use the `getDataContainer(Block)` method to get a `PersistentDataContainer` linked to the block. Note that changes to that container aren't saved, you must use `setDataContainer()` to save the changes. There is a utility method to edit the container: `editDataContainer()` which takes a lambda. This class is useful and unrelated to this specific plugin, so you may copy and use it in your own projects, which need to store data in Blocks.

### Events
There are three events the API has:
* `CustomBlockBreakEvent` is fired when a Custom Block is broken. It has the Player, Block and Custom Block. In this event you can set the BlockDrops object used for block drops (set it to null if you don't want drops). You can also set if the block break effect should be played and if the custom block id should be removed from the physical block. If the event is cancelled the block doesn't break and the breaking progress is reset.
* `CustomBlockBreakStartEvent` is fired when a Player starts mining a Custom Block. It has the Player who is breaking the block, and the physical Block which is being broken. In this event you can set the CustomBlock object of the broken block and the mining stats (Mining Speed, Breaking Power and Tool Type). If this event is cancelled the block mining process doesn't start.
* `CustomBlockBreakProgressEvent` is fired every tick when a Custom Block is being mined. It has the Player, Block, Custom Block and the current Tick of the mining process. In this event you can set the overall progress of the block mining (from the block's strength to 0), the progress that will be done this tick and the block's destroy stage (the cracking animation, value from 0.0 to 1.0). If this event is cancelled, the mining won't progress this tick.<br>

This example code makes the mining animation appear backwards:
```
@EventHandler
public void onBlockDamage(@NotNull CustomBlockBreakProgressEvent event) {

    event.setBreakStage(1 - event.breakStage());
    
}
```

And this example increments the Mining Speed by 420 if the mined block's id is "cinema":
```
@EventHandler
public void onCustomBlockBreakStart(@NotNull CustomBlockBreakStartEvent event) {

    if (event.getCustomBlock().id().equals("cinema"))
        event.setMiningSpeed(event.getMiningSpeed() + 420);

}
```

