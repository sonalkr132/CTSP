import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FacilitesTest {
  private Facilites facility;

  @Before
  public void setUp() throws Exception {
    facility = new Facilites(5);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testFacilites() {
    fail("Not yet implemented");
  }

  @Test
  public void testGen_random_facilites() {
    fail("Not yet implemented");
  }

  @Test
  public void test_load_fixed_distances() {
    facility.load_fixed_distances();
    double []expected_map = new double[]{1000000, 26, 12, 9, 4};
    double []expected_depot_dist = new double[] {12,  26, 9, 21, 4};
    
    assertArrayEquals(facility.map[0], expected_map, 0.001);
    assertArrayEquals(facility.depot_dist, expected_depot_dist, 0.001);
  }

  @Test
  public void testLoad_tsplib() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoad_tsplib_points() {
    fail("Not yet implemented");
  }

}
