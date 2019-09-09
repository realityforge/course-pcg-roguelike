package org.realityforge.saber;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.google.gwt.core.client.EntryPoint;
import org.realityforge.saber.components.Hello;
import org.realityforge.saber.game.LevelData;
import org.realityforge.saber.systems.HelloWorldSystem;

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
    final int entityId = world.create();
    world.edit( entityId ).create( Hello.class ).message = "\n\rHello world!\n\r";
  }
}
