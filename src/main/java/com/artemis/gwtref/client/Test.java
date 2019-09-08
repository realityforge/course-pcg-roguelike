/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.artemis.gwtref.client;

import com.google.gwt.core.client.EntryPoint;
import javax.annotation.Nonnull;

public class Test
  implements EntryPoint
{
  public enum Enu
  {
    Winter, Summer, Bleh
  }

  public static class A
  {
    String text;
    float numberf;
    int numberi;

    public String getText()
    {
      return text;
    }

    public void setText( final String text )
    {
      this.text = text;
    }

    public float getNumberf()
    {
      return numberf;
    }

    public void setNumberf( final float numberf )
    {
      this.numberf = numberf;
    }

    public int getNumberi()
    {
      return numberi;
    }

    public void setNumberi( final int numberi )
    {
      this.numberi = numberi;
    }

    public float getSum( final float a, final float b )
    {
      return a + b;
    }
  }

  public static class B
    extends A
  {
    @Nonnull
    String text = "This is a string";

    public void testWithPackagePrivate( final C c, final int a )
    {
    }

    public void testWidthPrivate( final A c )
    {
    }

    public void testVoid()
    {
    }

    public native void test( A c ) /*-{
      //			this.@com.badlogic.gwtref.client.Test.B::testWidthPrivate(LC;)(c);
    }-*/;
  }

  public static class C
  {
  }

  @Override
  public void onModuleLoad()
  {
    try
    {
      final Type ta = ReflectionCache.getType( A.class );
      final Type tb = ReflectionCache.getType( B.class );
      final B b = (B) tb.newInstance();
      for ( final Field f : tb.getFields() )
      {
        System.out.println( f );
      }
      for ( final Method m : tb.getMethods() )
      {
        System.out.println( m );
      }

      tb.getDeclaredFields()[ 0 ].set( b, "Field of B" );
      ta.getDeclaredFields()[ 0 ].set( b, "Field of A" );
      System.out.println( ta.getMethod( "getText" ).invoke( b ) );
      System.out.println( ta.getMethod( "getSum", float.class, float.class ).invoke( b, 1, 2 ) );
    }
    catch ( final Exception e )
    {
      e.printStackTrace();
    }
  }
}
