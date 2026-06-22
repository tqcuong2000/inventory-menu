# Changelog

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

### Notes

Teleport actions still keep old `cost` field.

If a teleport action has both `cost` and `action_cost`, both costs are used.

- `action_cost` is checked and paid first.
- The teleport action then runs.
- The teleport action still checks and pays its own `cost`.

Using `action_cost` for cost control is recommended.

I am planning for version 2.0 which focuses on simplifying the menu JSON, spam prevention and more advanced features.
