import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomersAllocationTest {
  private CustomersAllocation ca;
  private int number_of_customers = 30;
  private int number_of_facilites = 5;

  @Before
  public void setUp() throws Exception {
    ca = new CustomersAllocation(number_of_customers, number_of_facilites);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCustomersAllocation() {
    fail("Not yet implemented");
  }

  @Test
  public void test_customer_per_facility_adds_to_total_customers() {
    ca.allocate_customers();
    
    int facility_sum = 0, allocation_sum = 0;
    for(int i = 0; i < number_of_facilites; i++) facility_sum += ca.customers_per_facility[i];
    for(int i = 0; i < number_of_facilites; i++){
      for(int j = 0; j < number_of_customers; j++) allocation_sum += ca.allocation[i][j];
    }

    assertEquals(number_of_customers, facility_sum);
    assertEquals(number_of_customers, allocation_sum);
  }

  @Test
  public void testAllocate_fixed_customers() {
    fail("Not yet implemented");
  }

  @Test
  public void testSort() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoad_tsplib_points() {
    fail("Not yet implemented");
  }

}
