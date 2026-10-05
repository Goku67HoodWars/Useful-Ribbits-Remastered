# Useful Ribbits

A multi-loader continuation of the **Useful Ribbits** mod, maintained across **multiple
Minecraft versions**.

Useful Ribbits adds *job ribbits* — little frog folk who actually work for you. **Chef**
ribbits cook raw food in nearby smokers, **miner** ribbits head underground, and
**farmer** ribbits plant and tend your crops. Assign them from the ribbit bed and let
them gather and craft while you get on with everything else.

Built from one shared codebase (Architectury) for **Fabric, Forge, and NeoForge**, published
as a community continuation with the blessing of the original author, **Rogue_one12**.

## Versions

Each Minecraft version lives on its own branch and has its own release.

| Minecraft | Loaders | Branch | Release |
|---|---|---|---|
| **26.3** | Fabric · Forge · NeoForge | [`main`](../../tree/main) | [26.3-1.0.0](../../releases/tag/26.3-1.0.0) |
| **26.2** | Fabric · Forge · NeoForge | [`26.2`](../../tree/26.2) | [v1.0.0](../../releases/tag/v1.0.0) |

Animations are powered by **GeckoLib**, the only runtime dependency (the Fabric build also needs
**Fabric API**).

## Porting & development docs

Written up in [`docs/`](docs) for anyone continuing or rebuilding this mod:

- [**Porting Playbook**](docs/Porting-Playbook.md) — a reusable, loader/version-agnostic guide to
  porting any Minecraft mod (decision tree, toolchain, cross-loader map, API-change cheatsheet,
  GUI/render cookbook, crash-guard patterns, validation, distribution, difficulty tiering).
- [**Useful Ribbits Port Case Study**](docs/Useful-Ribbits-Port-Case-Study.md) — the true 1:1 account
  of taking this mod from 1.20.1 Forge to multiloader on all three loaders, including the hard bugs.
- [**Writing a Mod From Scratch**](docs/Writing-A-26.2-Mod-From-Scratch.md) — a greenfield
  companion: clean multiloader layout, modern registration, the data-component + retained-render model.

## Credits

- Original **Useful Ribbits** mod by **rogue_one (Rogue_one12)** — concept, design, art, and code.
- Original project: <https://www.curseforge.com/minecraft/mc-mods/useful-ribbits>

Useful Ribbits builds on the **Ribbits** mod; the base ribbit and its assets remain
© Refresh Studios and Bonsai Studios (see [`LICENSE`](LICENSE)). Animations are powered
by **GeckoLib**, the only runtime dependency.

This multi-loader build updates rogue_one's work to current Minecraft versions with
cross-loader registration and a dependency-free config screen, published with the
original author's blessing.

## License

- **Code** is licensed under the **GNU LGPL v3** (see [`LICENSE`](LICENSE)).
- **Assets** (textures, sounds, models, and the Ribbit itself) remain
  **© 2025 Refresh Studios and Bonsai Studios, All Rights Reserved**, per the
  original project's license notice, which is preserved in [`LICENSE`](LICENSE).

## Building

Requires JDK 25. Check out the branch for the Minecraft version you want, then:

```
./gradlew build
```

Builds all loaders; the distributable jars land in `fabric/build/libs/`,
`forge/build/libs/`, and `neoforge/build/libs/` (named
`UsefulRibbits-<mc>-<Loader>-<version>.jar`).
