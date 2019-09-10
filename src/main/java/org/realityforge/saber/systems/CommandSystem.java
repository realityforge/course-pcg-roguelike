package org.realityforge.saber.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import jsinterop.base.Js;
import org.realityforge.saber.CommandType;
import org.realityforge.saber.Game;
import org.realityforge.saber.components.CommandTarget;
import org.realityforge.saber.components.Position;
import org.realityforge.saber.world.LevelPosition;

@All( CommandTarget.class )
public class CommandSystem
  extends IteratingSystem
{
  protected ComponentMapper<CommandTarget> commandTarget;
  protected ComponentMapper<Position> position;

  @Override
  protected void process( final int id )
  {
    final CommandTarget t = commandTarget.get( id );
    assert null != t;
    if ( null != t.command )
    {
      final Position pc = position.get( id );
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
