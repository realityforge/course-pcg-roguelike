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
package com.artemis.gwtref.gen;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JAbstractMethod;
import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JEnumConstant;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReflectionCacheSourceCreator
{
  private static final List<String> PRIMITIVE_TYPES =
    Collections.unmodifiableList( Arrays.asList( "char",
																								 "int",
																								 "long",
																								 "byte",
																								 "short",
																								 "float",
																								 "double",
																								 "boolean" ) );
  @Nonnull
  final TreeLogger logger;
  final GeneratorContext context;
  @Nonnull
  final JClassType type;
  @Nonnull
  final String simpleName;
  final String packageName;
  SourceWriter sw;
  @Nonnull
  final
  StringBuffer source = new StringBuffer();
  @Nonnull
  final
  List<JType> types = new ArrayList<>();
  @Nonnull
  final
  List<SetterGetterStub> setterGetterStubs = new ArrayList<>();
  @Nonnull
  final
  List<MethodStub> methodStubs = new ArrayList<>();
  int nextId = 0;

  class SetterGetterStub
  {
    int getter;
    int setter;
    String name;
    @Nullable
    String enclosingType;
    @Nullable
    String type;
    boolean isStatic;
    boolean isFinal;
    boolean unused;
  }

  class MethodStub
  {
    @Nullable
    String enclosingType;
    @Nullable
    String returnType;
    @Nonnull
    final
    List<String> parameterTypes = new ArrayList<>();
    String jnsi;
    int methodId;
    boolean isStatic;
    boolean isAbstract;
    boolean isFinal;
    boolean isNative;
    boolean isConstructor;
    boolean isMethod;
    boolean isPublic;
    String name;
    boolean unused;
  }

  public ReflectionCacheSourceCreator( @Nonnull final TreeLogger logger, final GeneratorContext context, @Nonnull final JClassType type )
  {
    this.logger = logger;
    this.context = context;
    this.type = type;
    this.packageName = type.getPackage().getName();
    this.simpleName = type.getSimpleSourceName() + "Generated";
    logger.log( Type.INFO, type.getQualifiedSourceName() );
  }

  private int nextId()
  {
    return nextId++;
  }

  @Nonnull
  public String create()
  {
    final ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory( packageName, simpleName );
    composer.addImplementedInterface( "com.artemis.gwtref.client.IReflectionCache" );
    imports( composer );
    final PrintWriter printWriter = context.tryCreate( logger, packageName, simpleName );
    if ( printWriter == null )
    {
      return packageName + "." + simpleName;
    }
    sw = composer.createSourceWriter( context, printWriter );

    generateLookups();

    getKnownTypesC();
    forNameC();
    newArrayC();

    getArrayLengthT();
    getArrayElementT();
    setArrayElementT();

    getF();
    setF();

    invokeM();

    sw.commit( logger );
    createProxy( type );
    return packageName + "." + simpleName;
  }

  private void createProxy( @Nonnull final JClassType type )
  {
    final ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory( type.getPackage().getName(),
                                                                                  type.getSimpleSourceName() +
                                                                                  "Proxy" );
    final PrintWriter printWriter = context.tryCreate( logger, packageName, simpleName );
    if ( printWriter == null )
    {
      return;
    }
    final SourceWriter writer = composer.createSourceWriter( context, printWriter );
    writer.commit( logger );
  }

  private void generateLookups()
  {
    p( "Map<String, Type> types = new HashMap<String, Type>();" );

    final TypeOracle typeOracle = context.getTypeOracle();
    final JPackage[] packages = typeOracle.getPackages();

    // gather all types from wanted packages
    for ( final JPackage p : packages )
    {
      for ( final JClassType t : p.getTypes() )
      {
        gatherTypes( t.getErasedType(), types );
      }
    }

    gatherTypes( typeOracle.findType( "java.util.List" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.util.ArrayList" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.util.HashMap" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.util.Map" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.String" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Boolean" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Byte" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Long" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Character" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Short" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Integer" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Float" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.CharSequence" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Double" ).getErasedType(), types );
    gatherTypes( typeOracle.findType( "java.lang.Object" ).getErasedType(), types );

    // sort the types so the generated output will be stable between runs
    Collections.sort( types, new Comparator<JType>()
    {
      public int compare( @Nonnull final JType o1, @Nonnull final JType o2 )
      {
        return o1.getQualifiedSourceName().compareTo( o2.getQualifiedSourceName() );
      }
    } );

    // generate Type lookup generator methods.
    int id = 0;
    for ( final JType t : types )
    {
      final String typeGen = createTypeGenerator( t );
      p( "private void c" + ( id++ ) + "() {" );
      p( typeGen );
      p( "}\n" );
    }

    // generate constructor that calls all the type generators
    // that populate the map.
    p( "public " + simpleName + "() {" );
    for ( int i = 0; i < id; i++ )
    {
      p( "c" + i + "();" );
    }
    p( "}" );

    // sort the stubs so the generated output will be stable between runs
    Collections.sort( setterGetterStubs, new Comparator<SetterGetterStub>()
    {
      @Override
      public int compare( @Nonnull final SetterGetterStub o1, @Nonnull final SetterGetterStub o2 )
      {
        return new Integer( o1.setter ).compareTo( o2.setter );
      }
    } );

    // generate field setters/getters
    for ( final SetterGetterStub stub : setterGetterStubs )
    {
      final String stubSource = generateSetterGetterStub( stub );
			if ( stubSource.equals( "" ) )
			{
				stub.unused = true;
			}
      p( stubSource );
    }

    // sort the stubs so the generated output will be stable between runs
    Collections.sort( methodStubs, new Comparator<MethodStub>()
    {
      @Override
      public int compare( @Nonnull final MethodStub o1, @Nonnull final MethodStub o2 )
      {
        return new Integer( o1.methodId ).compareTo( o2.methodId );
      }
    } );

    // generate methods
    for ( final MethodStub stub : methodStubs )
    {
      final String stubSource = generateMethodStub( stub );
			if ( stubSource.equals( "" ) )
			{
				stub.unused = true;
			}
      p( stubSource );
    }

    logger.log( Type.INFO, types.size() + " types reflected" );
  }

  private void out( final String message, final int nesting )
  {
		for ( int i = 0; i < nesting; i++ )
		{
			System.out.print( "  " );
		}
    System.out.println( message );
  }

  int nesting = 0;

  private void gatherTypes( @Nullable final JType type, @Nonnull final List<JType> types )
  {
    nesting++;
    // came here from a type that has no super class
    if ( type == null )
    {
      nesting--;
      return;
    }
    // package info
    if ( type.getQualifiedSourceName().contains( "-" ) )
    {
      nesting--;
      return;
    }

    // not visible
    if ( !isVisible( type ) )
    {
      nesting--;
      return;
    }

    // filter reflection scope based on configuration in gwt xml module
    boolean keep = false;
    final String name = type.getQualifiedSourceName();
    try
    {
      ConfigurationProperty prop;
      keep |= !name.contains( "." );
      prop = context.getPropertyOracle().getConfigurationProperty( "artemis.reflect.include" );
			for ( final String s : prop.getValues() )
			{
				keep |= name.contains( s );
			}
      prop = context.getPropertyOracle().getConfigurationProperty( "artemis.reflect.exclude" );
			for ( final String s : prop.getValues() )
			{
				keep &= !name.equals( s );
			}
    }
    catch ( final BadPropertyValueException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if ( !keep )
    {
      nesting--;
      return;
    }

    // already visited this type
    if ( types.contains( type.getErasedType() ) )
    {
      nesting--;
      return;
    }
    types.add( type.getErasedType() );
    out( type.getErasedType().getQualifiedSourceName(), nesting );

    if ( type instanceof JPrimitiveType )
    {
      // nothing to do for a primitive type
      nesting--;
      return;
    }
    else
    {
      // gather fields
      final JClassType c = (JClassType) type;
      final JField[] fields = c.getFields();
      if ( fields != null )
      {
        for ( final JField field : fields )
        {
          gatherTypes( field.getType().getErasedType(), types );
        }
      }

      // gather super types & interfaces
      gatherTypes( c.getSuperclass(), types );
      final JClassType[] interfaces = c.getImplementedInterfaces();
      if ( interfaces != null )
      {
        for ( final JClassType i : interfaces )
        {
          gatherTypes( i.getErasedType(), types );
        }
      }

      // gather method parameter & return types
      final JMethod[] methods = c.getMethods();
      if ( methods != null )
      {
        for ( final JMethod m : methods )
        {
          gatherTypes( m.getReturnType().getErasedType(), types );
          if ( m.getParameterTypes() != null )
          {
            for ( final JType p : m.getParameterTypes() )
            {
              gatherTypes( p.getErasedType(), types );
            }
          }
        }
      }

      // gather inner classes
      final JClassType[] inner = c.getNestedTypes();
      if ( inner != null )
      {
        for ( final JClassType i : inner )
        {
          gatherTypes( i.getErasedType(), types );
        }
      }
    }
    nesting--;
  }

  @Nonnull
  private String generateMethodStub( @Nonnull final MethodStub stub )
  {
    buffer.setLength( 0 );

    if ( stub.enclosingType == null )
    {
      logger.log( Type.INFO, "method '" + stub.name + "' of invisible class is not invokable" );
      return "";
    }

    if ( ( stub.enclosingType.startsWith( "java" ) && !stub.enclosingType.startsWith( "java.util" ) ) ||
         stub.enclosingType.contains( "google" ) )
    {
      logger.log( Type.INFO, "not emitting code for accessing method " + stub.name + " in class '" + stub.enclosingType
                             + ", either in java.* or GWT related class" );
      return "";
    }

    if ( stub.enclosingType.contains( "[]" ) )
    {
      logger.log( Type.INFO, "method '" + stub.name + "' of class '" + stub.enclosingType
                             + "' is not invokable because the class is an array type" );
      return "";
    }

    for ( int i = 0; i < stub.parameterTypes.size(); i++ )
    {
      final String paramType = stub.parameterTypes.get( i );
      if ( paramType == null )
      {
        logger.log( Type.INFO, "method '" + stub.name + "' of class '" + stub.enclosingType
                               + "' is not invokable because one of its argument types is not visible" );
        return "";
      }
      else if ( paramType.startsWith( "long" ) || paramType.contains( "java.lang.Long" ) )
      {
        logger.log( Type.INFO, "method '" + stub.name + "' of class '" + stub.enclosingType
                               + " has long parameter, prohibited in JSNI" );
        return "";
      }
      else
      {
        stub.parameterTypes.set( i, paramType.replace( ".class", "" ) );
      }
    }
    if ( stub.returnType == null )
    {
      logger.log( Type.INFO, "method '" + stub.name + "' of class '" + stub.enclosingType
                             + "' is not invokable because its return type is not visible" );
      return "";
    }
    if ( stub.returnType.startsWith( "long" ) || stub.returnType.contains( "java.lang.Long" ) )
    {
      logger.log( Type.INFO, "method '" + stub.name + "' of class '" + stub.enclosingType
                             + " has long return type, prohibited in JSNI" );
      return "";
    }

    stub.enclosingType = stub.enclosingType.replace( ".class", "" );
    stub.returnType = stub.returnType.replace( ".class", "" );

    if ( stub.isMethod )
    {
      final boolean isVoid = stub.returnType.equals( "void" );
      pbn( "private native " + ( isVoid ? "Object" : stub.returnType ) + " m" + stub.methodId + "(" );
			if ( !stub.isStatic )
			{
				pbn( stub.enclosingType + " obj" + ( stub.parameterTypes.size() > 0 ? ", " : "" ) );
			}
      int i = 0;
      for ( final String paramType : stub.parameterTypes )
      {
        pbn( paramType + " p" + i + ( i < stub.parameterTypes.size() - 1 ? "," : "" ) );
        i++;
      }
      pbn( ") /*-{" );

			if ( !isVoid )
			{
				pbn( "return " );
			}
			if ( stub.isStatic )
			{
				pbn( "@" + stub.enclosingType + "::" + stub.name + "(" + stub.jnsi + ")(" );
			}
			else
			{
				pbn( "obj.@" + stub.enclosingType + "::" + stub.name + "(" + stub.jnsi + ")(" );
			}

      for ( i = 0; i < stub.parameterTypes.size(); i++ )
      {
        pbn( "p" + i + ( i < stub.parameterTypes.size() - 1 ? ", " : "" ) );
      }
      pbn( ");" );
			if ( isVoid )
			{
				pbn( "return null;" );
			}
      pbn( "}-*/;" );
    }
    else
    {
      pbn( "private static " + stub.returnType + " m" + stub.methodId + "(" );
      int i = 0;
      for ( final String paramType : stub.parameterTypes )
      {
        pbn( paramType + " p" + i + ( i < stub.parameterTypes.size() - 1 ? "," : "" ) );
        i++;
      }
      pbn( ") {" );

      pbn( "return new " + stub.returnType + "(" );
      for ( i = 0; i < stub.parameterTypes.size(); i++ )
      {
        pbn( "p" + i + ( i < stub.parameterTypes.size() - 1 ? ", " : "" ) );
      }
      pbn( ")" );
      if ( !stub.isPublic )
      {
        // Access non-public constructors through an anonymous class
        pbn( "{}" );
      }
      pbn( ";" );

      pbn( "}" );
    }

    return buffer.toString();
  }

  @Nonnull
  private String generateSetterGetterStub( @Nonnull final SetterGetterStub stub )
  {
    buffer.setLength( 0 );
    if ( stub.enclosingType == null || stub.type == null )
    {
      logger.log( Type.INFO,
                  "field '" + stub.name + "' in class '" + stub.enclosingType + "' is not accessible as its type '"
                  + stub.type + "' is not public" );
      return "";
    }
    if ( stub.enclosingType.startsWith( "java" ) || stub.enclosingType.contains( "google" ) )
    {
      logger.log( Type.INFO, "not emitting code for accessing field " + stub.name + " in class '" + stub.enclosingType
                             + ", either in java.* or GWT related class" );
      return "";
    }

    if ( stub.type.startsWith( "long" ) || stub.type.contains( "java.lang.Long" ) )
    {
      logger.log( Type.INFO, "not emitting code for accessing field " + stub.name + " in class '" + stub.enclosingType
                             + " as its of type long which can't be used with JSNI" );
      return "";
    }

    stub.enclosingType = stub.enclosingType.replace( ".class", "" );
    stub.type = stub.type.replace( ".class", "" );

    pb( "// " + stub.enclosingType + "#" + stub.name );
    pbn( "private native " + stub.type + " g" + stub.getter + "(" + stub.enclosingType + " obj) /*-{" );
		if ( stub.isStatic )
		{
			pbn( "return @" + stub.enclosingType + "::" + stub.name + ";" );
		}
		else
		{
			pbn( "return obj.@" + stub.enclosingType + "::" + stub.name + ";" );
		}
    pb( "}-*/;" );

    if ( !stub.isFinal )
    {
      pbn( "private native void s" + stub.setter + "(" + stub.enclosingType + " obj, " + stub.type + " value)  /*-{" );
			if ( stub.isStatic )
			{
				pbn( "@" + stub.enclosingType + "::" + stub.name + " = value" );
			}
			else
			{
				pbn( "obj.@" + stub.enclosingType + "::" + stub.name + " = value;" );
			}
      pb( "}-*/;" );
    }

    return buffer.toString();
  }

  private boolean isVisible( @Nullable final JType type )
  {
		if ( type == null )
		{
			return false;
		}

    if ( type instanceof JClassType )
    {
      if ( type instanceof JArrayType )
      {
        JType componentType = ( (JArrayType) type ).getComponentType();
        while ( componentType instanceof JArrayType )
        {
          componentType = ( (JArrayType) componentType ).getComponentType();
        }
        if ( componentType instanceof JClassType )
        {
          return ( (JClassType) componentType ).isPublic();
        }
      }
      else
      {
        return ( (JClassType) type ).isPublic();
      }
    }
    return true;
  }

  @Nonnull
  private final Map<String, Integer> typeNames2typeIds = new HashMap<>();

  @Nonnull
  private String createTypeGenerator( final JType t )
  {
    buffer.setLength( 0 );
    String varName = "t";
		if ( t instanceof JPrimitiveType )
		{
			varName = "p";
		}
    final int id = nextId();
    typeNames2typeIds.put( t.getErasedType().getQualifiedSourceName(), id );
    pb( "Type " + varName + " = new Type();" );
    pb( varName + ".name = \"" + t.getErasedType().getQualifiedSourceName() + "\";" );
    pb( varName + ".id = " + id + ";" );
    pb( varName + ".clazz = " + t.getErasedType().getQualifiedSourceName() + ".class;" );
    if ( t instanceof JClassType )
    {
      final JClassType c = (JClassType) t;
			if ( isVisible( c.getSuperclass() ) )
			{
				pb( varName + ".superClass = " + c.getSuperclass().getErasedType().getQualifiedSourceName() + ".class;" );
			}
      if ( c.getFlattenedSupertypeHierarchy() != null )
      {
        pb( "Set<Class> " + varName + "Assignables = new HashSet<Class>();" );
        for ( final JType i : c.getFlattenedSupertypeHierarchy() )
        {
					if ( !isVisible( i ) )
					{
						continue;
					}
          pb( varName + "Assignables.add(" + i.getErasedType().getQualifiedSourceName() + ".class);" );
        }
        pb( varName + ".assignables = " + varName + "Assignables;" );
      }
			if ( c.isInterface() != null )
			{
				pb( varName + ".isInterface = true;" );
			}
			if ( c.isEnum() != null )
			{
				pb( varName + ".isEnum = true;" );
			}
			if ( c.isArray() != null )
			{
				pb( varName + ".isArray = true;" );
			}
			if ( c.isMemberType() )
			{
				pb( varName + ".isMemberClass = true;" );
			}
      pb( varName + ".isStatic = " + c.isStatic() + ";" );
      pb( varName + ".isAbstract = " + c.isAbstract() + ";" );

      if ( c.getFields() != null )
      {
        pb( varName + ".fields = new Field[] {" );
        for ( final JField f : c.getFields() )
        {
          final String enclosingType = getType( c );
          final String fieldType = getType( f.getType() );
          final int setter = nextId();
          final int getter = nextId();
          final String elementType = getElementTypes( f );
          final String annotations = getAnnotations( f.getDeclaredAnnotations() );

          pb( "new Field(\"" + f.getName() + "\", " + enclosingType + ", " + fieldType + ", " + f.isFinal() + ", "
              + f.isDefaultAccess() + ", " + f.isPrivate() + ", " + f.isProtected() + ", " + f.isPublic() + ", "
              + f.isStatic() + ", " + f.isTransient() + ", " + f.isVolatile() + ", " + getter + ", " + setter + ", "
              + elementType + ", " + annotations + "), " );

          final SetterGetterStub stub = new SetterGetterStub();
          stub.name = f.getName();
          stub.enclosingType = enclosingType;
          stub.type = fieldType;
          stub.isStatic = f.isStatic();
          stub.isFinal = f.isFinal();
          if ( enclosingType != null && fieldType != null )
          {
            stub.getter = getter;
            stub.setter = setter;
          }
          setterGetterStubs.add( stub );
        }
        pb( "};" );
      }

      printMethods( c, varName, "Method", c.getMethods() );
      if ( c.isPublic() && !c.isAbstract() && ( c.getEnclosingType() == null || c.isStatic() ) )
      {
        printMethods( c, varName, "Constructor", c.getConstructors() );
      }
      else
      {
        logger.log( Type.INFO, c.getName() + " can't be instantiated. Constructors not generated" );
      }

      if ( c.isArray() != null )
      {
        pb( varName + ".componentType = " + getType( c.isArray().getComponentType() ) + ";" );
      }
      if ( c.isEnum() != null )
      {
        final JEnumConstant[] enumConstants = c.isEnum().getEnumConstants();
        if ( enumConstants != null )
        {
          pb( varName + ".enumConstants = new Object[" + enumConstants.length + "];" );
          for ( int i = 0; i < enumConstants.length; i++ )
          {
            pb( varName + ".enumConstants[" + i + "] = " + c.getErasedType().getQualifiedSourceName() + "."
                + enumConstants[ i ].getName() + ";" );
          }
        }
      }

      final Annotation[] annotations = c.getDeclaredAnnotations();
      if ( annotations != null && annotations.length > 0 )
      {
        pb( varName + ".annotations = " + getAnnotations( annotations ) + ";" );
      }
    }
    else if ( t.isAnnotation() != null )
    {
      pb( varName + ".isAnnotation = true;" );
    }
    else
    {
      pb( varName + ".isPrimitive = true;" );
    }

    pb( "types.put(\"" + t.getErasedType().getQualifiedSourceName() + "\", " + varName + ");" );
    return buffer.toString();
  }

  @Nonnull
  private String getAnnotations( @Nullable final Annotation[] annotations )
  {
    if ( annotations != null && annotations.length > 0 )
    {
      int numValidAnnotations = 0;
      final Class<?>[] ignoredAnnotations = { Deprecated.class, Retention.class };
      final StringBuilder b = new StringBuilder();
      b.append( "new java.lang.annotation.Annotation[] {" );
      for ( final Annotation annotation : annotations )
      {
        final Class<?> type = annotation.annotationType();
        // skip ignored types, assuming we are not interested in those at runtime
        boolean ignoredType = false;
        for ( int i = 0; !ignoredType && i < ignoredAnnotations.length; i++ )
        {
          ignoredType = ignoredAnnotations[ i ].equals( type );
        }
        if ( ignoredType )
        {
          continue;
        }
        // skip if not annotated with RetentionPolicy.RUNTIME
        final Retention retention = type.getAnnotation( Retention.class );
        if ( retention == null || retention.value() != RetentionPolicy.RUNTIME )
        {
          continue;
        }
        numValidAnnotations++;
        // anonymous class
        b.append( " new " ).append( type.getCanonicalName() ).append( "() {" );
        // override all methods
        final Method[] methods = type.getDeclaredMethods();
        for ( final Method method : methods )
        {
          final Class<?> returnType = method.getReturnType();
          b.append( " @Override public" );
          b.append( " " ).append( returnType.getCanonicalName() );
          b.append( " " ).append( method.getName() ).append( "() { return" );
          if ( returnType.isArray() )
          {
            b.append( " new " ).append( returnType.getCanonicalName() ).append( " {" );
          }
          // invoke the annotation method
          Object invokeResult = null;
          try
          {
            invokeResult = method.invoke( annotation );
          }
          catch ( final IllegalAccessException e )
          {
            logger.log( Type.ERROR, "Error invoking annotation method." );
          }
          catch ( final InvocationTargetException e )
          {
            logger.log( Type.ERROR, "Error invoking annotation method." );
          }
          // write result as return value
          if ( invokeResult != null )
          {
            if ( returnType.equals( String[].class ) )
            {
              // String[]
              for ( final String s : (String[]) invokeResult )
              {
                b.append( " \"" ).append( s ).append( "\"," );
              }
            }
            else if ( returnType.equals( String.class ) )
            {
              // String
              b.append( " \"" ).append( (String) invokeResult ).append( "\"" );
            }
            else if ( returnType.equals( Class[].class ) )
            {
              // Class[]
              for ( final Class c : (Class[]) invokeResult )
              {
                b.append( " " ).append( c.getCanonicalName() ).append( ".class," );
              }
            }
            else if ( returnType.equals( Class.class ) )
            {
              // Class
              b.append( " " ).append( ( (Class) invokeResult ).getCanonicalName() ).append( ".class" );
            }
            else if ( returnType.isArray() && returnType.getComponentType().isEnum() )
            {
              // enum[]
              final String enumTypeName = returnType.getComponentType().getCanonicalName();
              final int length = Array.getLength( invokeResult );
              for ( int i = 0; i < length; i++ )
              {
                final Object e = Array.get( invokeResult, i );
                b.append( " " ).append( enumTypeName ).append( "." ).append( e.toString() ).append( "," );
              }
            }
            else if ( returnType.isEnum() )
            {
              // enum
              b.append( " " ).append( returnType.getCanonicalName() ).append( "." ).append( invokeResult.toString() );
            }
            else if ( returnType.isArray() && returnType.getComponentType().isPrimitive() )
            {
              // primitive []
              final Class<?> primitiveType = returnType.getComponentType();
              final int length = Array.getLength( invokeResult );
              for ( int i = 0; i < length; i++ )
              {
                final Object n = Array.get( invokeResult, i );
                b.append( " " ).append( n.toString() );
                if ( primitiveType.equals( float.class ) )
                {
                  b.append( "f" );
                }
                b.append( "," );
              }
            }
            else if ( returnType.isPrimitive() )
            {
              // primitive
              b.append( " " ).append( invokeResult.toString() );
              if ( returnType.equals( float.class ) )
              {
                b.append( "f" );
              }
            }
            else
            {
              logger.log( Type.ERROR, "Return type not supported (or not yet implemented)." );
            }
          }
          if ( returnType.isArray() )
          {
            b.append( " }" );
          }
          b.append( "; " );
          b.append( "}" );
        }
        // must override annotationType()
        b.append( " @Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return " );
        b.append( type.getCanonicalName() );
        b.append( ".class; }" );
        b.append( "}, " );
      }
      b.append( "}" );
      return ( numValidAnnotations > 0 ) ? b.toString() : "null";
    }
    return "null";
  }

  private void printMethods( @Nonnull final JClassType c, final String varName, @Nonnull final String methodType, @Nullable final JAbstractMethod[] methodTypes )
  {
    if ( methodTypes != null )
    {
      pb( varName + "." + methodType.toLowerCase() + "s = new " + methodType + "[] {" );
      for ( final JAbstractMethod m : methodTypes )
      {
        final MethodStub stub = new MethodStub();
        stub.isPublic = m.isPublic();
        stub.enclosingType = getType( c );
        if ( m.isMethod() != null )
        {
          stub.isMethod = true;
          stub.returnType = getType( m.isMethod().getReturnType() );
          stub.isStatic = m.isMethod().isStatic();
          stub.isAbstract = m.isMethod().isAbstract();
          stub.isNative = m.isMethod().isAbstract();
          stub.isFinal = m.isMethod().isFinal();

        }
        else
        {
          if ( m.isPrivate() || m.isDefaultAccess() )
          {
            logger.log( Type.INFO, "Skipping non-visible constructor for class " + c.getName() );
            continue;
          }
          if ( m.getEnclosingType().isFinal() && !m.isPublic() )
          {
            logger.log( Type.INFO, "Skipping non-public constructor for final class" + c.getName() );
            continue;
          }
          stub.isConstructor = true;
          stub.returnType = stub.enclosingType;
        }

        stub.jnsi = "";
        stub.methodId = nextId();
        stub.name = m.getName();
        methodStubs.add( stub );

        final String methodAnnotations = getAnnotations( m.getDeclaredAnnotations() );

        pb( "new " + methodType + "(\"" + m.getName() + "\", " );
        pb( stub.enclosingType + ", " );
        pb( stub.returnType + ", " );

        pb( "new Parameter[] {" );
        if ( m.getParameters() != null )
        {
          for ( final JParameter p : m.getParameters() )
          {
            stub.parameterTypes.add( getType( p.getType() ) );
            stub.jnsi += p.getType().getErasedType().getJNISignature();
            pb( "new Parameter(\"" +
                p.getName() +
                "\", " +
                getType( p.getType() ) +
                ", \"" +
                p.getType().getJNISignature()
                +
                "\"), " );
          }
        }
        pb( "}, " );

        pb( stub.isAbstract +
            ", " +
            stub.isFinal +
            ", " +
            stub.isStatic +
            ", " +
            m.isDefaultAccess() +
            ", " +
            m.isPrivate()
            +
            ", " +
            m.isProtected() +
            ", " +
            m.isPublic() +
            ", " +
            stub.isNative +
            ", " +
            m.isVarArgs() +
            ", "
            +
            stub.isMethod +
            ", " +
            stub.isConstructor +
            ", " +
            stub.methodId +
            ", " +
            methodAnnotations +
            ")," );
      }
      pb( "};" );
    }
  }

  @Nonnull
  private String getElementTypes( @Nonnull final JField f )
  {
    final StringBuilder b = new StringBuilder();
    final JParameterizedType params = f.getType().isParameterized();
    if ( params != null )
    {
      final JClassType[] typeArgs = params.getTypeArgs();
      b.append( "new Class[] {" );
      for ( final JClassType typeArg : typeArgs )
      {
				if ( typeArg.isWildcard() != null )
				{
					b.append( "Object.class" );
				}
				else if ( !isVisible( typeArg ) )
				{
					b.append( "null" );
				}
				else if ( typeArg.isClassOrInterface() != null )
				{
					b.append( typeArg.isClassOrInterface().getQualifiedSourceName() ).append( ".class" );
				}
				else if ( typeArg.isParameterized() != null )
				{
					b.append( typeArg.isParameterized().getQualifiedBinaryName() ).append( ".class" );
				}
				else
				{
					b.append( "null" );
				}
        b.append( ", " );
      }
      b.append( "}" );
      return b.toString();
    }
    return "null";
  }

  @Nullable
  private String getType( @Nonnull final JType type )
  {
		if ( !isVisible( type ) )
		{
			return null;
		}
    return type.getErasedType().getQualifiedSourceName() + ".class";
  }

  private void imports( @Nonnull final ClassSourceFileComposerFactory composer )
  {
    composer.addImport( "java.security.AccessControlException" );
    composer.addImport( "java.util.*" );
    composer.addImport( "com.artemis.gwtref.client.*" );
  }

  private void invokeM()
  {
    p( "public Object invoke(Method m, Object obj, Object[] params) {" );
    SwitchedCodeBlock pc = new SwitchedCodeBlock( "m.methodId" );
    int subN = 0;
    int nDispatch = 0;

    for ( final MethodStub stub : methodStubs )
    {
			if ( stub.enclosingType == null )
			{
				continue;
			}
			if ( stub.enclosingType.contains( "[]" ) )
			{
				continue;
			}
			if ( stub.returnType == null )
			{
				continue;
			}
			if ( stub.unused )
			{
				continue;
			}
      boolean paramsOk = true;
      for ( final String paramType : stub.parameterTypes )
      {
        if ( paramType == null )
        {
          paramsOk = false;
          break;
        }
      }

			if ( !paramsOk )
			{
				continue;
			}

      buffer.setLength( 0 );
      pbn( "return m" + stub.methodId + "(" );
      addParameters( stub );
      pbn( ");" );
      pc.add( stub.methodId, buffer.toString() );
      nDispatch++;
      if ( nDispatch > 1000 )
      {
        pc.print();
        pc = new SwitchedCodeBlock( "m.methodId" );
        subN++;
        p( "   return invoke" + subN + "(m, obj, params);" );
        p( "}" );
        p( "public Object invoke" + subN + "(Method m, Object obj, Object[] params) {" );
        nDispatch = 0;
      }
    }

    pc.print();
    p( "   throw new IllegalArgumentException(\"Missing method-stub \" + m.methodId + \" for method \" + m.name);" );
    p( "}" );
  }

  private void addParameters( @Nonnull final MethodStub stub )
  {
		if ( !stub.isStatic && !stub.isConstructor )
		{
			pbn( "(" + stub.enclosingType + ")obj" + ( stub.parameterTypes.size() > 0 ? "," : "" ) );
		}
    for ( int i = 0; i < stub.parameterTypes.size(); i++ )
    {
      pbn( cast( stub.parameterTypes.get( i ), "params[" + i + "]" ) +
           ( i < stub.parameterTypes.size() - 1 ? ", " : "" ) );
    }
  }

  @Nonnull
  private String cast( @Nonnull final String paramType, final String arg )
  {
    if ( paramType.equals( "byte" ) ||
         paramType.equals( "short" ) ||
         paramType.equals( "int" ) ||
         paramType.equals( "long" )
         ||
         paramType.equals( "float" ) ||
         paramType.equals( "double" ) )
    {
      return "((Number)" + arg + ")." + paramType + "Value()";
    }
    else if ( paramType.equals( "boolean" ) )
    {
      return "((Boolean)" + arg + ")." + paramType + "Value()";
    }
    else if ( paramType.equals( "char" ) )
    {
      return "((Character)" + arg + ")." + paramType + "Value()";
    }
    else
    {
      return "((" + paramType + ")" + arg + ")";
    }
  }

  private void setF()
  {
    p( "public void set(Field field, Object obj, Object value) throws IllegalAccessException {" );
    final SwitchedCodeBlock pc = new SwitchedCodeBlock( "field.setter" );
    for ( final SetterGetterStub stub : setterGetterStubs )
    {
			if ( stub.enclosingType == null || stub.type == null || stub.isFinal || stub.unused )
			{
				continue;
			}
      pc.add( stub.setter,
              "s" + stub.setter + "(" + cast( stub.enclosingType, "obj" ) + ", " + cast( stub.type, "value" )
              + "); return;" );
    }
    pc.print();
    p( "   throw new IllegalArgumentException(\"Missing setter-stub \" + field.setter + \" for field \" + field.name);" );
    p( "}" );
  }

  private void getF()
  {
    p( "public Object get(Field field, Object obj) throws IllegalAccessException {" );
    final SwitchedCodeBlock pc = new SwitchedCodeBlock( "field.getter" );
    for ( final SetterGetterStub stub : setterGetterStubs )
    {
			if ( stub.enclosingType == null || stub.type == null || stub.unused )
			{
				continue;
			}
      pc.add( stub.getter, "return g" + stub.getter + "(" + cast( stub.enclosingType, "obj" ) + ");" );
    }
    pc.print();
    p( "   throw new IllegalArgumentException(\"Missing getter-stub \" + field.getter + \" for field \" + field.name);" );
    p( "}" );
  }

  private static boolean isInstantiableWithNewOperator( @Nonnull final JClassType t )
  {
		if ( !t.isDefaultInstantiable() || t instanceof JArrayType || t instanceof JEnumType )
		{
			return false;
		}
    try
    {
      final JConstructor constructor = t.getConstructor( new JType[ 0 ] );
      return constructor != null && constructor.isPublic();
    }
    catch ( final NotFoundException e )
    {
      return false;
    }
  }

  private void setArrayElementT()
  {
    p( "public void setArrayElement(Type type, Object obj, int i, Object value) {" );
    final SwitchedCodeBlock pc = new SwitchedCodeBlock( "type.id" );

    for ( final String s : PRIMITIVE_TYPES )
    {
			if ( !typeNames2typeIds.containsKey( s + "[]" ) )
			{
				continue;
			}
      pc.add( typeNames2typeIds.get( s + "[]" ), "((" + s + "[])obj)[i] = " + cast( s, "value" ) + "; return;" );
    }

    pc.print();
    p( "	((Object[])obj)[i] = value;" );
    p( "}" );
  }

  private void getArrayElementT()
  {
    p( "public Object getArrayElement(Type type, Object obj, int i) {" );
    final SwitchedCodeBlock pc = new SwitchedCodeBlock( "type.id" );

    for ( final String s : PRIMITIVE_TYPES )
    {
			if ( !typeNames2typeIds.containsKey( s + "[]" ) )
			{
				continue;
			}
      pc.add( typeNames2typeIds.get( s + "[]" ), "return ((" + s + "[])obj)[i];" );
    }

    pc.print();
    p( "	return ((Object[])obj)[i];" );
    p( "}" );
  }

  private void getArrayLengthT()
  {
    p( "public int getArrayLength(Type type, Object obj) {" );
    final SwitchedCodeBlock pc = new SwitchedCodeBlock( "type.id" );

    for ( final String s : PRIMITIVE_TYPES )
    {
			if ( !typeNames2typeIds.containsKey( s + "[]" ) )
			{
				continue;
			}
      pc.add( typeNames2typeIds.get( s + "[]" ), "return ((" + s + "[])obj).length;" );
    }

    pc.print();
    p( "	return ((Object[])obj).length;" );
    p( "}" );
  }

  private void newArrayC()
  {
    p( "public Object newArray (Class componentType, int size) {" );
    p( "	Type t = forName(componentType.getName().replace('$', '.'));" );
    p( "	if (t != null) {" );
    final SwitchedCodeBlock pc = new SwitchedCodeBlock( "t.id" );
    for ( final JType type : types )
    {
			if ( type.getQualifiedSourceName().equals( "void" ) )
			{
				continue;
			}
			if ( type.getQualifiedSourceName().endsWith( "Void" ) )
			{
				continue;
			}
      String arrayType = type.getErasedType().getQualifiedSourceName() + "[size]";
      if ( arrayType.contains( "[]" ) )
      {
        arrayType = type.getErasedType().getQualifiedSourceName();
        arrayType = arrayType.replaceFirst( "\\[\\]", "[size]" ) + "[]";
      }
      pc.add( typeNames2typeIds.get( type.getQualifiedSourceName() ), "return new " + arrayType + ";" );
    }
    pc.print();
    p( "	}" );
    p( "	throw new RuntimeException(\"Couldn't create array with element type \" + componentType.getName());" );
    p( "}" );
  }

  private void forNameC()
  {
    p( "public Type forName(String name) {" );
    p( "	return types.get(name);" );
    p( "}" );
  }

  private void getKnownTypesC()
  {
    p( "public Collection<Type> getKnownTypes() {" );
    p( "	return types.values();" );
    p( "}" );
  }

  void p( final String line )
  {
    sw.println( line );
    source.append( line );
    source.append( "\n" );
  }

  void pn( final String line )
  {
    sw.print( line );
    source.append( line );
  }

  @Nonnull
  final
  StringBuffer buffer = new StringBuffer();

  void pb( final String line )
  {
    buffer.append( line );
    buffer.append( "\n" );
  }

  private void pbn( final String line )
  {
    buffer.append( line );
  }

  class SwitchedCodeBlock
  {
    @Nonnull
    private final List<KeyedCodeBlock> blocks = new ArrayList<>();
    private final String switchStatement;

    SwitchedCodeBlock( final String switchStatement )
    {
      this.switchStatement = switchStatement;
    }

    void add( final int key, final String codeBlock )
    {
      final KeyedCodeBlock b = new KeyedCodeBlock();
      b.key = key;
      b.codeBlock = codeBlock;
      blocks.add( b );
    }

    void print()
    {
			if ( blocks.isEmpty() )
			{
				return;
			}

      p( "	switch(" + switchStatement + ") {" );
      for ( final KeyedCodeBlock b : blocks )
      {
        p( "	case " + b.key + ": " + b.codeBlock );
      }
      p( "}" );
    }

    class KeyedCodeBlock
    {
      int key;
      String codeBlock;
    }
  }
}
