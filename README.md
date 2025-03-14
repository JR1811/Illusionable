# Illusionable

This is a Fabric Minecraft mod which introduces illusions and other effects.
Those features originally came from the [NeMuelch](https://github.com/JR1811/NeMuelch) mod.

## Features

### Illusion

An illusion is an Entity, which other entities normally can't perceive.

An Illusion's Target is an Entity which is allowed to perceive the Illusion. The perception restrictions are lifted for that specific entity.

#### Perception

Perceptions of entities include:
- rendering of entities
- rendering of entity fire (if it is burning)
- rendering of entity shadow
- rendering of entity `F3 + B` debug hitbox (disabled for server admins)
- step sound of entities
- Entity AI target selection

If installed, Illusions who are speaking with the [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) mod can only be heard by their targets.

To set up an Illusion Entity, use the `/illusion set <illusionEntity>` command. Setting the Illusion state to `false` will also remove all target entries automatically.
To add and remove Illusion targets to your Illusion Entity, use the `/illusion add <illusionEntity> <targetEntity>` and `/illusion remove <illusionEntity> <targetEntity>` commands.
Omitting the `targetEntity` from the remove command will remove all targets from an `illusionEntity`. Those specified entities in the command can also be multiple and even non-player entities, like Zombies.

Illusion entities will only target their specified targets and Entities which can't perceive the illusion entity won't target them either.

### Obfuscation

Obfuscation is a new StatusEffect. Entities which have this StatusEffect have their name tag scrambled. If a player has this StatusEffect, their names will also be scrambled in the PlayerList and their skin preview will be black.

<div style="text-align: center;">
<br>
<a href="https://modfest.net/bc25"><img
    src="https://github.com/JR1811/Boatism/blob/1.21.1/external/promo/fabric_supported.png?raw=true"
    alt="Supported on Fabric"
    width="200"
></a>
<a href="https://fabricmc.net/">
<img src="https://raw.githubusercontent.com/worldwidepixel/badges/642d312b71811b9d2696b562f735b07288844c71/bc25/made_for/cozy.svg"
    alt="ClanketCon25"
    width="200"
/></a>
<a href="https://github.com/JR1811/Illusionable/issues"><img
    src="https://github.com/JR1811/Boatism/blob/1.21.1/external/promo/badges/work_in_progress.png?raw=true"
    alt="Work in Progress"
    width="200"
></a>
</div>
