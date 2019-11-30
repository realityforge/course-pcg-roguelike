package org.realityforge.saber;

import com.google.gwt.core.client.EntryPoint;
import org.realityforge.saber.game.LevelData;

public class Saber
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    final Game game = new Game( new Renderer( 950, 950 ) );
    game.init();
    game.start( LevelData.LEVEL1_DATA );
  }
}
