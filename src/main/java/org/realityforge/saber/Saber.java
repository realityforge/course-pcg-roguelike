package org.realityforge.saber;

import com.google.gwt.core.client.EntryPoint;

public class Saber
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    final Game game = new Game( new Renderer( 800, 600 ) );
    game.init();
    game.start();
  }
}
