package org.realityforge.saber;

import elemental2.dom.DomGlobal;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.saber.game.Tiles;
import org.realityforge.saber.world.Level;
import org.realityforge.saber.world.TileType;
import org.realityforge.saber.world.TileTypeManager;

public final class Game
{
  @Nonnull
  private final Renderer _renderer;
  @Nonnull
  private final TileTypeManager _tileTypeManager = new TileTypeManager();
  @Nonnull
  private final TextureManager _textureManager = new TextureManager( this::dataLoaded );
  private Level _level;

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
  }

  public void start()
  {
    _textureManager.startTextureLoad();
  }

  private void registerTile( @Nonnull final String textureName, final int value )
  {
    registerTile( textureName, value, 0 );
  }

  private void registerTile( @Nonnull final String textureName, final int value, final int flags )
  {
    _tileTypeManager.registerTileType( value, _textureManager.registerTexture( textureName ), flags );
  }

  private void dataLoaded()
  {
    DomGlobal.console.log( "Textures loaded" );
  }
}
