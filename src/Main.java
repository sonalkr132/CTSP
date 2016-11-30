import java.util.ArrayList;
import java.io.*;


public class Main {
  public static void main(String[] args) {
    int NUMBER_OF_FACILITES = 15;
    int NUMBER_OF_CUSTOMERS = 35;
    int POPULATION_SIZE = 30;
    int PRIZE = 35;
    int NUM_OF_ITR = 8000;
    boolean TSPLIB = true;
    Population p = new Population(NUMBER_OF_FACILITES, NUMBER_OF_CUSTOMERS, POPULATION_SIZE, PRIZE); //number of points, population size
    
    if(TSPLIB){
      p.tsplib_cities("/home/addie/current/tsplib/eil51.tsp", "points");
      p.allocate_fixed_customers("/home/addie/current/tsplib/eil51.tsp", 3);
    } else{
      p.fixed_point_facilites();
      //p.random_point_facilites(100);
      p.allocate_customers();
    }

    //customers allocation
//    System.out.println("\n\nAllocation(facilites x customers): \n");
//    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
//      for(int j = 0; j < NUMBER_OF_CUSTOMERS; j++){
//        System.out.print(p.cust_allocation.allocation[i][j] + " ");
//      }
//      System.out.println();
//    }
    
    //customers allocation per facility
    System.out.println("\nCustomers per facility: \n");
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      System.out.print(i + " ");
    }
    System.out.println();
    for(int i = 0; i < NUMBER_OF_FACILITES; i++){
      System.out.print(p.cust_allocation.customers_per_facility[i] + " ");
    }
    
    
 
    p.initialize_population();
    
    //Intial Chromosome score and genes
    System.out.println("\n\nIntial Chromosome score and genes:\n");
    for(int i = 0; i < POPULATION_SIZE; i++){
      ArrayList<Integer> genes = p.chromosomes[i].genes;
      System.out.print("[" + p.chromosomes[i].score + "] ");
      for(int j = 0; j < genes.size(); j++){
        System.out.print(genes.get(j) + " ");
      }
      System.out.println();
    }
    
    //Best Chromosome score and gene
    System.out.println("\n\nBest Chromosome score and genes:\n");
    ArrayList<Integer> genes = p.alltime_best_chromosome.genes;
    System.out.print("[" + p.alltime_best_chromosome.score + "] ");
    for(int j = 0; j < genes.size(); j++){
      System.out.print(genes.get(j) + " ");
    }
    System.out.println("\nNEXT GENERATIONS:\n\n");

  
    for(int i = 0; i < NUM_OF_ITR; i++){
      p.next_generation();
      if(i < 20){
        //Best Chromosome score and gene
        System.out.println("\n\nBest Chromosome score and genes:\n");
        genes = p.alltime_best_chromosome.genes;
        System.out.print("[" + p.alltime_best_chromosome.score + "] ");
        for(int j = 0; j < genes.size(); j++){
          System.out.print(genes.get(j) + " ");
        }
      }
      else if(i % 100 == 0){
        //Best Chromosome score and gene
        System.out.println("\n\nBest Chromosome score and genes:\n");
        genes = p.alltime_best_chromosome.genes;
        System.out.print("[" + p.alltime_best_chromosome.score + "] ");
        for(int j = 0; j < genes.size(); j++){
          System.out.print(genes.get(j) + " ");
        }
      }
      
      //termination condition
      if(p.best_score_fraction() > 0.7){
        System.out.print("[Terminated at: " + i + " itr] ");
        System.out.println("\n\nFinal Chromosome score and genes:\n");
        for(int c = 0; c < POPULATION_SIZE; c++){
          genes = p.chromosomes[c].genes;
          System.out.print("[" + p.chromosomes[c].score + "] ");
          for(int j = 0; j < genes.size(); j++){
            System.out.print(genes.get(j) + " ");
          }
          System.out.println();
        }
        break;
      }
    }
  }
}

