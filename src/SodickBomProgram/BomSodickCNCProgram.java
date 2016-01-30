/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SodickBomProgram;

import SodickCNCProgram.CutProgram;
import SodickCNCProgram.FirstReliefProgram;
import SodickCNCProgram.PartProgram;
import SodickCNCProgram.SecondReliefProgram;
import Toolpkg.Chain;

/**
 *
 * @author Mats
 */
public class BomSodickCNCProgram {

    private final MainProgram mainProgram;
    private final CutProgram cutProgram;
    private final PartProgram partProgram;
    private final FirstReliefProgram firstReliefProgram;
    private final SecondReliefProgram secondReliefProgram;
    

    public BomSodickCNCProgram(double stockDia, double firstTiltMeasurePoint) {
        mainProgram = new MainProgram( stockDia, firstTiltMeasurePoint );
        cutProgram = new CutProgram();
        partProgram = new PartProgram();
        firstReliefProgram = new FirstReliefProgram();
        secondReliefProgram = new SecondReliefProgram();
    }

    public void setChains(Chain partChain, Chain cutGeoChain, Chain firstReleifChain, Chain secondReliefChain) {
        partProgram.setChain(partChain);
        cutProgram.setChain(cutGeoChain);
        firstReliefProgram.setChain(firstReleifChain);
        secondReliefProgram.setChain(secondReliefChain);
        mainProgram.setChains( partChain, cutGeoChain, firstReleifChain, secondReliefChain );
    }
    
    public void buildProgram() {
        partProgram.build("F0S0.NC");
        if (!partProgram.save( )) return;
        cutProgram.build("F90S10.NC");
        if (!cutProgram.save()) return;
        firstReliefProgram.build("G120S5.NC");
        if (!firstReliefProgram.save()) return;
        secondReliefProgram.build("G150S0N5.NC");
        if (!secondReliefProgram.save()) return;
        mainProgram.build("MAINS.NC");
        if (!mainProgram.save()) return;
    }   

       
}
