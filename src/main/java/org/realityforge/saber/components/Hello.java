package org.realityforge.saber.components;

import galdr.annotations.Component;

@Component
public class Hello
{
  public String message;

  public void set( final String message )
  {
    this.message = message;
  }
}
