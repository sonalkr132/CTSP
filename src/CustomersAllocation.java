 import java.io.File;
import java.util.*;

public class CustomersAllocation {
  public int[][]allocation;
  public int[] customers_per_facility;
  Integer[] facilites_set;
  public int number_of_customers, number_of_facilites;
  
  
  CustomersAllocation(int _number_of_customers, int _number_of_facilites){
    number_of_customers = _number_of_customers;
    number_of_facilites = _number_of_facilites;
    
    customers_per_facility = new int[number_of_facilites];
    facilites_set = new Integer[number_of_facilites];
    for(int i =0; i < number_of_facilites; i++) facilites_set[i] = Integer.valueOf(i);
  }
  
  // copy constructor
  CustomersAllocation(CustomersAllocation original_ca){
    number_of_customers = original_ca.number_of_customers;
    number_of_facilites = original_ca.number_of_facilites;
    
    customers_per_facility = original_ca.customers_per_facility.clone();
    facilites_set = original_ca.facilites_set.clone();
    allocation = new int[number_of_facilites][number_of_customers];
    for(int i = 0; i < number_of_facilites; i++){
      allocation[i] = original_ca.allocation[i].clone(); 
    }
  }
  
  public void allocate_random_customers(){
    allocation = new int[number_of_facilites][number_of_customers];
    Random rand = new Random();
    
    for(int i = 0; i < number_of_customers; i++){
      int idx = rand.nextInt(number_of_facilites);
      allocation[idx][i] = 1;
      customers_per_facility[idx]++;
    }
  }
  
  // does not allocate the allocation matrix
  public void allocate_customers(String filename, int num, double[][] map){
    load_tsplib_points(filename, num, map);
    for(int i = 0; i < number_of_facilites; i++) customers_per_facility[i] = num;
  }
  
  public ArrayList<Integer> sort(ArrayList<Integer> facilites){
    Integer[] dup_facilites_set = facilites_set;
    
    //sorts in increasing order
    Arrays.sort(dup_facilites_set, new NumberOfCustomersComparator());
    
    ArrayList<Integer> sorted_facilites = new ArrayList<Integer>();
    
    //itr over sorted facilites and add facility to sorted list
    //when matching facility in list was found
    for(int i = (number_of_facilites - 1); i >= 0 ; i--){
      int facility = dup_facilites_set[i];
      if(facilites.contains(facility)) sorted_facilites.add(facility);
    }
    
    return sorted_facilites;
  }
  
  public class NumberOfCustomersComparator implements Comparator<Integer> {
    public int compare(final Integer o1, final Integer o2) {
      return Float.compare(customers_per_facility[o1], customers_per_facility[o2]);
    }
  }
  
  public void load_tsplib_points(String filename, int num, double[][] map){
    allocation = new int[number_of_facilites][number_of_customers];
    for(int i=0; i < number_of_facilites; i++){
      double[] customers = map[i].clone();
      ArrayIndexComparator comparator = new ArrayIndexComparator(customers);
      Integer[] indexes = comparator.createIndexArray();
      Arrays.sort(indexes, comparator);
      
      for(int c = 0; c < num; c++) allocation[i][indexes[c]] = 1;
    }
  }  
  
  public class ArrayIndexComparator implements Comparator<Integer> {
      private final double[] array;

      public ArrayIndexComparator(double[] array) {  this.array = array; }

      public Integer[] createIndexArray(){
          Integer[] indexes = new Integer[array.length];
          for (int i = 0; i < array.length; i++) indexes[i] = i; // Autoboxing
          return indexes;
      }

      @Override
      public int compare(Integer index1, Integer index2){
           // Autounbox from Integer to int to use as array indexes
          return Double.compare(array[index1], array[index2]);
      }
  }
}
