package com.artemis.managers;

import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Use this class together with PlayerManager.
 * <p>
 * You may sometimes want to create teams in your game, so that some players
 * are team mates.
 * </p><p>
 * A player can only belong to a single team.
 * </p>
 *
 * @author Arni Arent
 */
public class TeamManager
  extends Manager
{
  /**
   * Teams mapped to their players.
   */
  @Nonnull
  private final Map<String, Bag<String>> playersByTeam;
  /**
   * Players mapped to their teams.
   */
  @Nonnull
  private final Map<String, String> teamByPlayer;

  /**
   * Creates a new TeamManager instance.
   */
  public TeamManager()
  {
    playersByTeam = new HashMap<>();
    teamByPlayer = new HashMap<>();
  }

  @Override
  protected void initialize()
  {
  }

  /**
   * The the name of the team the given player is in.
   *
   * @param player the player
   * @return the player's team
   */
  public String getTeam( final String player )
  {
    return teamByPlayer.get( player );
  }

  /**
   * Set the player's team.
   * <p>
   * Each player can only be in one team at a time.
   * </p>
   *
   * @param player the player
   * @param team   the team to put the player in
   */
  public void setTeam( @Nonnull final String player, final String team )
  {
    removeFromTeam( player );

    teamByPlayer.put( player, team );

    Bag<String> players = playersByTeam.get( team );
    if ( players == null )
    {
      players = new Bag<>();
      playersByTeam.put( team, players );
    }
    players.add( player );
  }

  /**
   * Get all players on a team.
   *
   * @param team the team
   * @return all players on the team in a bag
   */
  public ImmutableBag<String> getPlayers( final String team )
  {
    return playersByTeam.get( team );
  }

  /**
   * Remove a player from his team.
   *
   * @param player the player to remove
   */
  public void removeFromTeam( @Nonnull final String player )
  {
    final String team = teamByPlayer.remove( player );
    if ( team != null )
    {
      final Bag<String> players = playersByTeam.get( team );
      if ( players != null )
      {
        players.remove( player );
      }
    }
  }

}
