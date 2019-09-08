package com.artemis.annotations;

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
import javax.annotation.Nonnull;

/**
 * <p>Auto-configures fields or systems pertaining to aspects. The annotated field
 * must be one the following types: {@link Aspect}, {@link Aspect.Builder},
 * {@link EntitySubscription}, {@link EntityTransmuter}.</p>
 *
 * <p>On BaseEntitySystem subclasses, this annotation configures the aspects for the system,
 * replacing the need to use constructor parameters.</>/p>
 *
 * <p>This annotation can be combined with {@link All} and {@link One},
 * but will be ignored if {@link AspectDescriptor} is present.</p>
 *
 * <p>This annotation works similar to {@link Wire}; fields are configured
 * during {@link EntitySystem#initialize()}, or explicitly via {@link World#inject(Object)}.</p>
 *
 * <h4>Note on EntityTransmuters</h4>
 * <p>{@link #value()} corresponds to remove.</p>
 *
 * @author Felix Bridault
 * @author Ken Schosinsky
 * @see All
 * @see One
 * @see AspectDescriptor
 * @see Wire
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.TYPE } )
@Documented
@UnstableApi
public @interface Exclude
{
  /**
   * @return excluding types
   */
  @Nonnull Class<? extends Component>[] value() default {};

}
