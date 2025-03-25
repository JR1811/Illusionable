# Illusionable

**Illusionable** is a Fabric Minecraft mod that introduces illusions and other perception-altering effects, allowing players to manipulate visibility and awareness in unique ways.
By leveraging the illusion system, entities can become selectively invisible, making themselves imperceptible to all but chosen targets. This allows for creative mechanics such as invisible scouting, selective interactions, and even strategic deception in multiplayer environments.

This mod was originally inspired and continued by features which were implemented in the [NeMuelch](https://github.com/JR1811/NeMuelch) mod.

## Features

### Illusion

Illusions are entities that are normally imperceptible to others. However, entities can be designated as a target, allowing them to perceive the illusion while others remain unaware of its existence.

#### Perception Details

Entities that cannot perceive an illusion will not experience the following:

- Rendering of the illusion entity
- Debug hitbox (F3 + B) visibility (restricted for server admins)
- Step sounds of the illusion entity
- Selection of the illusion entity as a target by AI-driven entities (e.g. zombies won't chase players which are listed as illusions)

If the [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) mod is installed, only the designated targets will be able to hear an illusion entity speaking.

#### How to work with Illusions

- Create an Illusion: `/illusion set <illusionEntity> true`
- Remove an Illusion: `/illusion set <illusionEntity> false` *(also removes all target entries automatically)*
- Add a Target: `/illusion add <illusionEntity> <targetEntity>`
- Remove a Target: `/illusion remove <illusionEntity> <targetEntity>`
- Remove All Targets: `/illusion remove <illusionEntity>`

Multiple entities (including non-player entities like Zombies) can be specified as illusions and targets. Illusion entities will only be visible and interactable to their assigned targets, while remaining hidden from all others.

To set up an Illusion Entity, use the `/illusion set <illusionEntity>` command. Setting the Illusion state to `false` will also remove all target entries automatically.
To add and remove Illusion targets to your Illusion Entity, use the `/illusion add <illusionEntity> <targetEntity>` and `/illusion remove <illusionEntity> <targetEntity>` commands.
Omitting the `targetEntity` from the remove command will remove all targets from an `illusionEntity`. any specified entities in the commands can also be multiple and even non-player entities, like Zombies. So, for example, you can turn a horde of mobs into illusions or illusion targets.

Illusion entities will only target their specified illusion targets, and Entities which can't perceive the illusion entity (are not listed as illusion targets) won't target them either.

### Obfuscation Details
Obfuscation is a new status effect that scrambles an entity's name tag. When applied to a player, it also:

- Scrambles their name in the Player List
- Displays a blacked-out skin in the Player List

<div style="text-align: center;">
<br>
<a href="https://fabricmc.net/"><img
    src="https://raw.githubusercontent.com/fabricated-atelier/.github/a021bde84febcb68adc69fc7ae60114e8c0902db/assets/badges/bc25/supported_on_fabric_loader.svg"
    alt="Supported on Fabric"
    width="200"
></a>
<a href="https://modfest.net/bc25">
<img src="https://raw.githubusercontent.com/fabricated-atelier/.github/f026478715176aeb6a334f1c21765031d9b6c3f9/assets/badges/bc25/created_for_bc25.svg"
    alt="BlanketCon25"
    width="200"
/></a>
<a href="https://github.com/JR1811/Illusionable/issues"><img
    src="https://raw.githubusercontent.com/fabricated-atelier/.github/f026478715176aeb6a334f1c21765031d9b6c3f9/assets/badges/bc25/work_in_progress.svg"
    alt="Work in Progress"
    width="200"
></a>
</div>
