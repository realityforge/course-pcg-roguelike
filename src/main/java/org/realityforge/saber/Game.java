package org.realityforge.saber;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.KeyboardEvent;
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
  public static Game c_game;
  @Nonnull
  private final Renderer _renderer;
  @Nonnull
  private final TileTypeManager _tileTypeManager = new TileTypeManager();
  @Nonnull
  private final TextureManager _textureManager = new TextureManager( this::texturesLoaded );
  private Level _level;
  private boolean _texturesLoaded;
  @Nonnull
  private final SaberApplication _application;
  @Nonnull
  private CommandType _commandType = CommandType.MoveForward;

  public static Game getGame()
  {
    assert null != c_game;
    return c_game;
  }

  public Game( @Nonnull final Renderer renderer )
  {
    c_game = this;
    _renderer = Objects.requireNonNull( renderer );
    _application = SaberApplication.create();
  }

  @Nonnull
  public SaberApplication getApplication()
  {
    return _application;
  }

  @Nonnull
  public Renderer getRenderer()
  {
    return _renderer;
  }

  @Nonnull
  public TextureManager getTextureManager()
  {
    return _textureManager;
  }

  public Level getLevel()
  {
    return _level;
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

    initLevel( emptyTileType );

    _textureManager.registerTexture( "resources/players/warrior/spr_warrior_idle_down" );
  }

  private void initLevel( @Nonnull final TileType emptyTileType )
  {
    _level = new Level( 19, 19, emptyTileType );

    final HTMLCanvasElement canvas = _renderer.getCanvas();
    final double cellWidth = canvas.width / ( _level.getColumnCount() * 1D );
    final double cellHeight = canvas.height / ( _level.getRowCount() * 1D );

    int index = 0;
    final int columnCount = _level.getColumnCount();
    final int rowCount = _level.getRowCount();
    final Tile[] tiles = _level.getTiles();
    double topLeftY = 0;
    for ( int i = 0; i < rowCount; i++ )
    {
      double topLeftX = 0;
      for ( int j = 0; j < columnCount; j++ )
      {
        final Tile tile = tiles[ index++ ];
        tile.setTopLeftX( topLeftX );
        tile.setTopLeftY( topLeftY );
        topLeftX += cellWidth;
      }
      topLeftY += cellHeight;
    }
  }

  public void start( @Nonnull final String levelData )
  {
    _textureManager.startTextureLoad();
    loadFromData( levelData );

    getApplication().sim().process( 1 );

    runFrame();
    DomGlobal.setInterval( v -> runFrame(), FRAME_DELAY );
    DomGlobal.document.addEventListener( "keydown", e -> onKeyUp( (KeyboardEvent) e ) );
  }

  private void onKeyUp( @Nonnull final KeyboardEvent event )
  {
    final CommandType commandType;
    if ( "ArrowLeft".equals( event.code ) )
    {
      commandType = CommandType.TurnLeft;
    }
    else if ( "ArrowRight".equals( event.code ) )
    {
      commandType = CommandType.TurnRight;
    }
    else if ( "ArrowUp".equals( event.code ) )
    {
      commandType = CommandType.MoveForward;
    }
    else if ( "ArrowDown".equals( event.code ) )
    {
      commandType = CommandType.MoveBackward;
    }
    else
    {
      return;
    }
    event.preventDefault();
    event.stopPropagation();

    executeTurn( commandType );
  }

  private void executeTurn( @Nonnull final CommandType commandType )
  {
    _commandType = commandType;
    getApplication().sim().process( 1 );
  }

  @Nonnull
  public CommandType getCommandType()
  {
    return _commandType;
  }

  private void runFrame()
  {
    if ( _texturesLoaded )
    {
      renderWorld();
    }
  }

  private void renderWorld()
  {
    getApplication().renderStage().process( 1 );
  }

  private void registerTile( @Nonnull final String textureName, final int value )
  {
    registerTile( textureName, value, 0 );
  }

  private void registerTile( @Nonnull final String textureName, final int value, final int flags )
  {
    _tileTypeManager.registerTileType( value, _textureManager.registerTexture( textureName ), flags );
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
