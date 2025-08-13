![Inventory Menu](https://cdn.modrinth.com/data/cached_images/9e0762c520df14b7803ae5d6800f72557284ee4a_0.webp)

## **About this mod**
Welcome to Inventory Menu, a server-side mod that allows players and datapack makers to create an interactive menu by defining them within a JSON file.

This mod adds a folder to the datapack structure called "menu." If you have worked with datapacks before, specifically with predicates or advancements, this mod works in a similar way!

Written completely for the server side, this means other players can use its features without installing the mod themselves—it only needs to be installed on the server.

## Features

- Create highly customizable menus.
- Powerful placeholder system to display information based on the context.
- A variety of actions that allow users to choose what happens when a player clicks on an item.
- Built-in teleport system with safe-checking and cost management.

## Installation

- Required on server.
- No need to install on client
- Required a datapack install on server to function

## Usage

Before you start, it is recommended to have a basic understanding of datapacks and how they work. If you are a datapack maker and have experience, then let's get started. If not, you can visit [this page](https://minecraft.wiki/w/Tutorial:Creating_a_data_pack) to learn more about them.

You can visit [this wiki](https://github.com/tqcuong2000/inventory-menu/wiki) to learn detail about this mod.

### 1. Setup the datapack.


After installing this mod, you need a place to store your menus, and that's when datapacks come in handy. Just like other vanilla ways to create custom advancements or predicates, creating a menu works in the same way.

You can get inside the datapack structure folder using the following path:

- For server: `<server_dicrectory>/world/datapacks/<your_datapack>/data/<namespace>/`
- For client: `<minecraft_folder>/saves/<your_world>/datapacks/<your_datapack>/data/<namespace>/`

In your datapack namespace folder (the folder that includes the functions and advancements folders), create a new folder and name it "menu." It serves as a root folder for your menu files. Inside the folder you just created, create a file named "my_menu.json," then paste this into it:

```json
{"name": "My menu", "items": [{"type": "item", "slot": [1,1], "item": {"id": "minecraft:diamond"}}]}
```
First, make sure your datapack is enabled. Use the `/reload` command, and if it loaded correctly, the menu should appear in the suggestions after you type `/menu`.

![My menu](https://cdn.modrinth.com/data/cached_images/97ec91323344188defb9c128ce6bc874017d1f1b.png)

If the menu appears like this, you should be ready to create and modify your menu!

### 2. Display a variety of information through placeholders.


Placeholders are temporary strings used to represent information based on context. For example, %name% will show the menu opener's name. There are [several of them](https://github.com/tqcuong2000/inventory-menu/wiki/Placeholders), and you can use multiple in one line. The example below shows how to assign them; they can be put in an item's name, its lore, and even in some actions.


```json
{"name": "My menu", 
  "items": [
    {"type": "item", 
      "slot": [1,1], 
      "item": {"id": "minecraft:diamond", "components": {"custom_name": "§b%name%'s diamond!"}}
    }
  ]}
```

### 3. Add functionality


Choose what happens when a player clicks on an item by defining an action for it. There are several actions, and each is designed for a specific purpose. The most versatile action is the command action, which executes commands upon clicking.

Say "Hello player <name>" when clicked.

```json
{"name": "My menu",
  "items": [
    {"type": "item",
      "slot": [1,1],
      "item": {"id": "minecraft:diamond", "components": {"custom_name": "§b%name%'s diamond!"}},
      "action": {"type": "command", "command": "say Hello player %name%"}
    }
  ]}
```
Give night vision and glowing effect to player.


```json
{"name": "My menu",
  "items": [
    {"type": "item",
      "slot": [1,1],
      "item": {"id": "minecraft:diamond", "components": {"custom_name": "§b%name%'s diamond!"}},
      "action": {"type": "command", "command": "say Hello player %name%"}
    },
    {
      "type": "item",
      "slot": [1,2],
      "item": {"id": "minecraft:ender_eye",
        "components": {
          "custom_name": "§aGive effects",
          "lore": ["§aNight vision: §730s", "§eGlowing: §730s"]
        }
      },
      "action": {
        "type":"command",
        "command": ["effect give %name% night_vision 30 0","effect give %name% glowing 30 0"]}
    }
  ]}
```
## Planned features

- New item types: Claimable and Defined
- New action: Effect
- Support for older version

## Notes

- Hopefully, this mod is powerful enough for you to make any menu you need. If you encounter a bug or have a feature suggestion, please contact me via Discord or GitHub.
- GitHub homepage and wiki are under construction. I will try to finish them as quickly as possible!







