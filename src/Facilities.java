import java.io.File;
import java.util.*;


public class Facilities {
  public double[][] map;
  public int no_of_facilities;
  
  Facilities(int num){
    no_of_facilities = num;
  }
   
   public void load_csp_points(String filename) {
     int[][] ary = new int[no_of_facilities][2];

     try {
       Scanner input = new Scanner(new File(filename));
       //only useful info in first six lines is dimension
       for (int i = 0; i < 6; i++) input.nextLine();
       
       for (int i = 0; i < no_of_facilities; i++){
         input.nextInt(); //skip first number on each row
         ary[i][0] = input.nextInt();
         ary[i][1] = input.nextInt();
       }
       input.close();
     } catch (Exception e){
       System.out.println("Something went wrong in loading tsplib:" + e.getMessage());
     }
     
     set_map(ary);
   }
   
   public int find_nearest_facility(int facility){
     int idx = 0;
     double min_dist = 100000.0;
     for(int i = 0; i < no_of_facilities; i++){
       if(min_dist > map[facility][i]){
         min_dist = map[facility][i];
         idx = i;
       }
     }

     return idx;
   }

   public ArrayList<Integer> sort(Integer origin_facility, ArrayList<Integer> facilites){
     DistanceComparator comparator = new DistanceComparator(origin_facility);
     Collections.sort(facilites, comparator);
     
     return facilites;
   }

   public class DistanceComparator implements Comparator<Integer> {
     private int facility;
     
     public DistanceComparator(int facility) {  this.facility = facility; }
 
     public int compare(final Integer o1, final Integer o2) {
       return Double.compare(map[facility][o1], map[facility][o2]);
     }
   }

   private void set_map(int[][] ary){
     map = new double[no_of_facilities][no_of_facilities];
     for(int i = 0; i < no_of_facilities; i++){
       for(int j = 0; j < no_of_facilities; j++){
         Double dist = distance_between(ary[i], ary[j]);
         map[i][j] = dist;
       }
     }
   }

   private double distance_between(int[] point_i, int[] point_j){
     double dx = point_i[0] - point_j[0];
     double dy = point_i[1] - point_j[1];
     double ans = Math.sqrt(dx * dx + dy * dy);
     return ans;
   }
}
