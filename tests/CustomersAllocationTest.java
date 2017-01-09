import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CustomersAllocationTest {
  private FacilitiesAllocation ca;
  private int number_of_customers = 30;
  private int number_of_facilites = 5;

  @Before
  public void setUp() throws Exception {
    ca = new FacilitiesAllocation(number_of_facilites);
  }
  
  @Test
  public void test_customer_per_facility_adds_to_total_customers() {
    //ca.allocate_customers();
    
    int facility_sum = 0, allocation_sum = 0;
    for(int i = 0; i < number_of_facilites; i++) facility_sum += ca.facility_coverage[i];
    for(int i = 0; i < number_of_facilites; i++){
      for(int j = 0; j < number_of_customers; j++) allocation_sum += ca.allocation[i][j];
    }

    assertEquals(number_of_customers, facility_sum);
    assertEquals(number_of_customers, allocation_sum);
  }
  
  @Test
  public void copy_constructor(){
    //ca.allocate_customers();
    FacilitiesAllocation copy_ca = new FacilitiesAllocation(ca);
    ca.facility_coverage[0] = 0;
    assertNotEquals(copy_ca.facility_coverage[0], 0);
    ca.facilities_set[0] = 1;
    assertTrue(copy_ca.facilities_set[0] != ca.facilities_set[0]);
    ca.allocation[0][0] = 2;
    assertTrue(copy_ca.allocation[0][0] != ca.allocation[0][0]);
  }
}
