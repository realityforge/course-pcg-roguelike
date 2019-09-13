# TODO

This document is essentially a list of shorthand notes describing work yet to completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

* Step 1 is to re-implement the starter kit using web technologies.
    - Use Canvas2D to render world
    - Consider using [Ashley](https://github.com/libgdx/ashley) or another ECS to implement entities.

* Add commands system that maps keys to commands. `KeyUp` -> `MoveForward`

* Add binding layer that allows binding keys to commands (think quakes console).

* Consider having an `InputUpdateSystem` that takes a sequence of keys

## Reimplementation Notes

* TileType should be data driven and loaded from a file.

* Artemis should be rewritten to use annotation processor based code generation and that way reflection
  can be ditched.

## Notes

* Consider using a deterministic world generated from an initial seed. This means all decisions must occur
  in fixed order and drawing from the same RNG. Have a turn based game with one action per turn.

  This means the entire world and gameplay can be recreated by recording the seed and the sequence of
  commands.

## PixiJS

Seems like the 2D framework of choice.
- https://github.com/kittykatattack/learningPixi
- https://github.com/klaun76/gwt-pixi
- https://github.com/klaun76/gwt-pixi-demo
- https://pixijs.io/examples/#/demos-advanced/star-warp.js
- https://www.pixijs.com/tutorials
- https://www.amazon.co.uk/Learn-Pixi-js-Interactive-Graphics/dp/1484210956

## Games to look at for ideas on ECS

* [alone-rl](https://github.com/fabio-t/alone-rl.git) - a roguelike using `artemis-odb` 
* [Ore Infinium](https://github.com/sreich/ore-infinium) - "Open Source multiplayer Terraria-inspired Sci-fi game, focused on technology, devices and researching". Uses `artemis-odb`, `protobuf` and `KryoNet` (networking library)
* [underkeep](https://github.com/DaanVanYperen/odb-underkeep) - a Ludum Dare game using `artemis-odb` 