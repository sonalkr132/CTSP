import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    int NUMBER_OF_FACILITES = 5;
    int NUMBER_OF_CUSTOMERS = 15;
    int POPULATION_SIZE = 40;
    int PRIZE = 12;
    int NUM_OF_ITR = 800;
    Population p = new Population(NUMBER_OF_FACILITES, NUMBER_OF_CUSTOMERS, POPULATION_SIZE, PRIZE); //number of points, population size
    p.random_point_facilites(100);
    p.allocate_customers();
    
    
    //customers allocation per facility
    System.out.println("\nCustomers per facility: \n");
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      System.out.print(i + " ");
    }
    System.out.println();
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      System.out.print(p.cust_allocation.customers_per_facility[i] + " ");
    }
    
    //customers allocation
    System.out.println("\n\nAllocation(facilites x customers): \n");
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      for(int j = 0; j < NUMBER_OF_CUSTOMERS; j++){
        System.out.print(p.cust_allocation.allocation[i][j] + " ");
      }
      System.out.println();
    }
 
    p.initialize_population();
    
    //Intial Chromosome score and genes
//    System.out.println("\n\nIntial Chromosome score and genes:\n");
//    for(int i = 0; i < POPULATION_SIZE; i++){
//      ArrayList<Integer> genes = p.chromosomes[i].genes;
//      System.out.print("[" + p.chromosomes[i].score + "] ");
//      for(int j = 0; j < genes.size(); j++){
//        System.out.print(genes.get(j) + " ");
//      }
//      System.out.println();
//    }
//    
//    //Best Chromosome score and gene
//    System.out.println("\n\nBest Chromosome score and genes:\n");
//    ArrayList<Integer> genes = p.alltime_best_chromosome.genes;
//    System.out.print("[" + p.alltime_best_chromosome.score + "] ");
//    for(int j = 0; j < genes.size(); j++){
//      System.out.print(genes.get(j) + " ");
//    }
//    System.out.println("\nNEXT GENERATION\n");


    for(int i = 0; i < NUM_OF_ITR; i++){
      if(i % 100 == 0){
      //Best Chromosome score and gene
      System.out.println("\n\nBest Chromosome score and genes:\n");
      ArrayList<Integer> genes = p.alltime_best_chromosome.genes;
      System.out.print("[" + p.alltime_best_chromosome.score + "] ");
      for(int j = 0; j < genes.size(); j++){
        System.out.print(genes.get(j) + " ");
      }
      }
      p.next_generation();
    }
  }
}
