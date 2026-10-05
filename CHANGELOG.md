# Useful Ribbits 26.3 — 1.0.0

## 🐸 Minecraft 26.3 — Fabric, Forge & NeoForge

- Multi-loader release for **Minecraft 26.3** on **Fabric, Forge, and NeoForge**, built from one
  Architectury codebase.
- Job ribbits intact: **chef** (cooks raw food in nearby smokers), **miner**, and **farmer**
  (plants and tends crops), assigned from the ribbit bed. Only runtime dependency is **GeckoLib**
  (plus **Fabric API** on the Fabric build).

## 🔧 What the 26.2 → 26.3 port touched

Useful Ribbits has no custom worldgen/blocks/mixins, so the 26.3 delta was small:

- 🎨 **Rendering** — `PoseStack.mulPose(Quaternion)` → `rotate(Quaternion)` in the bed-preview renderer.
- 🧾 **Inventory API** — `Inventory.placeItemBackInInventory` now takes a `Prediction` argument; the
  server-side container dump on GUI close uses `SERVER_ONLY`.
- 🧾 **Datapack** — loot-table condition/function discriminators unified to `"type"`.
- 🧩 **Fabric metadata** — the Minecraft range was widened to `>=26.3 <26.4` (the old `~26.2` rejected 26.3).
- 🛠️ **Toolchain** — pinned LWJGL to 3.4.1 so the Architectury transformer packages cleanly on 26.3.

## 🧪 Tested

- ✅ Runtime-verified on **Fabric, Forge, and NeoForge** for Minecraft 26.3.

## 💚 Credits

- Original **Useful Ribbits** by **rogue_one (Rogue_one12)**. Builds on the **Ribbits** mod; the base
  ribbit and its assets remain © Refresh Studios & Bonsai Studios. Animations by **GeckoLib**.

Published as a community continuation with the original author's blessing.
