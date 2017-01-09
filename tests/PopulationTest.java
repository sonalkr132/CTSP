import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class PopulationTest {
  private Population p;
  private FacilitiesStub f_stub;
  private FacilitiesAllocationStub ca_stub;

  @Before
  public void setUp() throws Exception {
    ca_stub = new FacilitiesAllocationStub();
    f_stub = new FacilitiesStub();
    p = new Population(30, f_stub, ca_stub);
  }

  @Test
  public void test_initialize_population(){
    p.initialize_population();
    for(Chromosome c: p.chromosomes) assertNotNull(c);
    assertNotNull(p.current_best_chromosome);
    assertNotNull(p.alltime_best_chromosome);
    assertTrue(p.best_score > 0.0);
  }
  
  @Test
  public void test_copy_genes(){
    p.f_allocation = ca_stub;
    p.facilities = f_stub;
    ArrayList<Integer> genes1 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
    int []v1 = {0, 1, 1, 1, 1};
    ArrayList<Integer> genes2 = new ArrayList<Integer>(Arrays.asList(3, 4, 1));
    int []v2 = {0, 1, 0, 1, 1};
    Chromosome parent1 = new Chromosome(0.0, genes1, ca_stub, f_stub, v1);
    Chromosome parent2 = new Chromosome(0.0, genes2, ca_stub, f_stub, v2);
    
    p.copy_genes(parent1, parent2, 1, 3);
    
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(1, 4, 1));
    assertTrue(parent2.genes.equals(expected_genes));
    
    expected_genes = new ArrayList<Integer>(Arrays.asList(3, 2, 3, 4));
    assertTrue(parent1.genes.equals(expected_genes));
  }
}
