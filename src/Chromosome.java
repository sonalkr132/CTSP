import java.util.ArrayList;
import java.util.*;

import java.util.Random;

public class Chromosome {
  public double score;
  public int covered_facilities;
  public ArrayList<Integer> genes;
  public FacilitiesAllocation fallocation;
  final Facilities facilities;
  int []visited_facilities;
  int ITR_LIMIT = 100;

  Chromosome(FacilitiesAllocation cust_allocation, Facilities _facilities){
    genes = new ArrayList<Integer>();
    facilities = _facilities;
    fallocation = new FacilitiesAllocation(cust_allocation);
    visited_facilities = new int[facilities.no_of_facilities];
 
    covered_facilities = 0;
    while(covered_facilities < facilities.no_of_facilities) add_next_facility();

    shuffle_array(genes);
    score = evaluate(facilities.map);
  }
  
  Chromosome(Chromosome c){
    score = c.score;
    covered_facilities = c.covered_facilities;
    genes = new ArrayList<Integer>(c.genes);
    facilities = c.facilities;
    visited_facilities = c.visited_facilities.clone();
    fallocation = new FacilitiesAllocation(c.fallocation);
  }
  
  public void add_next_facility(){
    //find facilities with at least one customers allocation
    int f;
    ArrayList<Integer> unvisited_facilities = unvisited_facilities();
    Random rand = new Random();
    f = rand.nextInt(unvisited_facilities.size());
    add_facility(genes.size(), unvisited_facilities.get(f));
  }
  
  public void add_facility(int f_idx, int f){
    for(int i = 0; i < fallocation.number_of_facilities; i++){
      if(fallocation.allocation[f][i] == 1){
        //found a customer allocation to facility f
        covered_facilities++;
        for(int j = 0; j < fallocation.number_of_facilities; j++){
          if(fallocation.allocation[j][i] == 1){
            fallocation.facility_coverage[j]--;
            fallocation.allocation[j][i] = 0;
          }
        }
      }
    }
    visited_facilities[f] = 1;
    genes.add(f_idx, f);
  }
  
  //Evaluates the total sum of distances between the genes/points of the chromosome
  // @param dist 2D array of distance between all the points
  public double evaluate(double[][] dist){
    int size = genes.size();
    double sum = dist[genes.get(0)][genes.get(size - 1)];
    for(int i = 1; i < size; i++){
      sum += dist[genes.get(i - 1)][genes.get(i)];
    }
    return sum;
  }
  
  public int find_facility_index(int facility){
    for(int i = 0 ; i < genes.size(); i++) {
      if(genes.get(i) == facility) return i;
    }
    
    return genes.size()/2;
  }
  
  private void shuffle_array(ArrayList<Integer> a) {
    int n = a.size() ;
    Random random = new Random();
    random.nextInt();
    for (int i = 0; i < n; i++) {
      int change = i + random.nextInt(n - i);
      swap(a, i, change);
    }
  }
  
  public void recover(FacilitiesAllocation ca){
    remove_duplicates(ca);
    ArrayList<Integer> unvisited_facilites = unvisited_facilities();
    unvisited_facilites = ca.sort(unvisited_facilites);

    while(covered_facilities < facilities.no_of_facilities){
      int facility = unvisited_facilites.remove(0);
      add_facility(genes.size(), facility);
    }
    score = evaluate(facilities.map);
  }
  
  public void remove_duplicates(FacilitiesAllocation original_fa){
    ArrayList<Integer> dup = new ArrayList<Integer>();
    Iterator<Integer> iterator = genes.iterator();
    while(iterator.hasNext()){
      int gene = (int) iterator.next();
      if(!dup.contains(gene)) dup.add(gene);
    }
    
    genes.clear();
    fallocation = new FacilitiesAllocation(original_fa);
    covered_facilities = 0;
    for(int gene : dup) add_facility(genes.size(), gene);
  }
  
  public void two_opt(){
    Random rand = new Random();
    int opt1, opt2, range = genes.size();

    for(int i = 0; i < ITR_LIMIT; i++){
      opt1 = rand.nextInt(range);
      opt2 = rand.nextInt(range);
      if(opt1 == opt2) continue;
      if(check_better_opt(opt1, opt2)) i = 0;
    }
  }
  
  public boolean check_better_opt(int opt1, int opt2){
    swap(genes, opt1, opt2);
    double cur_score = evaluate(facilities.map);
    if(cur_score >= score){
      swap(genes, opt1, opt2);
      return false;
    } else {
      //better solution was found
      //original genes are already swapped
      score = cur_score;
      return true;
    }
  }
  
  private static void swap(ArrayList<Integer> list, int firstInd, int secondInd ){
    int temp = list.set( firstInd, list.get( secondInd ) ) ;
    list.set( secondInd, temp ) ;
  }
  
  public void drop_and_procedures(FacilitiesAllocation ca){
    Random rand = new Random();
    int r_idx = rand.nextInt(genes.size());
    
    int r_facility = genes.get(r_idx);
    remove_facility(r_idx, ca);
    ArrayList<Integer> unvisited_facilites = unvisited_facilities();
    
    double min_gain = 0.0;
    int min_itr_facility = 0;
    for(int i = 0; i < unvisited_facilites.size(); i++){
      int itr_facility = unvisited_facilites.get(i);
      int itr_cost = fallocation.facility_coverage[itr_facility];
      
      if(covered_facilities + itr_cost >= facilities.no_of_facilities){
        //feasible solution
        //we will try to see if score improves
        double dist = facility_exchange_cost(r_idx, r_facility, itr_facility);
        
        if(dist > min_gain){
          //score would improve
          min_gain = dist;
          min_itr_facility = itr_facility;
        }
      }
    }
    if(min_gain > 0){
      score -= min_gain;
      add_facility(r_idx, min_itr_facility);
    } else add_facility(r_idx, r_facility);
  }
  
  public double facility_exchange_cost(int r_idx, int r_facility, int itr_facility){
    int prev_facility = get_prev_facility(r_idx);
    int next_facility = get_next_facility(r_idx);

    return facilities.map[prev_facility][r_facility] + facilities.map[r_facility][next_facility]
            - facilities.map[prev_facility][itr_facility] - facilities.map[itr_facility][next_facility];
  }
  
  private int get_prev_facility(int idx){
    if(idx == 0) return genes.get(genes.size() - 1);
    else return genes.get(idx - 1);
  }
  
  // facility was removed at idx. current facility at idx
  // is next facility
  private int get_next_facility(int idx){
    if(idx == genes.size()) return genes.get(0);
    else return genes.get(idx);
  }

  public ArrayList<Integer> unvisited_facilities(){
    ArrayList<Integer> unvisited_facilities = new ArrayList<Integer> ();
    for(int i = 0; i < facilities.no_of_facilities; i++) {
      if(!genes.contains(i) && fallocation.facility_coverage[i] > 0){
        unvisited_facilities.add(i);
      }
    }
    
    return unvisited_facilities;
  }
  
  public void mutate(FacilitiesAllocation cust_allocation){
    Random rand = new Random();
    ArrayList<Integer> prev_genes = new ArrayList<Integer>(genes);
    FacilitiesAllocation prev_fallocation = new FacilitiesAllocation(fallocation);
    double prev_score = score;
    int r_idx = rand.nextInt(genes.size());
    int r_facility = genes.get(r_idx);
    
    remove_facility(r_idx, cust_allocation);
    ArrayList<Integer> unvisited_facilities = unvisited_facilities();
    unvisited_facilities.remove(Integer.valueOf(r_facility));

    double removal_cost = facilities.map[get_prev_facility(r_idx)][r_facility] + facilities.map[r_facility][get_next_facility(r_idx)];
    score -= removal_cost;
    
    unvisited_facilities = facilities.sort(r_facility, unvisited_facilities);
    
    for(int i = 0; i < unvisited_facilities.size() && covered_facilities < facilities.no_of_facilities; i++){
      int itr_facility = unvisited_facilities.get(i);
      
      double min_dist = 10000.0;
      int min_itr_facility = 0;
      int min_itr_idx = 0;
      int clone_size = genes.size();
      
      //find minimum insertion cost point
      for(int j = 0; j < clone_size; j++) {
        int next_facility = get_next_facility(j);
        int prev_faciitity = get_prev_facility(j);
        double itr_dist = facilities.map[prev_faciitity][itr_facility]
            + facilities.map[itr_facility][next_facility];
        
        if(itr_dist < min_dist){
          min_dist = itr_dist;
          min_itr_facility = itr_facility;
          min_itr_idx = j;
        }
      }
      add_facility(min_itr_idx, min_itr_facility);
      score += min_dist;
    }

    if(covered_facilities < facilities.no_of_facilities){
      genes = prev_genes;
      score = prev_score;
      fallocation = prev_fallocation;
    }
  }
  
  public void remove_facility(int f_idx, FacilitiesAllocation original_allocation){
    //restore facility f customer allocation
    int f = genes.get(f_idx);
    visited_facilities[f] = 0;
    
    //iterating over allocation of f
    for(int i = 0; i < original_allocation.number_of_facilities; i++){
      if(original_allocation.allocation[f][i] == 1){
        //found a customer allocation to facility f
        covered_facilities--;
        
        //find other facilities j which can cover allocation i
        for(int j = 0; j < original_allocation.number_of_facilities; j++){
          if(original_allocation.allocation[j][i] == 1 && visited_facilities[j] == 1){
            covered_facilities++;
          } else if(original_allocation.allocation[j][i] == 1) {
            fallocation.facility_coverage[j]++;
            fallocation.allocation[j][i] = 1;
          }
        }
      }
    }
    genes.remove(f_idx);
  }
}
