package com.artemis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Holds the path or identifier for <code>Prefab</code> types.
 * The value from this annotation is passed to the
 * corresponding <code>PrefabReader</code>.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@Documented
public @interface PrefabData
{
  @Nonnull String value();
}
