# Useful Ribbits — Multiloader 26.2 — 1.0.0

## 🐸 Job Ribbits, Now On 26.2

- 🌿 Ported **Useful Ribbits** from 1.20.1 Forge to **Minecraft 26.2** as a single Architectury codebase running on **Fabric, Forge, and NeoForge**.
- 🧩 Rebuilt registration, networking, menus, and the config screen cross-loader, and trimmed unused dependencies so **GeckoLib** is the only runtime requirement.

## 👨‍🍳 Chef, ⛏️ Miner, 🌾 Farmer

- 🍖 **Chef ribbits** now spread across *all* nearby smokers instead of crowding one, prefer smokers that already have fuel, and move to newly placed smokers.
- 🌱 **Farmer ribbits** replant from their held seeds after a crop is harvested or broken, instead of standing idle around the chest.
- 🔎 Improved job scanning so ribbits get stuck far less often when there is actually work to do.

## 🛏️ Ribbit Bed

- 💥 Fixed the ribbit bed crashing the game when opened.
- 👀 Fixed the bed's three job previews all rendering as the same ribbit on **Forge** — each slot now renders independently, so Fabric, Forge, and NeoForge all show chef, miner, and farmer distinctly.
- 🧾 The bed recipe accepts a toadstool from either this port or the official Ribbits mod.

## 📦 Ribbit Chest & 🥚 Spawn Eggs

- 📦 Added a proper **chest-opening animation** to the ribbit chest.
- 🥚 Fixed the chef / miner / farmer spawn eggs showing the missing-texture checkerboard — each egg now uses its correct tinted icon.

## ⚙️ Config & Polish

- 🔊 Added a config option for how often ribbits croak (ambient sound frequency).
- 🩹 Fixed a Fabric startup crash and a batch of smaller issues surfaced by a full code audit.

## 🧪 Tested

- ✅ Built and runtime-verified on **Fabric, Forge, and NeoForge** for **Minecraft 26.2**.
- ✅ Checked job behavior (cooking, farming), the ribbit bed previews, the chest animation, spawn eggs, the recipe, and the croak config.

## 💚 Credits

- 🐸 Original **Useful Ribbits** mod, concept, art, and design by **rogue_one (Rogue_one12)**.
- 🔗 Official CurseForge page: https://www.curseforge.com/minecraft/mc-mods/useful-ribbits
- 🛠️ Built on the **Ribbits** mod (base ribbit and assets © Refresh Studios & Bonsai Studios) and powered by **GeckoLib**.

Published as a community 26.2 continuation with the blessing of the original author.
