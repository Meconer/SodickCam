/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SodickSickelProgram;

/**
 *
 * @author matsandersson
 */
public class CutProgram extends AngleFineProgram {
    String fileName;

    void build(String fileName) {
        this.fileName = fileName;
        addHeader();
        addMainProgram();
        
    }
  
}
