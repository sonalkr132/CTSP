
public class FacilitiesAllocationStub extends FacilitiesAllocation {
  FacilitiesAllocationStub(){
    super(5);
    int [][]tmp_allocation = {
        {1, 0, 0, 1, 1}, 
        {0, 1, 1, 0, 1}, 
        {0, 1, 1, 1, 0}, 
        {1, 0, 1, 1, 0}, 
        {1, 1, 0, 0, 1}};
    super.allocation = tmp_allocation;
    
    int []tmp_cpf = {3, 3, 3, 3, 3};
    super.facility_coverage = tmp_cpf;
  }
}
