package org.realityforge.saber;

import com.artemis.EntityEdit;
import com.artemis.World;
import com.google.gwt.core.client.EntryPoint;
import org.realityforge.saber.components.Hello;
import org.realityforge.saber.components.Position;
import org.realityforge.saber.game.LevelData;
import org.realityforge.saber.world.LevelPosition;

public class Saber
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    final Game game = new Game( new Renderer( 950, 950 ) );
    game.init();
    game.start( LevelData.LEVEL1_DATA );

    final World world = game.getWorld();

    // Create a hello world entity
    world.edit( world.create() ).create( Hello.class ).message = "\n\rHello world!\n\r";
  }
}
