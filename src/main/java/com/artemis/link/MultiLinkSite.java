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

  protected MultiLinkSite( @Nonnull World world,
                           @Nonnull ComponentType type,
                           @Nonnull Field field )
  {

    super( world, type, field, LinkPolicy.Policy.CHECK_SOURCE );
  }

  @Override
  protected void check( int id )
  {
    Object collection = fieldMutator.read( mapper.get( id ), field );
    fieldMutator.validate( id, collection, listener );
  }

  @Override
  protected void insert( int id )
  {
		if ( listener != null )
		{
			listener.onLinkEstablished( id, -1 );
		}
  }

  @Override
  protected void removed( int id )
  {
		if ( listener != null )
		{
			listener.onLinkKilled( id, -1 );
		}
  }
}
