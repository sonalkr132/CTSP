
public class FacilitiesStub extends Facilities{
  FacilitiesStub(){
    super(5);
    
    double MAX_DIST = 1000000.0;
    super.map = new double[][]{
      {MAX_DIST,       26, 12, 9,       4},
      {26, MAX_DIST,        3,       17,       19},
      {12,        3, MAX_DIST,        9,       21},
      {9,        17,        9, MAX_DIST,        7},
      {4,        19,       21,        7, MAX_DIST}
    };
  }
}
