package org.realityforge.saber.systems;

import galdr.ComponentManager;
import galdr.World;
import galdr.annotations.ComponentManagerRef;
import galdr.annotations.GaldrSubSystem;
import galdr.annotations.Processor;
import galdr.annotations.WorldRef;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import org.realityforge.saber.Game;
import org.realityforge.saber.components.CommandTarget;
import org.realityforge.saber.components.Hello;
import org.realityforge.saber.components.Player;
import org.realityforge.saber.components.Position;
import org.realityforge.saber.components.Sprite;
import org.realityforge.saber.world.Level;
import org.realityforge.saber.world.LevelPosition;

@GaldrSubSystem
public abstract class InitializerSystem
{
  private boolean _initialized;

  @WorldRef
  @Nonnull
  abstract World world();

  @Processor
  final void initializeWorld()
  {
    if ( !_initialized )
    {
      hello().get( world().createEntity( Hello.class ) ).message = "\n\rHello world!\n\r";

      final int playerEntityId =
        world().createEntity( Position.class, Sprite.class, CommandTarget.class, Player.class );

      final LevelPosition position = position().get( playerEntityId ).position;
      final Game game = Game.getGame();
      final Level level = game.getLevel();
      position.setColumn( level.getColumnCount() / 2 );
      position.setRow( level.getRowCount() / 2 );
      sprite().get( playerEntityId ).texture =
        game.getTextureManager().getImageByName( "resources/players/warrior/spr_warrior_idle_down" );
      _initialized = true;
    }
  }

  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<Hello> hello();

  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<Position> position();

  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<Sprite> sprite();
}
