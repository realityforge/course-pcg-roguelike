package org.realityforge.saber.systems;

import com.artemis.utils.BitVector;
import javax.annotation.Nonnull;
import org.realityforge.saber.Renderer;
import org.realityforge.saber.ecs.PassiveSystem;
import org.realityforge.saber.screens.AbstractScreen;
import org.realityforge.saber.screens.Screen;
import org.realityforge.saber.screens.StartScreen;

public final class ScreenSystem
  extends PassiveSystem
  implements Screen
{
  private Screen selected;

  @Override
  protected void initialize()
  {
    this.selected = world.getSystem( StartScreen.class );
  }

  @Override
  public void handleKeys( @Nonnull final BitVector keys )
  {
    selected.handleKeys( keys );
  }

  @Override
  public void display( @Nonnull final Renderer terminal )
  {
    selected.display( terminal );
  }

  public void select( final Class<? extends AbstractScreen> next )
  {
    this.selected = world.getSystem( next );
  }
}