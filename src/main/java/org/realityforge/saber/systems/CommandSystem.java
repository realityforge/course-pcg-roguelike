package org.realityforge.saber.systems;

import galdr.ComponentManager;
import galdr.annotations.ComponentManagerRef;
import galdr.annotations.EntityProcessor;
import galdr.annotations.GaldrSubSystem;
import javax.annotation.Nonnull;
import org.realityforge.saber.CommandType;
import org.realityforge.saber.Game;
import org.realityforge.saber.components.CommandTarget;
import org.realityforge.saber.components.Player;
import org.realityforge.saber.components.Position;
import org.realityforge.saber.world.LevelPosition;

@GaldrSubSystem
public abstract class CommandSystem
{
  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<CommandTarget> commandTarget();

  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<Position> position();

  @EntityProcessor( all = Player.class )
  void attachCommandToPlayer( final int id )
  {
    commandTarget().get( id ).command = Game.getGame().getCommandType();
  }

  @EntityProcessor( all = CommandTarget.class )
  void processCommands( final int id )
  {
    final CommandTarget t = commandTarget().get( id );
    if ( null != t.command )
    {
      final Position pc = position().find( id );
      if ( null != pc )
      {
        final LevelPosition position = pc.position;
        if ( CommandType.MoveForward == t.command )
        {
          position.setRow( Math.max( 0, position.getRow() - 1 ) );
        }
        else if ( CommandType.MoveBackward == t.command )
        {
          position.setRow( Math.min( Game.getGame().getLevel().getRowCount() - 1, position.getRow() + 1 ) );
        }
        else if ( CommandType.TurnLeft == t.command )
        {
          position.setColumn( Math.max( 0, position.getColumn() - 1 ) );
        }
        else if ( CommandType.TurnRight == t.command )
        {
          position.setColumn( Math.min( Game.getGame().getLevel().getColumnCount() - 1, position.getColumn() + 1 ) );
        }
      }
      t.command = null;
    }
  }
}
