package org.assertj.neo4j.api.indexdefinition;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.schema.IndexDefinition;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.assertj.neo4j.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class IndexDefinitionAssert_hasName_Test {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private IndexDefinition indexDefinition = mock(IndexDefinition.class);

  @Test
  public void should_fail_if_api_version_is_too_old() {
    expectedException.expect(AssertionError.class);
    expectedException.expectMessage("\nExpecting actual to define method called \"getName\" (requires Neo4j \"3.5.0\" or later)");

    assertThat(indexDefinition).hasName("name");
  }

  @Test
  public void should_fail_if_name_does_not_match() {
    class IndexDefinitionDelegate {
      public String getNameAgain() {
        return "name";
      }
    }
    Class<?> type = new ByteBuddy()
      .rebase(IndexDefinition.class)
      .defineMethod("getNameAgain", String.class, Visibility.PUBLIC)
      .intercept(MethodDelegation.to(IndexDefinitionDelegate.class))
      .method(named("getName"))
      .intercept(SuperMethodCall.INSTANCE
        .andThen(MethodCall.invoke(named("getNameAgain"))))
      .make()
      .load(IndexDefinition.class.getClassLoader(),
        ClassLoadingStrategy.Default.CHILD_FIRST)
      .getLoaded();

    /*Object instance = type.newInstance();*/
  }
}
