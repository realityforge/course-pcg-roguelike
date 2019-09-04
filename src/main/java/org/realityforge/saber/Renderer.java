package org.realityforge.saber;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import javax.annotation.Nonnull;
import jsinterop.base.Js;

public final class Renderer
{
  @Nonnull
  private final HTMLCanvasElement _canvas;
  @Nonnull
  private final CanvasRenderingContext2D _context;

  public Renderer( final int width, final int height )
  {
    _canvas = (HTMLCanvasElement) DomGlobal.document.createElement( "canvas" );
    _canvas.width = width;
    _canvas.height = height;
    DomGlobal.document.documentElement.appendChild( _canvas );
    _context = Js.uncheckedCast( _canvas.getContext( "2d" ) );
  }

  @Nonnull
  public HTMLCanvasElement getCanvas()
  {
    return _canvas;
  }

  @Nonnull
  public CanvasRenderingContext2D getContext()
  {
    return _context;
  }
}
