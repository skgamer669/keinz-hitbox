# Keinz AS Enemy Hitbox

Client-side Fabric mod (Minecraft 1.21.1). Draws a colored line-box around
players not on your clan roster, on top of unmodified vanilla hitboxes.
No aim/attack/movement automation of any kind.

## Before you build

1. Install a JDK 21.
2. Open `gradle.properties` and double-check `yarn_mappings`, `loader_version`,
   and `fabric_version` against https://fabricmc.net/develop/ (select
   Minecraft 1.21.1). These move fast; the ones checked in here may be stale
   by the time you build.
3. `RangeUtil.java` reads the player's attack-range attribute for the
   red→white range indicator. The exact Yarn field name for that attribute
   can shift between mapping builds — if it fails to compile, open
   `EntityAttributes` in your IDE, find the "entity_interaction_range" entry,
   and swap the constant name in `RangeUtil.getAttackRange()`.
4. `./gradlew build` → JAR lands in `build/libs/`.

## How classification works

This does **not** use Minecraft scoreboard teams. On public servers you
don't control, the server may not expose a team for your clan at all, so
scoreboard matching isn't reliable. Instead:

- **Friend** = the player's UUID is in your local roster (`friendlyPlayers`
  in the config). No overlay — vanilla hitbox only.
- **Enemy** = everyone else. Red overlay, turning white when they enter your
  actual attack range (read from the game, not hardcoded).

## Config file

Location: `<your instance>/config/keinz-hitbox.json`, created on first launch.

```json
{
  "enabled": false,
  "friendlyPlayers": [
    { "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", "lastKnownName": "SomeClanmate" }
  ],
  "authorizedUsers": [
    "UUID_OF_SENIORRESPER",
    "UUID_OF_ARS_GAMER"
  ],
  "colors": { "enemy": 16729643, "inRange": 16777215 },
  "changeEnemyColorInRange": true
}
```

- **friendlyPlayers**: your clanmates. Edit by hand (paste real UUIDs — look
  them up with any Mojang-UUID lookup tool) or via `/keinz team add <name>`
  in-game (only works for players currently on your tab list).
- **authorizedUsers**: UUIDs allowed to run `/keinz team add|remove|clear`.
  No usernames are accepted here — usernames can change, UUIDs can't. This
  starts empty; nobody can modify the roster via commands until you add
  UUIDs yourself.
- **colors**: decimal RGB (e.g. red `0xFF3B30` = `16729643`).

## Commands

| Command | Who |
|---|---|
| `/keinz team list` | everyone |
| `/keinz team add <name>` | authorized UUIDs only |
| `/keinz team remove <name>` | authorized UUIDs only |
| `/keinz team clear` | authorized UUIDs only |
| `/keinz hitbox on` / `off` / `status` | everyone |

## What this deliberately does NOT do

No auto-attack, aim assist, reach change, target locking, or any input
automation. It only draws lines and reads config/commands. It also never
disables or replaces vanilla F3+B hitbox rendering — this overlay is
additive and only appears for players classified as enemy.

## One thing worth knowing

Many servers' rules restrict any client mod that visually flags player
identity/location beyond vanilla (this is the same category as "ESP"/name
overlays), even when — like here — it adds no combat advantage. Worth
checking the ruleset of whatever server you're running this on before use.

## Not done in this pass

- Multi-version support (1.16–1.21) — this build targets 1.21.1 only.
- In-game config GUI (commands + JSON file only, per the "mandatory" clause
  of the original spec).
- Scoreboard-team fallback — can be added if a server you use does expose
  team data.
