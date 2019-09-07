package com.artemis.injection;

import java.util.Map;

/**
 * Field resolver for manually registered objects, for injection by type or name.
 *
 * @author Daan van Yperen
 * @see com.artemis.WorldConfiguration#register
 */
public interface PojoFieldResolver
  extends FieldResolver
{
  /**
   * Set manaully registered objects.
   *
   * @param pojos Map of manually registered objects.
   */
  void setPojos( Map<String, Object> pojos );
}
