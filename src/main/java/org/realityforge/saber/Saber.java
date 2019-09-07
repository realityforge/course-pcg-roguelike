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

    // 1. Register any plugins, setup the world.
    WorldConfiguration setup = new WorldConfigurationBuilder()
      .with( new HelloWorldSystem() )
      .build();

    // 2. Create the world.
    World world = new World( setup );

    // 3. Create entity. You can do it here or inside systems.
    int entityId = world.create();
    world.edit( entityId ).create( Hello.class ).message = "\n\rHello world!\n\r";

    // 4. Run the world. HelloWorldSystem should print the hello world message.
    world.process();
  }
}
