package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.reflect.Field;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class MultiLinkSite
  extends LinkSite
{
  @Nullable
  MultiFieldMutator fieldMutator;

  protected MultiLinkSite( @Nonnull final World world,
                           @Nonnull final ComponentType type,
                           @Nonnull final Field field )
  {

    super( world, type, field, LinkPolicy.Policy.CHECK_SOURCE );
  }

  @Override
  protected void check( final int id )
  {
    final Object collection = fieldMutator.read( mapper.get( id ), field );
    fieldMutator.validate( id, collection, listener );
  }

  @Override
  protected void insert( final int id )
  {
    if ( listener != null )
    {
      listener.onLinkEstablished( id, -1 );
    }
  }

  @Override
  protected void removed( final int id )
  {
    if ( listener != null )
    {
      listener.onLinkKilled( id, -1 );
    }
  }
}
