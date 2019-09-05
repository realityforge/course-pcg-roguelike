package org.realityforge.saber;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import javax.annotation.Nonnull;

public class Saber
  implements EntryPoint
{
  private Renderer _renderer;
  private TextureManager _textureManager;
  private TileTypeManager _tileTypeManager;

  @Override
  public void onModuleLoad()
  {
    _renderer = new Renderer( 800, 600 );
    _textureManager = new TextureManager( this::startGame );
    _tileTypeManager = new TileTypeManager();

    registerTile( "resources/tiles/spr_tile_floor", Tiles.FLOOR );

    registerTile( "resources/tiles/spr_tile_wall_top", Tiles.WALL_TOP );
    registerTile( "resources/tiles/spr_tile_wall_top_left", Tiles.WALL_TOP_LEFT );
    registerTile( "resources/tiles/spr_tile_wall_top_right", Tiles.WALL_TOP_RIGHT );
    registerTile( "resources/tiles/spr_tile_wall_top_t", Tiles.WALL_TOP_T );
    registerTile( "resources/tiles/spr_tile_wall_top_end", Tiles.WALL_TOP_END );

    registerTile( "resources/tiles/spr_tile_wall_bottom_left", Tiles.WALL_BOTTOM_LEFT );
    registerTile( "resources/tiles/spr_tile_wall_bottom_right", Tiles.WALL_BOTTOM_RIGHT );
    registerTile( "resources/tiles/spr_tile_wall_bottom_t", Tiles.WALL_BOTTOM_T );
    registerTile( "resources/tiles/spr_tile_wall_bottom_end", Tiles.WALL_BOTTOM_END );

    registerTile( "resources/tiles/spr_tile_wall_side", Tiles.WALL_SIDE );
    registerTile( "resources/tiles/spr_tile_wall_side_left_t", Tiles.WALL_SIDE_LEFT_T );
    registerTile( "resources/tiles/spr_tile_wall_side_left_end", Tiles.WALL_SIDE_LEFT_END );
    registerTile( "resources/tiles/spr_tile_wall_side_right_t", Tiles.WALL_SIDE_RIGHT_T );
    registerTile( "resources/tiles/spr_tile_wall_side_right_end", Tiles.WALL_SIDE_RIGHT_END );

    registerTile( "resources/tiles/spr_tile_wall_intersection", Tiles.WALL_INTERSECTION );
    registerTile( "resources/tiles/spr_tile_wall_single", Tiles.WALL_SINGLE );

    registerTile( "resources/tiles/spr_tile_wall_entrance", Tiles.WALL_ENTRANCE );
    registerTile( "resources/tiles/spr_tile_door_locked", Tiles.WALL_DOOR_LOCKED );
    registerTile( "resources/tiles/spr_tile_door_unlocked", Tiles.WALL_DOOR_UNLOCKED );

    _textureManager.startTextureLoad();
  }

  private void registerTile( @Nonnull final String textureName, final int value )
  {
    _tileTypeManager.registerTileType( value, _textureManager.registerTexture( textureName ) );
  }

  private void startGame()
  {
    DomGlobal.console.log( "Textures loaded" );
  }
}
