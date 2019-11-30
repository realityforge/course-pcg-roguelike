package org.realityforge.saber;

import galdr.Stage;
import galdr.annotations.GaldrApplication;
import galdr.annotations.GaldrStage;
import javax.annotation.Nonnull;
import org.realityforge.saber.components.CommandTarget;
import org.realityforge.saber.components.Hello;
import org.realityforge.saber.components.Player;
import org.realityforge.saber.components.Position;
import org.realityforge.saber.components.Sprite;
import org.realityforge.saber.systems.CommandSystem;
import org.realityforge.saber.systems.HelloWorldSystem;
import org.realityforge.saber.systems.InitializerSystem;
import org.realityforge.saber.systems.RenderSystem;

@GaldrApplication( components = { CommandTarget.class, Hello.class, Position.class, Sprite.class, Player.class } )
abstract class SaberApplication
{
  @Nonnull
  static SaberApplication create()
  {
    return new Galdr_SaberApplication();
  }

  @GaldrStage( { InitializerSystem.class, CommandSystem.class, HelloWorldSystem.class } )
  @Nonnull
  abstract Stage sim();

  @GaldrStage( RenderSystem.class )
  @Nonnull
  abstract Stage renderStage();

}
