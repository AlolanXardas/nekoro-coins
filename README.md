# Nekoro Coins

120 coin items with their own textures, for shops, quests, lootboxes and collecting. Made mainly for my Nekoro Pokemon modpack.

Download: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/nekoro-c)

## Versions

| Minecraft | Loaders |
|---|---|
| 1.21.1 | NeoForge, Forge, Fabric |
| 1.20.1 | Forge, Fabric |

Item ids are `nekorocoins:coin001` to `nekorocoins:coin120`.

## Building

Needs JDK 21. The code and textures are shared in `source/common`; every loader and Minecraft version has its own Gradle project in `source/`.

```
cd source
.\build-all.ps1                  # all projects, jars go to ..\Gotowe\<version>
.\build-all.ps1 fabric-1.21.1    # selected ones
```

Or run `gradlew build` inside one project folder.

## License

MIT, see [LICENSE](LICENSE). Mod and textures by Alolan Xardas, code written with Claude.
