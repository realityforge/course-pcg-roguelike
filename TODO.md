# TODO

This document is essentially a list of shorthand notes describing work yet to completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

* Step 1 is to re-implement the starter kit using web technologies.
    - Use Canvas2D to render world
    - Consider using [Ashley](https://github.com/libgdx/ashley) or another ECS to implement entities.

## Reimplementation Notes

* TileType should be data driven and loaded from a file.

* Artemis should be rewritten to use annotation processor based code generation and that way reflection
  can be ditched.

## Notes

* Consider using a deterministic world generated from an initial seed. This means all decisions must occur
  in fixed order and drawing from the same RNG. Have a turn based game with one action per turn.
  
  This means the entire world and gameplay can be recreated by recording the seed and the sequence of
  commands.
