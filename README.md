Connected
=========
Connected! is a connected textures API for Minecraft 1.7.10 that allows your blocks to have connected textures in multiple forms.  
The ones that are implemented right now are:
- Normal blocks
- FMP microblocks
- BuildCraft facades
- EnderIO painted blocks
- EnderIO conduit facades
- Blocks implementing CoFHLib's *IBlockAppearance* interface
  
Other mod blocks I'm going to try to add support for are:
- AE2 Facades (I'm currently working on a PR to get it working)
- Carpenter's Blocks

Usage in mods as an API
=========
This API can be redistributed in order to work without the mod installed and so that you don't have to have your own implementation of connected textues. The redistributable version only includes support for normal blocks, but when the mod is installed it'll add support for all the other types.  
Including the redistributable version in your mod is as easy as copying a package called *com.amadornes.connected.api* over to your project's source, but remember to keep the package name the same or there'll be API collisions.  
  
Then you can use all the methods and interfaces that are provided in there to make your blocks have connected textures. A more detailed tutorial on how to use it will be added as soon as the API is finished.  
  
Feel free to implement (and include) it in your mods and if you do so, please tell me! I'd love to know what mods are using the API :)

License
=========
This project is running under the MIT license, which states that you can copy and redistribute any of its code as long as I'm mentioned as an author and you have a link to the source (this repository).
