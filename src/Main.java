import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    int NUMBER_OF_FACILITES = 51;
    int POPULATION_SIZE = 20;
    int NUM_OF_ITR = 50000;
    int NC = 7;
    String file_to_read = "/home/addie/current/tsplib/eil51.tsp";
    Population p = new Population(NUMBER_OF_FACILITES, POPULATION_SIZE, NC, file_to_read); //number of points, population size
    

    //customers allocation
    System.out.println("\n\nAllocation(facilites x customers): \n");
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      for(int j = 0; j < NUMBER_OF_FACILITES; j++){
        System.out.print(p.f_allocation.allocation[i][j] + ", ");
      }
      System.out.println();
    }
    
    //customers allocation per facility
    System.out.println("\nCustomers per facility: \n");
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      System.out.print(i + " ");
    }
    System.out.println();
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      System.out.print(p.f_allocation.facility_coverage[i] + " ");
    }
    
    
 
    p.initialize_population();
    
    //Intial Chromosome score and genes
    print_all_chromosomes(p, POPULATION_SIZE);
    print_best_chromosome(p);
    System.out.println("\n\nNEXT GENERATIONS:\n\n");

    int no_change = 0;
    double prev_score = 0;
    
    long startTime = System.nanoTime();
    
    for(int i = 0; i < NUM_OF_ITR; i++){
      p.next_generation();
      if(i < 20) print_best_chromosome(p);
      else if(i % 1000 == 0) print_best_chromosome(p);
      
      if(prev_score == p.alltime_best_chromosome.score) no_change++;
      else{
        no_change = 0;
        prev_score = p.alltime_best_chromosome.score;
      }
      
      //termination condition
      if(no_change > 2000){
        System.out.println("\n\n[Terminated at: " + i + " itr] ");
        print_best_chromosome(p);
        //print_all_chromosomes(p, POPULATION_SIZE);
        break;
      }
    }
    print_best_chromosome(p);
    long endTime = System.nanoTime();
    System.out.println("Took "+(endTime - startTime) / 1000000000.0 + " s"); 
  }
  
  private static void print_best_chromosome(Population p){
    System.out.println("\n\nBest Chromosome score and genes:\n");
    ArrayList<Integer> genes = p.alltime_best_chromosome.genes;
    System.out.print("[" + p.alltime_best_chromosome.score + "] "+ "[" + p.alltime_best_chromosome.covered_facilities + "] ");
    for(int j = 0; j < genes.size(); j++){
      System.out.print(genes.get(j) + " ");
    }
  }
  
  private static void print_all_chromosomes(Population p, int POPULATION_SIZE){
    System.out.println("\n\nAll Chromosome score and genes:\n");
    for(int i = 0; i < POPULATION_SIZE; i++){
      ArrayList<Integer> genes = p.chromosomes[i].genes;
      System.out.print("[" + p.chromosomes[i].score + "] " + "[" + p.chromosomes[i].covered_facilities + "] ");
      for(int j = 0; j < genes.size(); j++){
        System.out.print(genes.get(j) + " ");
      }
      System.out.println();
    }
  }
}

