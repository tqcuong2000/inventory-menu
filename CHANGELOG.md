# Changelog

## Version 1.2.1

### Highlights

- **Background Filler Items:** Use the new `fill` option to automatically populate empty slots with background items.
- **Menu Close Control:** Choose when a menu should close after clicking an item (`always`, `never`, `on_success`, or `on_failure`).
- **Friendlier Item Checks:** Failing an item's requirements now displays a message without kicking the player out of the menu.
- **Safe Player Teleports:** Teleporting to other players now checks for dangerous spots (lava, void, suffocation) first.
- **Anti-Exploit & Anti-Spam:** Fixed click spamming, shift-click glitches, and multi-cost bypass exploits.
- **Stability & Performance:** Fixed server crashes from circular items or typos, resolved memory leaks on reload, and smoothed out menu lag.

### New Features

#### Empty Slot Filler (`fill`)

You can now define an optional `fill` item in menu layouts. Empty slots that are not assigned to any item will be filled automatically with this item stack. Placeholders in custom names and lore are fully resolved.

```json
{
  "name": "My Menu",
  "rows": 3,
  "fill": {
    "id": "minecraft:gray_stained_glass_pane",
    "components": {
      "minecraft:custom_name": " "
    }
  },
  "items": []
}
```

#### Menu Close Policy (`close_policy`)

You can now specify whether and when clicking an item should close the menu using `close_policy` (or `close_container`):

- `"default"`: Default menu closing behavior.
- `"on_success"` (or `true`): Closes the menu only when all actions succeed.
- `"on_failure"`: Closes the menu if conditions or action costs fail.
- `"always"`: Always closes the menu on click.
- `"never"` (or `false`): Keeps the menu open regardless of success or failure.

```json
{
  "slot": [1, 5],
  "close_policy": "on_success",
  "item": {
    "type": "custom",
    "id": "minecraft:compass"
  }
}
```

#### Non-Closing Item Condition Checks

- If a player fails an item predicate check (`condition`), they receive the failure message without having the entire menu force-closed. Menu-level conditions still close unauthorized menus.

#### Safe Player Teleportation

- Teleporting to another player now checks if the destination position is safe (`safe_check`), preventing teleportation into void, lava, or suffocation blocks.

### Fixes & Improvements

#### Exploits & Spam Protection

- **Click Spam & Autoclicker Prevention:** Added a 0.1-second delay between menu clicks to stop players from spamming buttons or using autoclickers.
- **Fair Cost Charging on Multiple Actions:** If a button runs multiple actions with costs, the total combined cost (items, XP, or scoreboard points) is checked before taking anything, preventing players from cheating multi-step actions.
- **Fixed Shift-Click Glitches:** Blocked shift-clicking inside menus to prevent item duplication bugs and inventory glitches.
- **No Negative Scoreboards:** Scoreboard points can no longer drop below zero when paying for an action.

#### Crash Fixes & Safety

- **Infinite Loop Protection:** If custom items accidentally reference each other in a loop, the mod now safely catches the error instead of crashing the server.
- **Safer Identifier Handling:** Typos or invalid item/menu names in configs, entity tags, or item data will now log a warning instead of crashing the game.
- **Safer Teleports:** Teleporting to offline players, unloaded areas, or missing worlds now shows a clean error message instead of causing a crash.
- **Advancement Display Safety:** Viewing advancements in menus will no longer accidentally alter original game advancement icons.

#### Performance & Memory

- **No More Server Reload Leaks:** Fixed memory leaks where reloading the server would leave behind old event handlers and use up RAM.
- **Cleaner Player Data:** Menus now properly clean up player data as soon as a menu is closed or a player disconnects.
- **Reduced Teleport Lag:** Safety checks before teleporting no longer force the server to load distant chunks instantly, eliminating lag spikes.
- **Faster Menus & Placeholders:** Menu items and placeholders now load and refresh faster and more efficiently.
- **Auto-Created Config Folder:** The `config/` directory is now created automatically if it doesn't already exist.

## Version 1.2.0

### New features

Add action costs. You can now make a menu action charge a player before it runs.

Use it to charge a player for running an action.

Existing menus still work. If an action does not have an action cost, it behaves the same as before.

If one menu item has more than one action, all `action_cost` checks must pass first. No cost is taken and no action runs unless every action cost can be paid.

- Added `action_cost` to actions.
- Added 3 action cost types: XP, item, and scoreboard.
- Added support for more than one cost on the same action.
- Added `fail_message` for action costs.
- Added `%action_cost%` item placeholder.
- Added one-jar support for Minecraft 26.1, 26.1.1, and 26.1.2.
- Added support for Minecraft 26.2.

### XP Cost Example

This action costs 3 XP levels before sending the message.

```json
{
  "type": "message",
  "message": {
    "text": "You paid 3 levels."
  },
  "action_cost": {
    "type": "minecraft:xp",
    "xp": 3,
    "is_level": true
  }
}
```

Use `"is_level": false` if you want to charge XP points instead of levels.

### Item Cost Example

This action costs 2 emeralds.

```json
{
  "type": "message",
  "message": {
    "text": "You paid 2 emeralds."
  },
  "action_cost": {
    "type": "minecraft:item",
    "item": {
      "id": "minecraft:emerald",
      "count": 2
    }
  }
}
```

The item must match the item and its data. For example, a named item only matches the same named item.

### Score Cost Example

This action costs 5 points from the `coins` scoreboard objective.

```json
{
  "type": "message",
  "message": {
    "text": "You spent 5 coins."
  },
  "action_cost": {
    "type": "minecraft:score",
    "objective": "coins",
    "amount": 5
  }
}
```

The scoreboard objective must exist, and the player must have enough score.

### Multiple Costs On One Action

You can write one cost or a list of costs.

This action costs 2 XP levels and 5 coins.

```json
{
  "type": "message",
  "message": {
    "text": "You paid both costs."
  },
  "action_cost": [
    {
      "type": "minecraft:xp",
      "xp": 2,
      "is_level": true
    },
    {
      "type": "minecraft:score",
      "objective": "coins",
      "amount": 5
    }
  ]
}
```

All costs must be paid before the action runs.

### Fail Message Example

Add `fail_message` inside a cost if you want to tell the player why the action did not run.

```json
"action_cost": {
  "type": "minecraft:xp",
  "xp": 3,
  "is_level": true,
  "fail_message": {
    "message": {
      "text": "You need 3 XP levels.",
      "color": "red"
    }
  }
}
```

If more than one action has a fail message, each non-empty fail message is sent.

### Action Cost Placeholder

Use `%action_cost%` in item names or lore to show the cost.

```text
Cost: %action_cost%
```

Use `%action_cost:<bullet>%` to show one cost per line.

```text
%action_cost:-%
```

This can show:

```text
- 2 levels
- 5 coins
```

### Multiple Actions Example

In this example, the player must have both 1 diamond and 2 XP levels.

If either cost cannot be paid, nothing runs and nothing is charged.

```json
"action": [
  {
    "type": "message",
    "message": {
      "text": "Diamond paid."
    },
    "action_cost": {
      "type": "minecraft:item",
      "item": {
        "id": "minecraft:diamond",
        "count": 1
      }
    }
  },
  {
    "type": "message",
    "message": {
      "text": "XP paid."
    },
    "action_cost": {
      "type": "minecraft:xp",
      "xp": 2,
      "is_level": true
    }
  }
]
```
