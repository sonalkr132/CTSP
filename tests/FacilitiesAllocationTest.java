import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FacilitiesAllocationTest {
  private FacilitiesAllocation fa;
  private int number_of_facilites = 5;

  @Before
  public void setUp() throws Exception {
    fa = new FacilitiesAllocationStub();
  }
  
  @Test
  public void test_facility_coverage_adds_to_total_customers() {
    //ca.allocate_customers();
    
    int facility_sum = 0, allocation_sum = 0;
    for(int i = 0; i < number_of_facilites; i++) facility_sum += fa.facility_coverage[i];
    for(int i = 0; i < number_of_facilites; i++){
      for(int j = 0; j < number_of_facilites; j++) allocation_sum += fa.allocation[i][j];
    }
 
    //each facility covers three others
    assertEquals(3 * number_of_facilites, facility_sum);
    assertEquals(3 *number_of_facilites, allocation_sum);
  }
  
  @Test
  public void copy_constructor(){
    //ca.allocate_customers();
    FacilitiesAllocation copy_fa = new FacilitiesAllocation(fa);
    fa.facility_coverage[0] = 0;
    assertNotEquals(copy_fa.facility_coverage[0], 0);
    fa.facilities_set[0] = 1;
    assertTrue(copy_fa.facilities_set[0] != fa.facilities_set[0]);
    fa.allocation[0][0] = 2;
    assertTrue(copy_fa.allocation[0][0] != fa.allocation[0][0]);
  }
}
