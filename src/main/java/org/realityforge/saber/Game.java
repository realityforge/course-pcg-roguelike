package org.realityforge.saber;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.saber.game.Tiles;
import org.realityforge.saber.world.Level;
import org.realityforge.saber.world.Tile;
import org.realityforge.saber.world.TileType;
import org.realityforge.saber.world.TileTypeManager;

public final class Game
{
  private static final int FRAMES_PER_SECOND = 30;
  private static final int MILLIS_PER_SECOND = 1000;
  private static final int FRAME_DELAY = MILLIS_PER_SECOND / FRAMES_PER_SECOND;
  @Nonnull
  private final Renderer _renderer;
  @Nonnull
  private final TileTypeManager _tileTypeManager = new TileTypeManager();
  @Nonnull
  private final TextureManager _textureManager = new TextureManager( this::texturesLoaded );
  private Level _level;
  private boolean _texturesLoaded;
  private double _cellWidth;
  private double _cellHeight;

  public Game( @Nonnull final Renderer renderer )
  {
    _renderer = Objects.requireNonNull( renderer );
  }

  public void init()
  {
    registerTile( "resources/tiles/spr_tile_door_locked", Tiles.WALL_DOOR_LOCKED );
    registerTile( "resources/tiles/spr_tile_door_unlocked", Tiles.WALL_DOOR_UNLOCKED, TileType.Flags.SOLID );
    registerTile( "resources/tiles/spr_tile_floor", Tiles.FLOOR, TileType.Flags.SOLID | TileType.Flags.FLOOR );
    registerTile( "resources/tiles/spr_tile_floor_alt", Tiles.FLOOR_ALT, TileType.Flags.SOLID | TileType.Flags.FLOOR );
    registerTile( "resources/tiles/spr_tile_wall_bottom_end", Tiles.WALL_BOTTOM_END );
    registerTile( "resources/tiles/spr_tile_wall_bottom_left", Tiles.WALL_BOTTOM_LEFT );
    registerTile( "resources/tiles/spr_tile_wall_bottom_right", Tiles.WALL_BOTTOM_RIGHT );
    registerTile( "resources/tiles/spr_tile_wall_bottom_t", Tiles.WALL_BOTTOM_T );
    registerTile( "resources/tiles/spr_tile_wall_entrance", Tiles.WALL_ENTRANCE );
    registerTile( "resources/tiles/spr_tile_wall_intersection", Tiles.WALL_INTERSECTION );
    registerTile( "resources/tiles/spr_tile_wall_side", Tiles.WALL_SIDE );
    registerTile( "resources/tiles/spr_tile_wall_side_left_end", Tiles.WALL_SIDE_LEFT_END );
    registerTile( "resources/tiles/spr_tile_wall_side_left_t", Tiles.WALL_SIDE_LEFT_T );
    registerTile( "resources/tiles/spr_tile_wall_side_right_end", Tiles.WALL_SIDE_RIGHT_END );
    registerTile( "resources/tiles/spr_tile_wall_side_right_t", Tiles.WALL_SIDE_RIGHT_T );
    registerTile( "resources/tiles/spr_tile_wall_single", Tiles.WALL_SINGLE );
    registerTile( "resources/tiles/spr_tile_wall_top", Tiles.WALL_TOP );
    registerTile( "resources/tiles/spr_tile_wall_top_end", Tiles.WALL_TOP_END );
    registerTile( "resources/tiles/spr_tile_wall_top_left", Tiles.WALL_TOP_LEFT );
    registerTile( "resources/tiles/spr_tile_wall_top_right", Tiles.WALL_TOP_RIGHT );
    registerTile( "resources/tiles/spr_tile_wall_top_t", Tiles.WALL_TOP_T );
    final TileType emptyTileType = _tileTypeManager.registerEmptyTileType( Tiles.EMPTY, 0 );

    _level = new Level( 19, 19, emptyTileType );
    final HTMLCanvasElement canvas = _renderer.getCanvas();
    _cellWidth = canvas.width / ( _level.getColumnCount() * 1D );
    _cellHeight = canvas.height / ( _level.getRowCount() * 1D );
  }

  public void start( @Nonnull final String levelData )
  {
    _textureManager.startTextureLoad();
    loadFromData( levelData );
    runFrame();
    DomGlobal.setInterval( v -> runFrame(), FRAME_DELAY );
  }

  private void runFrame()
  {
    /*
    if ( _simulationActive )
    {
      simulateWorld();
    }
    */
    if ( _texturesLoaded )
    {
      renderWorld();
    }
  }

  private void renderWorld()
  {
    clearBackground();

    drawWorld();
  }

  private void clearBackground()
  {
    final HTMLCanvasElement canvas = _renderer.getCanvas();
    final CanvasRenderingContext2D context = _renderer.getContext();
    context.fillStyle = CanvasRenderingContext2D.FillStyleUnionType.of( "black" );
    context.fillRect( 0, 0, canvas.width, canvas.height );
  }

  private void drawWorld()
  {
    int index = 0;
    double rowY = 0;
    final int rowCount = _level.getRowCount();
    final int columnCount = _level.getColumnCount();
    final Tile[] tiles = _level.getTiles();
    for ( int i = 0; i < rowCount; i++ )
    {
      double cellX = 0;
      for ( int j = 0; j < columnCount; j++ )
      {
        final Tile tile = tiles[ index++ ];
        final TileType tileType = tile.getTileType();
        final Texture texture = tileType.getTexture();
        if ( null != texture )
        {
          _renderer.getContext().drawImage( texture.getImage(), cellX, rowY );
        }
        cellX += _cellWidth;
      }
      rowY += _cellHeight;
    }
  }

  @Nonnull
  private TileType registerTile( @Nonnull final String textureName, final int value )
  {
    return registerTile( textureName, value, 0 );
  }

  @Nonnull
  private TileType registerTile( @Nonnull final String textureName, final int value, final int flags )
  {
    return _tileTypeManager.registerTileType( value, _textureManager.registerTexture( textureName ), flags );
  }

  private void texturesLoaded()
  {
    DomGlobal.console.log( "Textures loaded" );
    _texturesLoaded = true;
  }

  public void loadFromData( @Nonnull final String data )
  {
    int index = 0;
    int charIndex = 0;
    final int columnCount = _level.getColumnCount();
    final int rowCount = _level.getRowCount();
    final Tile[] tiles = _level.getTiles();
    for ( int j = 0; j < columnCount; j++ )
    {
      for ( int i = 0; i < rowCount; i++ )
      {
        // Format for each cell is [\d\d]
        final int tileId = ( data.charAt( charIndex + 1 ) - '0' ) * 10 + data.charAt( charIndex + 2 ) - '0';
        tiles[ index++ ].setTileType( _tileTypeManager.getTileType( tileId ) );
        charIndex += 4;
      }

      // Skip end line char.
      charIndex += 1;
    }
  }
}
