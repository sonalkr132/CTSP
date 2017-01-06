import java.util.ArrayList;
import java.util.*;

import java.util.Random;

public class Chromosome {
  public double score;
  public int collected_prize;
  public ArrayList<Integer> genes;
  public CustomersAllocation callocation;
  final Facilites facilites;
  int ITR_LIMIT = 100;

  Chromosome(CustomersAllocation cust_allocation, Facilites _facilites, int prize){
    genes = new ArrayList<Integer>();
    facilites = _facilites;
    callocation = new CustomersAllocation(cust_allocation);
 
    collected_prize = 0;
    while(collected_prize < prize) add_next_facility();

    shuffle_array(genes);
    score = evaluate(facilites.map);
  }
  
  Chromosome(double _score, int _clone_prize, ArrayList<Integer> _genes, Facilites _facilites, CustomersAllocation ca){
    score = _score;
    collected_prize = _clone_prize;
    genes = new ArrayList<Integer>(_genes);
    facilites = _facilites;
    callocation = new CustomersAllocation(ca);
  }
  
  public void add_next_facility(){
    //find facilites with at least one customers allocation
    int f;
    ArrayList<Integer> unvisited_facilites = unvisited_facilites();
    Random rand = new Random();
    f = rand.nextInt(unvisited_facilites.size());
    add_facility(genes.size(), unvisited_facilites.get(f));
  }
  
  public void add_facility(int f_idx, int f){
    for(int i = 0; i < callocation.number_of_customers; i++){
      if(callocation.allocation[f][i] == 1){
        //found a customer allocation to facility f
        collected_prize++;
        for(int j = 0; j < callocation.number_of_facilites; j++){
          if(callocation.allocation[j][i] == 1){
            callocation.customers_per_facility[j]--;
            callocation.allocation[j][i] = 0;
          }
        }
      }
    }
    genes.add(f_idx, f);
  }
  
  //Evaluates the total sum of distances between the genes/points of the chromosome
  // @param dist 2D array of distance between all the points
  public double evaluate(double[][] dist){
    int size = genes.size();
    double sum = dist[genes.get(0)][genes.get(size - 1)];
    for(int i = 1; i < size; i++){
      sum += dist[genes.get(i)][genes.get(i - 1)];
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
  
  public void recover(int prize, CustomersAllocation ca){
    remove_duplicates(ca);
    ArrayList<Integer> unvisited_facilites = unvisited_facilites();
    unvisited_facilites = ca.sort(unvisited_facilites);

    while(collected_prize < prize){
      int facility = unvisited_facilites.remove(0);
      add_facility(genes.size(), facility);
    }
    score = evaluate(facilites.map);
  }
  
  public void remove_duplicates(CustomersAllocation original_ca){
    ArrayList<Integer> dup = new ArrayList<Integer>();
    Iterator<Integer> iterator = genes.iterator();
    while(iterator.hasNext()){
      int gene = (int) iterator.next();
      if(!dup.contains(gene)) dup.add(gene);
    }
    
    genes.clear();
    callocation = new CustomersAllocation(original_ca);
    collected_prize = 0;
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
    double cur_score = evaluate(facilites.map);
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
  
  public void drop_and_procedures(CustomersAllocation ca, int prize){
    Random rand = new Random();
    int r_idx = rand.nextInt(genes.size());
    
    int r_facility = genes.get(r_idx);
    remove_facility(r_idx, ca);
    ArrayList<Integer> unvisited_facilites = unvisited_facilites();
    
    boolean found = false;
    for(int i = 0; i < unvisited_facilites.size() && !found; i++){
      int itr_facility = unvisited_facilites.get(i);
      int itr_cost = callocation.customers_per_facility[itr_facility];
      
      if(collected_prize + itr_cost >= prize){
        //feasible solution
        //we will try to see if score improves
        double dist = facility_exchange_cost(r_idx, r_facility, itr_facility);
        
        if(dist > 0){
          //score would improve
          score -= dist;
          add_facility(r_idx, itr_facility);
          found = true;
        }
      }
    }
    if(!found) add_facility(r_idx, r_facility);
  }
  
  public double facility_exchange_cost(int r_idx, int r_facility, int itr_facility){
    int prev_facility = genes.get(get_prev_facility(r_idx));;
    int next_facility = genes.get(get_next_facility(r_idx));

    return facilites.map[prev_facility][r_facility] + facilites.map[r_facility][next_facility]
             - facilites.map[prev_facility][itr_facility] - facilites.map[itr_facility][next_facility];
  }
  
  private int get_prev_facility(int idx){
    if(idx == 0) return genes.size() - 1;
    else return idx - 1;
  }
  
  // facility was removed at idx. current facility at idx
  // is next facility
  private int get_next_facility(int idx){
    if(idx == genes.size()) return 0;
    else return idx;
  }

  public ArrayList<Integer> unvisited_facilites(){
    ArrayList<Integer> unvisited_facilites = new ArrayList<Integer> ();
    for(int i = 0; i < facilites.number_of_facilites; i++) {
      if(!genes.contains(i) && callocation.customers_per_facility[i] > 0){
        unvisited_facilites.add(i);
      }
    }
    
    return unvisited_facilites;
  }
  
  public static Chromosome mutate(CustomersAllocation cust_allocation, int prize, Chromosome to_clone){
    Chromosome clone = new Chromosome(to_clone.score,
        to_clone.collected_prize,
        to_clone.genes,
        to_clone.facilites,
        to_clone.callocation);
    Random rand = new Random();
    int idx = rand.nextInt(to_clone.genes.size());
    
    clone.remove_facility(idx, cust_allocation);
    clone.recover(prize, cust_allocation);
    return clone;
  }
  
  public void remove_facility(int f_idx, CustomersAllocation original_allocation){
    //restore facility f customer allocation
    int f = genes.get(f_idx);
    for(int i = 0; i < original_allocation.number_of_customers; i++){
      if(original_allocation.allocation[f][i] == 1){
        //found a customer allocation to facility f
        collected_prize--;
        for(int j = 0; j < original_allocation.number_of_facilites; j++){
          if(original_allocation.allocation[j][i] == 1){
            callocation.customers_per_facility[j]++;
            callocation.allocation[j][i] = 1;
          }
        }
      }
    }
    genes.remove(f_idx);
  }
}
