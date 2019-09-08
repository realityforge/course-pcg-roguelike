package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class UuidEntityManager
  extends Manager
{
  @Nonnull
  private final Map<UUID, Entity> uuidToEntity;
  @Nonnull
  private final Bag<UUID> entityToUuid;

  public UuidEntityManager()
  {
    this.uuidToEntity = new HashMap<>();
    this.entityToUuid = new Bag<>();
  }

  @Override
  public void deleted( @Nonnull final Entity e )
  {
    final UUID uuid = entityToUuid.safeGet( e.getId() );
		if ( uuid == null )
		{
			return;
		}

    final Entity oldEntity = uuidToEntity.get( uuid );
		if ( oldEntity != null && oldEntity.equals( e ) )
		{
			uuidToEntity.remove( uuid );
		}

    entityToUuid.set( e.getId(), null );
  }

  public void updatedUuid( @Nonnull final Entity e, final UUID newUuid )
  {
    setUuid( e, newUuid );
  }

  public Entity getEntity( final UUID uuid )
  {
    return uuidToEntity.get( uuid );
  }

  public UUID getUuid( @Nonnull final Entity e )
  {
    UUID uuid = entityToUuid.safeGet( e.getId() );
    if ( uuid == null )
    {
      uuid = UUID.randomUUID();
      setUuid( e, uuid );
    }

    return uuid;
  }

  public void setUuid( @Nonnull final Entity e, final UUID newUuid )
  {
    final UUID oldUuid = entityToUuid.safeGet( e.getId() );
		if ( oldUuid != null )
		{
			uuidToEntity.remove( oldUuid );
		}

    uuidToEntity.put( newUuid, e );
    entityToUuid.set( e.getId(), newUuid );
  }
}
