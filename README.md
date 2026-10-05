# Useful Ribbits

A multi-loader continuation of the **Useful Ribbits** mod for Minecraft **26.2**.

Useful Ribbits adds *job ribbits* — little frog folk who actually work for you. **Chef**
ribbits cook raw food in nearby smokers, **miner** ribbits head underground, and
**farmer** ribbits plant and tend your crops. Assign them from the ribbit bed and let
them gather and craft while you get on with everything else.

This is a ground-up port to Minecraft **26.2** running on **Fabric, Forge, and
NeoForge** from one shared codebase (Architectury), published as a community
continuation with the blessing of the original author, **Rogue_one12**.

## Loaders / versions

- Minecraft **26.2** — **Fabric, Forge, and NeoForge**.

## Porting & development docs

Written up in [`docs/`](docs) for anyone continuing or rebuilding this mod:

- [**Porting Playbook**](docs/Porting-Playbook.md) — a reusable, loader/version-agnostic guide to
  porting any Minecraft mod (decision tree, toolchain, cross-loader map, API-change cheatsheet,
  GUI/render cookbook, crash-guard patterns, validation, distribution, difficulty tiering).
- [**Useful Ribbits Port Case Study**](docs/Useful-Ribbits-Port-Case-Study.md) — the true 1:1 account
  of taking this mod from 1.20.1 Forge to 26.2 on all three loaders, including the hard bugs.
- [**Writing a 26.2 Mod From Scratch**](docs/Writing-A-26.2-Mod-From-Scratch.md) — a greenfield
  companion: clean multiloader layout, modern registration, the data-component + retained-render model.

## Credits

- Original **Useful Ribbits** mod by **rogue_one (Rogue_one12)** — concept, design, art, and code.
- Original project: <https://www.curseforge.com/minecraft/mc-mods/useful-ribbits>

Useful Ribbits builds on the **Ribbits** mod; the base ribbit and its assets remain
© Refresh Studios and Bonsai Studios (see [`LICENSE`](LICENSE)). Animations are powered
by **GeckoLib**, the only runtime dependency.

This 26.2 multi-loader build updates rogue_one's work to the current Minecraft version
with cross-loader registration and a dependency-free config screen, published with the
original author's blessing.

## License

- **Code** is licensed under the **GNU LGPL v3** (see [`LICENSE`](LICENSE)).
- **Assets** (textures, sounds, models, and the Ribbit itself) remain
  **© 2025 Refresh Studios and Bonsai Studios, All Rights Reserved**, per the
  original project's license notice, which is preserved in [`LICENSE`](LICENSE).

## Building

Requires JDK 25.

```
./gradlew build
```

Builds all loaders; the distributable jars land in `fabric/build/libs/`,
`forge/build/libs/`, and `neoforge/build/libs/` (named
`UsefulRibbits-26.2-<Loader>-<version>.jar`).
