package com.artemis;

/**
 * Provides a blueprint for new entities, offering greatly
 * improved insertion performance for systems.
 * </p>
 * Instance entities using {@link World#create(Archetype)}
 *
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public final class Archetype
{
  final EntityTransmuter.TransmuteOperation transmuter;
  final int compositionId;

  /**
   * @param transmuter    Desired composition of derived components.
   * @param compositionId uniquely identifies component composition.
   */
  Archetype( final EntityTransmuter.TransmuteOperation transmuter, final int compositionId )
  {
    this.transmuter = transmuter;
    this.compositionId = compositionId;
  }
}
