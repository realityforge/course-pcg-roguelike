package org.realityforge.saber.systems;

import elemental2.dom.DomGlobal;
import galdr.ComponentManager;
import galdr.annotations.ComponentManagerRef;
import galdr.annotations.EntityProcessor;
import galdr.annotations.GaldrSubSystem;
import javax.annotation.Nonnull;
import org.realityforge.saber.components.Hello;

@GaldrSubSystem
public abstract class HelloWorldSystem
{
  @EntityProcessor( all = Hello.class )
  final void processHello( final int id )
  {
    DomGlobal.console.log( hello().get( id ).message );
  }

  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<Hello> hello();
}
