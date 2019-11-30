package org.realityforge.saber.systems;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import galdr.ComponentManager;
import galdr.annotations.ComponentManagerRef;
import galdr.annotations.EntityProcessor;
import galdr.annotations.GaldrSubSystem;
import galdr.annotations.Processor;
import javax.annotation.Nonnull;
import org.realityforge.saber.Game;
import org.realityforge.saber.Renderer;
import org.realityforge.saber.Texture;
import org.realityforge.saber.components.Position;
import org.realityforge.saber.components.Sprite;
import org.realityforge.saber.world.Level;
import org.realityforge.saber.world.Tile;
import org.realityforge.saber.world.TileType;

@GaldrSubSystem
public abstract class RenderSystem
{
  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<Sprite> sprite();

  @ComponentManagerRef
  @Nonnull
  abstract ComponentManager<Position> position();

  @Processor
  void renderWorld()
  {
    clearBackground();

    drawWorld();
  }

  @EntityProcessor( all = { Position.class, Sprite.class } )
  void renderSprite( final int id )
  {
    final Position position = position().get( id );
    final Texture texture = sprite().get( id ).texture;

    final Game game = Game.getGame();
    final Level level = game.getLevel();
    final Renderer renderer = game.getRenderer();

    final HTMLCanvasElement canvas = renderer.getCanvas();
    final int columnWidth = canvas.width / level.getColumnCount();
    final int rowHeight = canvas.height / level.getRowCount();

    assert null != texture;
    final HTMLImageElement image = texture.getImage();
    renderer.getContext()
      .drawImage( image, position.position.getColumn() * columnWidth, position.position.getRow() * rowHeight );
  }

  private void clearBackground()
  {
    final Game game = Game.getGame();
    final Renderer renderer = game.getRenderer();
    final HTMLCanvasElement canvas = renderer.getCanvas();
    final CanvasRenderingContext2D context = renderer.getContext();
    context.fillStyle = CanvasRenderingContext2D.FillStyleUnionType.of( "black" );
    context.fillRect( 0, 0, canvas.width, canvas.height );
  }

  private void drawWorld()
  {
    final Game game = Game.getGame();
    final Level level = game.getLevel();
    final Renderer renderer = game.getRenderer();

    int index = 0;
    final int rowCount = level.getRowCount();
    final int columnCount = level.getColumnCount();
    final Tile[] tiles = level.getTiles();
    for ( int i = 0; i < rowCount; i++ )
    {
      for ( int j = 0; j < columnCount; j++ )
      {
        final Tile tile = tiles[ index++ ];
        final TileType tileType = tile.getTileType();
        final Texture texture = tileType.getTexture();
        if ( null != texture )
        {
          renderer.getContext().drawImage( texture.getImage(), tile.getTopLeftX(), tile.getTopLeftY() );
        }
      }
    }
  }
}
