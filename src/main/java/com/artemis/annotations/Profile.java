package com.artemis.annotations;

import com.artemis.utils.ArtemisProfiler;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Profile EntitySystems with user-specified profiler class, implementing ArtemisProfiler.
 *
 * <p>Injects conditional profiler call at start of <code>begin()</code> and before any exit
 * point in <code>end()</code>.</p>
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.CLASS )
@Documented
public @interface Profile
{
  @Nonnull Class<? extends ArtemisProfiler> using();

  boolean enabled() default true;
}
