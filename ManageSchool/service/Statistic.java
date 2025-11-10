package ManageSchool.service;

import java.util.List;

public interface Statistic<T> {
    double getAverage();            
    T getTopEntity();               
    long countByCategory(String category); 
    void showStatistics();          
    List<T> getAllData();           
}
