public class Main {
  public static void main(String[] args) {
    int NUMBER_OF_FACILITES = 5;
    int NUMBER_OF_CUSTOMERS = 15;
    int POPULATION_SIZE = 40;
    int PRIZE = 12;
    int NUM_OF_ITR = 80000;
    Population p = new Population(NUMBER_OF_FACILITES, NUMBER_OF_CUSTOMERS, POPULATION_SIZE, PRIZE); //number of points, population size
    p.fixed_point_facilites();
    p.allocate_customers();
    
    
    //customers allocation per facility
    System.out.println("\nCustomers per facility: \n");
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
    System.out.println();
 
    p.initialize_population();
    
//    for(int i = 0; i < NUM_OF_ITR; i++){
//      p.next_generation();
//    }
    System.out.print("some");
  }
}
