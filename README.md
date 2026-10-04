# faux

![logo](src/main/resources/assets/faux/faux.png)

![discord yedel](https://img.shields.io/badge/discord-yedel-blue)

![github release version](https://img.shields.io/github/v/release/Yedelo/faux?include_prereleases&label=github)

![modrinth latest version](https://img.shields.io/modrinth/v/lDJcSaOT?label=modrinth)

This mod (java agent) mutes Fabric Loader dependencies by scanning the mods folder, looking for dependency relations,  
and generating an inverse in `config/fabric-loader-dependencies.json`.
This does not create any compatibility with any mods beyond removing the initial loader crash.

## Usages

- Swapping mods with the same effective functionality (e.g. `legacy-lwjgl3` vs `pylon` on Ornithe)
- Dealing with changing mod id's (e.g. `lumen` -> `lenis` -> `pylon` on Ornithe (rdh))
- Testing mods on different Minecraft versions (e.g. 26.2 -> 26.3)
- Removing hostile `breaks` relations (e.g. SkyHanni breaking mod hiders)

## Unsupported

- Dependencies from Jar-in-Jar
- Bad formatting. If your mod is missing a fabric.mod.json or your custom mods path file points to mystery dust, do not expect error handling from me.

## Dependencies

Ironic...  
[Fabric Loader](https://github.com/FabricMC/fabric-loader/) >= 0.11.1 (adds dependency overrides)

