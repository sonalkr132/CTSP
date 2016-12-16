import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class ChromosomeTest {
  private Chromosome ch;
  private FacilitesStub f_stub;
  private CustomersAllocationStub ca_stub;
  private int PRIZE = 20;
  @Before
  public void setUp() throws Exception {
    ca_stub = new CustomersAllocationStub();
    f_stub = new FacilitesStub();

    ch = new Chromosome(ca_stub, f_stub, PRIZE);
  }

  @Test
  public void test_constructor(){
    int expected_prize = 0;
    for(int gene : ch.genes) expected_prize += ca_stub.customers_per_facility[gene];

    assertTrue(ch.collected_prize > PRIZE);
    assertEquals(ch.collected_prize, expected_prize);
    assertTrue(ch.score > 0.0);
  }
  
  @Test
  public void test_copy_constrctor(){
    Chromosome copy_ch = new Chromosome(ch.score, ch.collected_prize, ch.genes, ch.facilites);
    
    ch.genes.clear();
    ch.collected_prize = 0;
    ch.score = 0.0;
    assertFalse(copy_ch.genes.size() == ch.genes.size());
    assertFalse(copy_ch.collected_prize == ch.collected_prize);
    assertFalse(copy_ch.score == ch.score);
    assertNotNull(copy_ch.facilites);
  }

  @Test
  public void test_add_next_facility(){
    ch.genes.clear();
    ch.facilites_set.clear();
    ch.facilites_set.add(2);
    ch.facilites_set.add(3);
    
    int facility = ch.add_next_facility();
    assertNotNull(facility);
    assertEquals(1, ch.genes.size());
    assertEquals(1, ch.facilites_set.size());
  }
  
  @Test
  public void test_recover(){
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(3, 2, 3, 2));
    
    ch.recover(PRIZE, ca_stub);
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(3, 2, 4));
    assertTrue(ch.genes.equals(expected_genes));
    assertEquals(ch.score, 55.0, 0.001);
    assertEquals(ch.collected_prize, 21);
  }
  
  @Test
  public void test_evaluate(){
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(1, 2, 3, 4));
    double score = ch.evaluate(f_stub.map, f_stub.depot_dist);
    
    assertEquals(49.0, score, 0.001);
  }
  
  @Test
  public void test_remove_duplicates_when_genes_has_duplicates(){
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(3, 2, 3, 2));
    ch.remove_duplicates();
    
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(3, 2));
    assertTrue(ch.genes.equals(expected_genes));
  }
  
  @Test
  public void test_remove_duplicates_when_genes_does_not_have_duplicates(){
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(3, 2, 4));
    ch.remove_duplicates();
    
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(3, 2, 4));
    assertTrue(ch.genes.equals(expected_genes));
  }
  
  @Test
  public void test_unvisited_facilites(){
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(3, 2, 4));
    ArrayList<Integer> unvisited_facilites = ch.unvisited_facilites();
    
    ArrayList<Integer> expected_facilites = new ArrayList<Integer>(Arrays.asList(0, 1));
    assertTrue(unvisited_facilites.equals(expected_facilites));
  }
}
