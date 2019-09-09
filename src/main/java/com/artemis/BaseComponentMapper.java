package com.artemis;

import com.artemis.annotations.DelayedComponentRemoval;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseComponentMapper<A extends Component>
{
  /**
   * The type of components this mapper handles.
   */
  public final ComponentType type;

  protected BaseComponentMapper( final ComponentType type )
  {
    this.type = type;
  }

  /**
   * Returns a component mapper for this type of components.
   *
   * @param <T>   the class type of components
   * @param type  the class of components this mapper uses
   * @param world the world that this component mapper should use
   * @return a new mapper
   */
  public static <T extends Component> BaseComponentMapper<T> getFor( final Class<T> type, @Nonnull final World world )
  {
    return world.getMapper( type );
  }

  /**
   * Fast but unsafe retrieval of a component for this entity.
   *
   * This method trades performance for safety.
   *
   * User is expected to avoid calling this method on recently (in same system) removed components
   * or invalid entity ids. Might return null, throw {@link ArrayIndexOutOfBoundsException}
   * or a partially recycled component if called on in-system removed components.
   *
   * Only exception are components marked with {@link DelayedComponentRemoval}, when calling
   * this method from within a subscription listener.
   *
   * @param e the entity that should possess the component
   * @return the instance of the component.
   */
  public A get( @Nonnull final Entity e )
    throws ArrayIndexOutOfBoundsException
  {
    return get( e.getId() );
  }

  /**
   * Fast but unsafe retrieval of a component for this entity.
   *
   * This method trades performance for safety.
   *
   * User is expected to avoid calling this method on recently (in same system) removed components
   * or invalid entity ids. Might return null, throw {@link ArrayIndexOutOfBoundsException}
   * or a partially recycled component if called on in-system removed components.
   *
   * Only exception are components marked with {@link DelayedComponentRemoval}, when calling
   * this method from within a subscription listener.
   *
   * @param entityId the entity that should possess the component
   * @return the instance of the component.
   */
  @Nullable
  public abstract A get( int entityId )
    throws ArrayIndexOutOfBoundsException;

  /**
   * Checks if the entity has this type of component.
   *
   * @param e the entity to check
   * @return true if the entity has this component type, false if it doesn't
   */
  public boolean has( @Nonnull final Entity e )
    throws ArrayIndexOutOfBoundsException
  {
    return has( e.getId() );
  }

  public abstract boolean has( int entityId );

  /**
   * Create component for this entity.
   * Will avoid creation if component preexists.
   *
   * @param entity the entity that should possess the component
   * @return the instance of the component.
   */
  @Nullable
  public A create( @Nonnull final Entity entity )
  {
    return create( entity.getId() );
  }

  public abstract void remove( int entityId );

  /**
   * Remove component from entity.
   * Does nothing if already removed.
   *
   * @param entity entity to remove.
   */
  public void remove( @Nonnull final Entity entity )
  {
    remove( entity.getId() );
  }

  protected abstract void internalRemove( int entityId );

  @Nullable
  public abstract A create( int entityId );

  @Nullable
  public abstract A internalCreate( int entityId );

  /**
   * Fast and safe retrieval of a component for this entity.
   * If the entity does not have this component then fallback is returned.
   *
   * @param entityId Entity that should possess the component
   * @param fallback fallback component to return, or {@code null} to return null.
   * @return the instance of the component
   */
  public A getSafe( final int entityId, final A fallback )
  {
    final A c = get( entityId );
    return ( c != null ) ? c : fallback;
  }

  /**
   * Create or remove a component from an entity.
   *
   * Does nothing if already removed or created respectively.
   *
   * @param entityId Entity id to change.
   * @param value    {@code true} to create component (if missing), {@code false} to remove (if exists).
   * @return the instance of the component, or {@code null} if removed.
   */
  @Nullable
  public A set( final int entityId, final boolean value )
  {
    if ( value )
    {
      return create( entityId );
    }
    else
    {
      remove( entityId );
      return null;
    }
  }

  /**
   * Create or remove a component from an entity.
   *
   * Does nothing if already removed or created respectively.
   *
   * @param entity Entity to change.
   * @param value  {@code true} to create component (if missing), {@code false} to remove (if exists).
   * @return the instance of the component, or {@code null} if removed.
   */
  @Nullable
  public A set( @Nonnull final Entity entity, final boolean value )
  {
    return set( entity.getId(), value );
  }

  /**
   * Fast and safe retrieval of a component for this entity.
   * If the entity does not have this component then fallback is returned.
   *
   * @param entity   Entity that should possess the component
   * @param fallback fallback component to return, or {@code null} to return null.
   * @return the instance of the component
   */
  public A getSafe( @Nonnull final Entity entity, final A fallback )
  {
    return getSafe( entity.getId(), fallback );
  }

  /**
   * Returns the ComponentType of this ComponentMapper.
   * see {@link ComponentMapper#type}
   */
  public ComponentType getType()
  {
    return type;
  }

  @Nonnull
  @Override
  public String toString()
  {
    return "ComponentMapper[" + type.getType().getSimpleName() + ']';
  }
}
