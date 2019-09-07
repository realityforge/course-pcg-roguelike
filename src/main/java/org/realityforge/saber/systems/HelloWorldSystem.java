package org.realityforge.saber.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import elemental2.dom.DomGlobal;
import org.realityforge.saber.components.Hello;

@All( Hello.class )
public class HelloWorldSystem
  extends IteratingSystem
{
  protected ComponentMapper<Hello> mHello;

  @Override
  protected void process( int id )
  {
    DomGlobal.console.log( mHello.get( id ).message );
  }
}