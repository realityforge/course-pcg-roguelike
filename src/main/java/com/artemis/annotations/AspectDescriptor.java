package com.artemis.annotations;

import com.artemis.Archetype;
import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.EntitySubscription;
import com.artemis.EntitySystem;
import com.artemis.EntityTransmuter;
import com.artemis.World;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Auto-configures fields pertaining to aspects. The annotated field
 * must be one the following types: {@link Archetype}, {@link Aspect}, {@link Aspect.Builder},
 * {@link EntitySubscription}, {@link EntityTransmuter}.</p>
 *
 * <p>This annotation will take precedence over {@link All}, {@link One} and {@link Exclude}.</p>
 *
 * <p>This annotation works similar to {@link Wire}; fields are configured
 * during {@link EntitySystem#initialize()}, or explicitly via {@link World#inject(Object)}.</p>
 *
 * <h4>Note on EntityTransmuters/Archetypes</h4>
 * <p><code>all</code> and <code>exclude</code> correspond to create/remove. Archetypes
 * only feature create. Any types specified in <code>one</code> are ignored.</p>
 *
 * @see All
 * @see One
 * @see Exclude
 * @see Wire
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD } )
@Documented
public @interface AspectDescriptor
{
  /**
   * @return required types
   */
  Class<? extends Component>[] all() default {};

  /**
   * @return match at least one
   */
  Class<? extends Component>[] one() default {};

  /**
   * @return excluding types
   */
  Class<? extends Component>[] exclude() default {};
}
