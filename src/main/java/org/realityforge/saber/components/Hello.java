package org.realityforge.saber.components;

import com.artemis.Component;

public class Hello
  extends Component
{
  public String message;

  public void set( String message )
  {
    this.message = message;
  }
}