package org.realityforge.saber;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLImageElement;
import java.util.Objects;
import javax.annotation.Nonnull;

final class Texture
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final HTMLImageElement _image;

  Texture( @Nonnull final String name )
  {
    _name = Objects.requireNonNull( name );
    _image = (HTMLImageElement) DomGlobal.document.createElement( "img" );
  }

  void startLoad( @Nonnull final HTMLImageElement.OnloadFn onImageLoaded )
  {
    _image.onload = onImageLoaded;
    _image.src = _name + ".png";
  }

  @Nonnull
  public String getName()
  {
    return _name;
  }

  @Nonnull
  public HTMLImageElement getImage()
  {
    return _image;
  }
}
