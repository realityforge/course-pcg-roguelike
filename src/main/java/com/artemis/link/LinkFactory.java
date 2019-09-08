package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Annotation;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static com.artemis.annotations.LinkPolicy.Policy.*;
import static com.artemis.utils.reflect.ReflectionUtil.*;

class LinkFactory
{
  private static final int NULL_REFERENCE = 0;
  private static final int SINGLE_REFERENCE = 1;
  private static final int MULTI_REFERENCE = 2;
  private final Bag<LinkSite> links = new Bag<>();
  @Nonnull
  private final World world;
  @Nonnull
  private final ReflexiveMutators reflexiveMutators;

  public LinkFactory( @Nonnull final World world )
  {
    this.world = world;
    reflexiveMutators = new ReflexiveMutators( world );
  }

  static int getReferenceTypeId( @Nonnull final Field f )
  {
    final Class type = f.getType();
    if ( Entity.class == type )
    {
      return SINGLE_REFERENCE;
    }
    if ( isGenericType( f, Bag.class, Entity.class ) )
    {
      return MULTI_REFERENCE;
    }

    final boolean explicitEntityId = f.getDeclaredAnnotation( EntityId.class ) != null;
    if ( int.class == type && explicitEntityId )
    {
      return SINGLE_REFERENCE;
    }
    if ( IntBag.class == type && explicitEntityId )
    {
      return MULTI_REFERENCE;
    }

    return NULL_REFERENCE;
  }

  @Nonnull
  Bag<LinkSite> create( @Nonnull final ComponentType ct )
  {
    final Class<?> type = ct.getType();
    final Field[] fields = ClassReflection.getDeclaredFields( type );

    links.clear();
    for ( final Field f : fields )
    {
      final int referenceTypeId = getReferenceTypeId( f );
      if ( referenceTypeId != NULL_REFERENCE && ( SKIP != getPolicy( f ) ) )
      {
        if ( SINGLE_REFERENCE == referenceTypeId )
        {
          final UniLinkSite ls = new UniLinkSite( world, ct, f );
          if ( !configureMutator( ls ) )
          {
            reflexiveMutators.withMutator( ls );
          }

          links.add( ls );
        }
        else if ( MULTI_REFERENCE == referenceTypeId )
        {
          final MultiLinkSite ls = new MultiLinkSite( world, ct, f );
          if ( !configureMutator( ls ) )
          {
            reflexiveMutators.withMutator( ls );
          }

          links.add( ls );
        }
      }
    }

    return links;
  }

  @Nullable
  static LinkPolicy.Policy getPolicy( @Nonnull final Field f )
  {
    final Annotation annotation = f.getDeclaredAnnotation( LinkPolicy.class );
    if ( annotation != null )
    {
      final LinkPolicy lp = annotation.getAnnotation( LinkPolicy.class );
      return lp != null ? lp.value() : null;
    }

    return null;
  }

  private boolean configureMutator( @Nonnull final UniLinkSite linkSite )
  {
    final UniFieldMutator mutator = MutatorUtil.getGeneratedMutator( linkSite );
    if ( mutator != null )
    {
      mutator.setWorld( world );
      linkSite.fieldMutator = mutator;
      return true;
    }
    else
    {
      return false;
    }
  }

  private boolean configureMutator( @Nonnull final MultiLinkSite linkSite )
  {
    final MultiFieldMutator mutator = MutatorUtil.getGeneratedMutator( linkSite );
    if ( mutator != null )
    {
      mutator.setWorld( world );
      linkSite.fieldMutator = mutator;
      return true;
    }
    else
    {
      return false;
    }
  }

  static class ReflexiveMutators
  {
    @Nonnull
    final EntityFieldMutator entityField;
    @Nonnull
    final IntFieldMutator intField;
    @Nonnull
    final IntBagFieldMutator intBagField;
    @Nonnull
    final EntityBagFieldMutator entityBagField;

    public ReflexiveMutators( @Nonnull final World world )
    {
      entityField = new EntityFieldMutator();
      entityField.setWorld( world );

      intField = new IntFieldMutator();
      intField.setWorld( world );

      intBagField = new IntBagFieldMutator();
      intBagField.setWorld( world );

      entityBagField = new EntityBagFieldMutator();
      entityBagField.setWorld( world );
    }

    @Nonnull
    UniLinkSite withMutator( @Nonnull final UniLinkSite linkSite )
    {
      if ( linkSite.fieldMutator != null )
      {
        return linkSite;
      }

      final Class type = linkSite.field.getType();
      if ( Entity.class == type )
      {
        linkSite.fieldMutator = entityField;
      }
      else if ( int.class == type )
      {
        linkSite.fieldMutator = intField;
      }
      else
      {
        throw new RuntimeException( "unexpected '" + type + "', on " + linkSite.type );
      }

      return linkSite;
    }

    @Nonnull
    MultiLinkSite withMutator( @Nonnull final MultiLinkSite linkSite )
    {
      if ( linkSite.fieldMutator != null )
      {
        return linkSite;
      }

      final Class type = linkSite.field.getType();
      if ( IntBag.class == type )
      {
        linkSite.fieldMutator = intBagField;
      }
      else if ( Bag.class == type )
      {
        linkSite.fieldMutator = entityBagField;
      }
      else
      {
        throw new RuntimeException( "unexpected '" + type + "', on " + linkSite.type );
      }

      return linkSite;
    }
  }
}
