package org.realityforge.saber;

import elemental2.dom.Event;
import elemental2.dom.HTMLImageElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TextureManager
{
  @Nonnull
  private final Map<String, Texture> _textures = new HashMap<>();
  @Nonnull
  private final Runnable _onReady;
  private int _loadedImageCount;

  public TextureManager( @Nonnull final Runnable onReady )
  {
    _onReady = Objects.requireNonNull( onReady );
  }

  @Nonnull
  Texture registerTexture( @Nonnull final String name )
  {
    final Texture texture = new Texture( name );
    _textures.put( Objects.requireNonNull( name ), texture );
    return texture;
  }

  void startTextureLoad()
  {
    final HTMLImageElement.OnloadFn onImageLoaded = this::onImageLoaded;
    for ( final Texture texture : _textures.values() )
    {
      texture.startLoad( onImageLoaded );
    }
  }

  @Nullable
  private Object onImageLoaded( @Nonnull final Event event )
  {
    _loadedImageCount++;
    if ( _textures.size() == _loadedImageCount )
    {
      _onReady.run();
    }
    return null;
  }

  @Nonnull
  Texture getImageByName( @Nonnull final String name )
  {
    return Objects.requireNonNull( _textures.get( name ) );
  }
}
