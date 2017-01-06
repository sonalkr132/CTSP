import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class PopulationTest {
  private Population p;
  private FacilitesStub f_stub;
  private CustomersAllocationStub ca_stub;

  @Before
  public void setUp() throws Exception {
    ca_stub = new CustomersAllocationStub();
    f_stub = new FacilitesStub();
    p = new Population(5, 30, 30, 20);
  }

  @Test
  public void test_fixed_point_facilites() {
    p.fixed_point_facilites();
    assertNotNull(p.facilites);
  }

  @Test
  public void test_allocate_customers() {
    p.allocate_random_customers();
    assertNotNull(p.cust_allocation);
  }
  
  @Test
  public void test_initialize_population(){
    p.cust_allocation = ca_stub;
    p.facilites = f_stub;
    p.initialize_population();
    for(Chromosome c: p.chromosomes) assertNotNull(c);
    assertNotNull(p.current_best_chromosome);
    assertNotNull(p.alltime_best_chromosome);
    assertTrue(p.best_score > 0.0);
  }
  
  @Test
  public void test_copy_genes(){
    p.cust_allocation = ca_stub;
    p.facilites = f_stub;
    ArrayList<Integer> genes1 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
    ArrayList<Integer> genes2 = new ArrayList<Integer>(Arrays.asList(3, 4, 1));
    Chromosome parent1 = new Chromosome(0.0, 0, genes1, f_stub, ca_stub);
    Chromosome parent2 = new Chromosome(0.0, 0, genes2, f_stub, ca_stub);
    
    p.copy_genes(parent1, parent2, 1);
    
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(1, 4, 1));
    assertTrue(parent2.genes.equals(expected_genes));
    
    expected_genes = new ArrayList<Integer>(Arrays.asList(3, 4));
    assertTrue(parent1.genes.equals(expected_genes));
  }
}
