package com.mashreq;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This extension is used to clean up the database after ALL the tests have been run. When running
 * in parallel, each test suite will call afterAll and because the database is shared between the
 * tests, we need to keep it until the end of the tests. ExtensionContext.Store.CloseableResource
 * allows to perform the close only once after all the tests.
 */
public class CleanDatabaseExtension
    implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

  private EntityManagerFactory entityManagerFactory;

  @Override
  public void beforeAll(ExtensionContext context) {
    // Stores emf for future reference
    ApplicationContext springContext = SpringExtension.getApplicationContext(context);
    entityManagerFactory = springContext.getBean(EntityManagerFactory.class);
    // Adds something in the store so that close() method is called at the end of ALL tests
    context
        .getRoot()
        .getStore(ExtensionContext.Namespace.GLOBAL)
        .put("CleanDatabaseExtension", this);
  }

  @Override
  public void close() {
    var em = entityManagerFactory.createEntityManager();
    var transaction = em.getTransaction();
    transaction.begin();

    // need to truncate all at the same time due to foreign key constraint
    // or user DELETE statements per entity
    em.createNativeQuery(
        "TRUNCATE TABLE users").executeUpdate();
    //em.createQuery("DELETE FROM User").executeUpdate();

    transaction.commit();
    em.close();
  }
}
