import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FacilitesTest {
  private Facilites facility;

  @Before
  public void setUp() throws Exception {
    facility = new Facilites(5);
    facility.load_fixed_distances();
  }

  @Test
  public void test_load_fixed_distances() {
    double []expected_map = new double[]{1000000, 26, 12, 9, 4};
    double []expected_depot_dist = new double[] {12,  26, 9, 21, 4};
    
    assertArrayEquals(facility.map[0], expected_map, 0.001);
    assertArrayEquals(facility.depot_dist, expected_depot_dist, 0.001);
  }
  
  @Test
  public void test_find_nearest_facility(){
    int idx = facility.find_nearest_facility(0);
    assertEquals(4, idx);
  }
}
