import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PopulationTest {
  private Population p;
  @Before
  public void setUp() throws Exception {
    p = new Population(5, 30, 30, 20);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testPopulation() {
    fail("Not yet implemented");
  }

  @Test
  public void test_fixed_point_facilites() {
    p.fixed_point_facilites();
    assertNotNull(p.facilites);
  }

  @Test
  public void testRandom_point_facilites() {
    fail("Not yet implemented");
  }

  @Test
  public void testTsplib_cities() {
    fail("Not yet implemented");
  }

  @Test
  public void test_allocate_customers() {
    p.allocate_customers();
    assertNotNull(p.cust_allocation);
  }

  @Test
  public void testAllocate_fixed_customers() {
    fail("Not yet implemented");
  }

  @Test
  public void testInitialize_population() {
    fail("Not yet implemented");
  }

  @Test
  public void testNext_generation() {
    fail("Not yet implemented");
  }

  @Test
  public void testBest_score_fraction() {
    fail("Not yet implemented");
  }

}
