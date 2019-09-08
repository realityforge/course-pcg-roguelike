package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class UniLinkSite
  extends LinkSite
{
  @Nullable
  UniFieldMutator fieldMutator;
  private final IntBag sourceToTarget = new IntBag();

  protected UniLinkSite( @Nonnull final World world,
                         @Nonnull final ComponentType type,
                         @Nonnull final Field field )
  {

    super( world, type, field, LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS );
  }

  @Override
  protected void check( final int id )
  {
    // -1 == not linked
    int target = fieldMutator.read( mapper.get( id ), field );
    if ( target != -1 && !activeEntityIds.unsafeGet( target ) )
    {
      // target is dead or linked field is set to null/-1
      target = -1;
      fieldMutator.write( target, mapper.get( id ), field );
    }

    final int oldTarget = sourceToTarget.get( id );
    if ( target != oldTarget )
    {
      if ( listener != null )
      {
        fireLinkListener( id, target );
      }

      sourceToTarget.set( id, target );
    }
  }

  private void fireLinkListener( final int id, final int target )
  {
    final int oldTarget = sourceToTarget.get( id );
    if ( oldTarget == -1 )
    {
      listener.onLinkEstablished( id, target );
    }
    else if ( target != -1 )
    {
      listener.onTargetChanged( id, target, oldTarget );
    }
    else
    {
      listener.onTargetDead( id, oldTarget );
    }
  }

  @Override
  protected void insert( final int id )
  {
    final int target = fieldMutator.read( mapper.get( id ), field );
    sourceToTarget.set( id, target );
    if ( target != -1 && listener != null )
    {
      listener.onLinkEstablished( id, target );
    }
  }

  @Override
  protected void removed( final int id )
  {
    final int target = sourceToTarget.size() > id
                       ? sourceToTarget.get( id )
                       : -1;

    if ( target != -1 )
    {
      sourceToTarget.set( id, -1 );
    }

    if ( listener != null )
    {
      listener.onLinkKilled( id, target );
    }
  }
}
