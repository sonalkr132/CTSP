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
  
  public void allocate_customers(){
    allocation = new int[number_of_facilites][number_of_customers];
    Random rand = new Random();
    
    for(int i = 0; i < number_of_customers; i++){
      int idx = rand.nextInt(number_of_facilites);
      allocation[idx][i] = 1;
      customers_per_facility[idx]++;
    }
  }
  
  // does not allocate the allocation matrix
  public void allocate_fixed_customers(String filename, int num){
    load_tsplib_points(filename, num);
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
  
  public void load_tsplib_points(String filename, int num){
    int total_points = number_of_facilites + number_of_customers;
    int[][] ary = new int[total_points][2];
    try {
      Scanner input = new Scanner(new File(filename));
      //only useful info in first six lines is dimension and then skip 1 more for depot
      for (int i = 0; i < 7; i++) input.nextLine();
      
      for (int i = 0; i < total_points; i++){
        input.nextInt(); //skip first number on each row
        ary[i][0] = input.nextInt();
        ary[i][1] = input.nextInt();
      }
      input.close();
    } catch (Exception e){
      System.out.println("Something went wrong in loading tsplib:" + e.getMessage());
    }

    int [][]map = set_map(ary, total_points);
    allocation = new int[number_of_facilites][number_of_customers];
    for(int i=0; i < number_of_facilites; i++){
      int[] customers = Arrays.copyOfRange(map[i], number_of_facilites, total_points);
      ArrayIndexComparator comparator = new ArrayIndexComparator(customers);
      Integer[] indexes = comparator.createIndexArray();
      Arrays.sort(indexes, comparator);
      
      for(int c = 0; c < num; c++) allocation[i][indexes[c]] = 1;
    }
  }
  
  private int[][] set_map(int[][] ary, int total_points){
    int [][]map = new int[total_points][total_points];
    for(int i = 0; i < total_points; i++){
      for(int j = 0; j < total_points; j++){
        Double dist = distance_between(ary[i], ary[j]);
        map[i][j] = dist.intValue();
      }
    }
    
    return map;
  }
  
  
  private double distance_between(int[] point_i, int[] point_j){
    int dx = point_i[0] - point_j[0];
    int dy = point_i[1] - point_j[1];
    return Math.sqrt(dx * dx + dy * dy);
  }
  
  public class ArrayIndexComparator implements Comparator<Integer> {
      private final int[] array;

      public ArrayIndexComparator(int[] array) {  this.array = array; }

      public Integer[] createIndexArray(){
          Integer[] indexes = new Integer[array.length];
          for (int i = 0; i < array.length; i++) indexes[i] = i; // Autoboxing
          return indexes;
      }

      @Override
      public int compare(Integer index1, Integer index2){
           // Autounbox from Integer to int to use as array indexes
          return Integer.compare(array[index1], array[index2]);
      }
  }
}
