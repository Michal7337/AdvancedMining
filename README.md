# AdvancedMining  
AdvancedMining is a plugin that adds a custom mining system. It functions like the one seen on Hypixel Skyblock.<br><br>
If you find any issues or have a suggestion, please create an Issue on the GitHub.
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
* The Break and Place sounds

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

When mining a Custom Block the plugin sets the player's `block_break_speed` attribute to 0 in order to get rid of the client side cracking animation and destroying the block. It then sends the cracking animation to the player when it needs to. With this system block breaking is completely server-side, so players can't use cheats like FastBreak. 

### Configuration
In the plugin's folder is a file named `config.yml` (as is in pretty much all plugins). In the config you can set:
* `show-progress-bar` - If set to true, a progress bar will be displayed when mining blocks.
* `progress-bar-color` - Sets the color of the mining progress bar. Possible colors: blue, red, pink, green, yellow, purple, white
* `break-vanilla-blocks` - If set to true, players will be able to break vanilla blocks as they normally would. If set to false, the players will only be able to break Custom Blocks. 
* `cracking-animation-range` - Sets the range in which block cracking animations will be shown to other players when mining a block.
* `allow-breaking-multiple-blocks` - If set to true, players will be able to pause mining a block and start mining another one without loosing progress. Note: when block mining is paused, the mining task is still ticking, just not calculating the mining, so setting a high block limit *may* cause performance issues at a large scale.
* `simultaneous-broken-blocks-limit` - Sets the amount of blocks a player can have mining progress on at the same time. Set to one if you simply want to be able to stop mining a block and resume later. 
* `mining-progress-reset-timer` - Sets the amount of ticks after which progress on perviously mined blocks is reset.
* `allow-tool-swapping` - Sets if the player should be able to resume mining a block with a different tool to what they originally used to mine it.
* `enchantments` - This section defines the behavior of enchantments. Efficiency can increase Mining Speed and Fortune can increase Block Drops. Both are disabled by default. If you are updating from an older version, copy the settings form the config file [here](https://github.com/Michal7337/AdvancedMining/blob/master/src/main/resources/config.yml).
* `effects` - This section defines the behavior of potion effects. haste can increase Mining Speed and Mining Fatigue can decrease it. Both are disabled by default. If you are updating from an older version, copy the settings form the config file [here](https://github.com/Michal7337/AdvancedMining/blob/master/src/main/resources/config.yml).

## More Advanced Stuff

### Block Drops
The optional `DropsFile` property is the id of a **Block Drops** file inside the `plugins/AdvancedMining/BlockDrops` directory (it's not the name of the file, but the name you specify when making the drops). The files are in a binary format, so you can't really edit them outside the in-game command or in code.<br>
The Drops File is a list of **Entries**. Each Entry has an **ID**, a **chance** to be rolled (a value between 0.0 and 1.0), a **minimum and maximum amount** of items and the **Item** itself that will be rolled. <br>
It also has optional properties: 
* `AffectedByFortune` - Sets if the drop is affected by the Fortune enchantment. Off by default.
* `SilkTouchOnly` - Sets if the drop should only be rolled when the tool has Silk Touch. Off by default.
* `NoRollByDefault` - If enabled, this entry will only be rolled as an **Extra Drop**. Off by default. Explained below.

Each entry can have multiple **Extra Drops**. These are just other entries. These entries will be rolled before the actual entry that has them. If any one of them drops, the chain is stopped and the item it rolled is dropped.<br>
This is used for creating chains of drops. For example: You have a stone block that you want to drop resources. Some may be rarer than others. So you set a main entry of **Cobblestone** and add entries for your resources. So: first entry: [diamond, 2% chance], second entry: [iron_ingot, 10% chance], third entry: [copper_ingot, 40% chance].<br>
Then you set **NoRollByDefault** of all three of them to **true**. This will result in them not be rolled when breaking the block. Instead, we add them as **Extra Drops** on the main cobblestone Entry in order from rarest to most common.<br>
The result of all this is that when the block is broken the **Cobblestone** Entry will be rolled, but since it has Extra Drops set, those will be rolled first. So: 2% for a diamond to drop. If It drops, then stop here, else continue to the iron. 10% for it to drop. If it does, stop here, else continue to the copper. If it drops, stop here, else drop the main entry - Cobblestone. 


To create a Drops File use the `/advmining drops create`command.<br>
There is a convenience command `/advmining block edit <BlockId> add-drop-itself` command that adds a guaranteed placeable Custom Block of the edited block to the drops file or creates a new one if it doesn't exist.<br>
To edit a Drop config us the `/advmining drops edit <drops-id>` command. It has subcommands for Cresting, Editing and Removing Entries.<br>
<br>
Note: Block Drop configurations from previous versions are automatically migrated and their entries are named

### Block regeneration

Each Custom Block can be set to automatically regenerate after being broken.<br>
For that the command `/advmining block edit <block-id> regeneration` is used. <br>
There you can set the **Primary** Block Regeneration properties and **Alternate** Block Regeneration Properties.<br>
The Primary Block Regen fires every time a Block is broken. You can also set an Alternate regeneration, which has a chance to happen instead of the primary one. 
This is useful if you want to e.g. create something similar to Hypixel Skyblock, where if you break Mythril, there is a chance for a Titanium block to spawn instead of Mythril regenerating.<br>
In both Regen Types you first need to specify either `regen-vanilla` or `regen-custom` and then set a Material or a Custom Block. This specifies the block that will be placed after the **Regen Time** is over.<br>
Regen Time specifies in Ticks, how long the block will be regenerating. During this time the block is replaced with a temporary block, which you can choose to be a vanilla block or a Custom Block.<br>
You can also specify the **delay** after which the regeneration will start.<br>
You can specify `no-regen` in the command to disable the respective block regeneration. 



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

## Miscellaneous 

### Default Blocks and Tools
The plugin has a system for setting a normal block type to be a custom block. To set a default block use `/advmining block set-default`. E.g. `/advmining block set-default minecraft:stone stone_block` will make all stone be the `stone_block` Custom Block.<br>
The same can be done with Items and Tools. The command `/advmining tool set-default` sets an Item to always have the specified stats. E.g. `/advmining tool set-default minecraft:breeze_rod 2137 6 pickaxe` will make every breeze rod have 2137 mining speed, 6 breaking power and be the pickaxe tool type. The tool type is optional.<br>
All default blocks and tools can be overridden by placing a custom block or setting the tool with the command.

### Other commands
There are other commands not mentioned above:<br>
`/advmining block give` gives you a placeable Custom Block<br>
`/advmining block set-hand` sets the id of the block you're holding<br>
`/advmining block place` places a Custom Block at the given location<br>
`/advmining block fill` fills a given area with the specified custom block. You can optionally specify a block to replace<br>
`/advmining reload` reloads the plugin's config, all the Blocks, Block Drops and default Tools and Blocks

### Planned / Considered Features
Currently, I am considering adding the following features:
* Optional keeping of the mining progress when the player stops mining a block and starts mining it again (with the same tool) [Added in v1.1]
* Optional allowing the player to mine multiple blocks at once when the above feature is enabled [Added in v1.1]
* Configurable progress bar color [Added in v1.1]

I am of course open to suggestions, so if you have any, create an Issue (with the Enhancement tag).

### Notes

Blocks that break instantly, such as TNT or slime may behave weirdly when the `break-vanilla-blocks` option is set to true.


## API For Developers

The plugin has an api that allows developers to customize the plugin's behavior. To use it for now you have to add the plugin jar as a dependency (maybe I'll make a repository at some point). I tried to document the plugin somewhat to make it understandable.
### The Main Things
The main things you will be interacting with are the **CustomBlock** class and the **BlockDrops** class.<br>
The CustomBlock class represents a Custom Block and all of its properties. It has methods to place and retrieve placed blocks and do some other things. To have a block work you need to either put it in the `CustomBlock.loadedBlocks()` map or use an Event.<br>
The BlockDrops class represents a Block Drops object and all of its properties. Among other things it has a method to roll the drops it contains. To have a block drop work you need to either put it in the `BlockDrops.loadedDrops()` map or use an Event.<br>

There also is the `BlockDataStorage` class which is used to store data about blocks in the Chunk's `PersistentDataContainer`. You can use the `getDataContainer(Block)` method to get a `PersistentDataContainer` linked to the block. Note that changes to that container aren't saved, you must use `setDataContainer()` to save the changes. There is a utility method to edit the container: `editDataContainer()` which takes a lambda. This class is useful and unrelated to this specific plugin, so you may copy and use it in your own projects, which need to store data in Blocks.

### Events
There are three events the API has:
* `CustomBlockBreakEvent` is fired when a Custom Block is broken. It has the Player, Block and Custom Block. In this event you can set the BlockDrops object used for block drops (set it to null if you don't want drops). You can also set if the block break effect should be played and if the custom block id should be removed from the physical block. If the event is canceled the block doesn't break and the breaking progress is reset.
* `CustomBlockBreakStartEvent` is fired when a Player starts mining a Custom Block. It has the Player who is breaking the block, and the physical Block which is being broken. In this event you can set the CustomBlock object of the broken block and the mining stats (Mining Speed, Breaking Power and Tool Type). If this event is canceled the block mining process doesn't start.
* `CustomBlockBreakProgressEvent` is fired every tick when a Custom Block is being mined. It has the Player, Block, Custom Block and the current Tick of the mining process. In this event you can set the overall progress of the block mining (from the block's strength to 0), the progress that will be done this tick and the block's destroy stage (the cracking animation, value from 0.0 to 1.0). If this event is canceled, the mining won't progress this tick.<br>

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

