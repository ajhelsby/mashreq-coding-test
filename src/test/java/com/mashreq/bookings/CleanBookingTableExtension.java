package com.mashreq;

import com.mashreq.bookings.CleanBookingTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class CleanBookingTableExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    // Retrieve the current test method
    var testMethod = context.getTestMethod().orElseThrow();

    // Retrieve the annotation from the test method
    var cleanTableAnnotation = testMethod.getAnnotation(CleanBookingTable.class);


    if (cleanTableAnnotation != null) {
      String tableName = cleanTableAnnotation.tableName();
      ApplicationContext springContext = SpringExtension.getApplicationContext(context);
      EntityManagerFactory entityManagerFactory = springContext.getBean(EntityManagerFactory.class);

      try (EntityManager em = entityManagerFactory.createEntityManager()) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.createNativeQuery("DELETE FROM " + tableName).executeUpdate();
        transaction.commit();
      }
    }
  }
}
