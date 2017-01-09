import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class ChromosomeTest {
  private Chromosome empty_ch;
  private FacilitiesStub f_stub;
  private FacilitiesAllocationStub fa_stub;
  private int PRIZE = 20;
  @Before
  public void setUp() throws Exception {
    fa_stub = new FacilitiesAllocationStub();
    f_stub = new FacilitiesStub();

    empty_ch = new Chromosome(
        0.0,
        new ArrayList<Integer>(),
        new FacilitiesAllocationStub(),
        new FacilitiesStub(),
        new int[5]);
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  //                             constructor                                               //
  ///////////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void test_constructor(){
    Chromosome ch = new Chromosome(fa_stub, f_stub);
    assertTrue(ch.covered_facilities > PRIZE);
    assertTrue(ch.score > 0.0);
    assertTrue(ch.genes.size() < 6);
  }
  
  @Test
  public void test_copy_constrctor(){
    Chromosome ch = new Chromosome(fa_stub, f_stub);
    Chromosome copy_ch = new Chromosome(ch);
    
    ch.genes.clear();
    ch.covered_facilities = 0;
    ch.score = 0.0;
    assertFalse(copy_ch.genes.size() == ch.genes.size());
    assertFalse(copy_ch.covered_facilities == ch.covered_facilities);
    assertFalse(copy_ch.score == ch.score);
    assertNotNull(copy_ch.facilities);
  }

  ///////////////////////////////////////////////////////////////////////////////////////////
  //                             add_next_facility                                         //
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  @Test
  public void test_add_next_facility_when_empty(){
    empty_ch.add_next_facility();
    int []row0 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    assertEquals(1, empty_ch.genes.size());
    
    int added_facility = empty_ch.genes.get(0);
    assertArrayEquals(row0, empty_ch.fallocation.allocation[added_facility]);
    assertEquals(0, empty_ch.fallocation.facility_coverage[added_facility]);
    assertEquals(fa_stub.facility_coverage[added_facility], empty_ch.covered_facilities);
  }
  
  @Test
  public void test_add_next_facility_when_genes_not_empty(){
    empty_ch.genes.add(2);
    empty_ch.fallocation.facility_coverage[2] = 0;
    empty_ch.covered_facilities = 8;
    for(int i = 0; i < empty_ch.fallocation.number_of_facilities; i++)
      empty_ch.fallocation.allocation[2][i] = 0; 
    
    empty_ch.add_next_facility();
    int added_facility = empty_ch.genes.get(1);
    int []row0 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int expected_prize = 8 + fa_stub.facility_coverage[added_facility];
 
    assertEquals(2, empty_ch.genes.size());
    assertArrayEquals(row0, empty_ch.fallocation.allocation[added_facility]);
    assertEquals(expected_prize, empty_ch.covered_facilities);
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  //                                recover(                                               //
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  @Test
  public void test_recover(){
    Chromosome ch = new Chromosome(fa_stub, f_stub);
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(3, 2, 3, 2));
    
    ch.recover(fa_stub);
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(3, 2, 4));
    assertTrue(ch.genes.equals(expected_genes));
    assertEquals(ch.score, 55.0, 0.001);
    assertEquals(ch.covered_facilities, 21);
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  //                                evaluate                                               //
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  @Test
  public void test_evaluate(){
    empty_ch.genes.addAll(Arrays.asList(1, 2, 3, 4));
    //double score = empty_ch.evaluate(f_stub.map, f_stub.depot_dist);
    
    //assertEquals(49.0, score, 0.001);
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  //                             remove_duplicates                                         //
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  @Test
  public void test_remove_duplicates_when_genes_has_duplicates(){
    Chromosome ch = new Chromosome(fa_stub, f_stub);
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(3, 2, 3, 2));
    FacilitiesAllocation original_ca = new FacilitiesAllocationStub();
    ch.recover_validity(original_ca);
    
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(3, 2));
    assertTrue(ch.genes.equals(expected_genes));
    int []row0 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    assertArrayEquals(row0, ch.fallocation.allocation[3]);
    assertArrayEquals(row0, ch.fallocation.allocation[2]);
    int []row = {0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    assertArrayEquals(row, ch.fallocation.allocation[0]);
    assertEquals(14, ch.covered_facilities);
  }
  
  @Test
  public void test_remove_duplicates_when_genes_does_not_have_duplicates(){
    Chromosome ch = new Chromosome(fa_stub, f_stub);
    ch.genes.clear();
    ch.genes.addAll(Arrays.asList(3, 2, 4));
    FacilitiesAllocation original_ca = new FacilitiesAllocationStub();
    ch.recover_validity(original_ca);
    
    ArrayList<Integer> expected_genes = new ArrayList<Integer>(Arrays.asList(3, 2, 4));
    assertTrue(ch.genes.equals(expected_genes));
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  //                             unvisited_facilites                                       //
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  @Test
  public void test_unvisited_facilites_when_empty(){
    ArrayList<Integer> unvisited_facilites = empty_ch.unvisited_facilities();
    
    ArrayList<Integer> expected_facilites = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));
    assertTrue(unvisited_facilites.equals(expected_facilites));
  }
  
  @Test
  public void test_unvisited_facilities_when_genes_is_not_empty(){
    empty_ch.genes.add(2); empty_ch.fallocation.facility_coverage[2] = 0;
    empty_ch.fallocation.facility_coverage[0] = 0;
    
    ArrayList<Integer> unvisited_facilites = empty_ch.unvisited_facilities();
    ArrayList<Integer> expected_facilites = new ArrayList<Integer>(Arrays.asList(1, 3, 4));
    assertTrue(unvisited_facilites.equals(expected_facilites));
  }
  
  @Test
  public void test_unvisited_facilities_when_genes_has_all_facilites(){
    empty_ch.genes = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4));
    
    ArrayList<Integer> unvisited_facilites = empty_ch.unvisited_facilities();
    assertEquals(0, unvisited_facilites.size());
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  //                             check_better_opt                                          //
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  @Test
  public void test_check_better_opt_when_better_found(){
    empty_ch.genes.addAll(Arrays.asList(3, 2, 0, 1));
    //empty_ch.score = empty_ch.evaluate(f_stub.map, f_stub.depot_dist);
    ArrayList<Integer> expected_facilites = new ArrayList<Integer>(Arrays.asList(3, 0, 2, 1));

    assertTrue(empty_ch.check_better_opt(1, 2));
    assertTrue(empty_ch.genes.equals(expected_facilites));
  }
  
  @Test
  public void test_check_better_opt_when_better_is_not_found(){
    empty_ch.genes.addAll(Arrays.asList(3, 0, 2, 1));
    //empty_ch.score = empty_ch.evaluate(f_stub.map, f_stub.depot_dist);
    ArrayList<Integer> expected_facilites = new ArrayList<Integer>(Arrays.asList(3, 0, 2, 1));

    assertFalse(empty_ch.check_better_opt(1, 2));
    assertTrue(empty_ch.genes.equals(expected_facilites));
  }
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  //                        adding_and_removing_facility                                   //
  ///////////////////////////////////////////////////////////////////////////////////////////
  
  @Test
  public void test_adding_and_removing_facility(){
    empty_ch.add_facility(0, 3);
    
    assertEquals(0, empty_ch.fallocation.facility_coverage[3]);
    assertEquals(6, empty_ch.covered_facilities);
    
    empty_ch.remove_facility(0, fa_stub);
    assertEquals(6, empty_ch.fallocation.facility_coverage[3]);
    assertEquals(0, empty_ch.covered_facilities);
    
    for(int i = 0; i < fa_stub.number_of_facilities; i++){
      assertArrayEquals(empty_ch.fallocation.allocation[i], fa_stub.allocation[i]);
    }
  }
  
   ///////////////////////////////////////////////////////////////////////////////////////////
   //                                   facility_exchange_cost                              //
   ///////////////////////////////////////////////////////////////////////////////////////////
  
   @Test
   public void test_facility_exchange_cost(){
     empty_ch.add_facility(0, 3);
     empty_ch.add_facility(1, 1);
     empty_ch.add_facility(2, 4);
 
     double dist = empty_ch.facility_exchange_cost(1, 2, 0);
     assertEquals(-23.0, dist, 0.001);
   }
}
