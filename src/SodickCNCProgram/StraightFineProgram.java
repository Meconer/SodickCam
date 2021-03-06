/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SodickCNCProgram;

import Geometry.Point;

/**
 *
 * @author Mats
 */
class StraightFineProgram extends StraightProgram {
    
    StraightFineProgram() {
        this.headerFileName = "SodickCNCProgram/straight6.txt";
    }
    
    @Override
    protected void buildMain(Point startPoint, Point secondPoint, Point lastPoint, Point nextToLastPoint) {
        program.add("G92 " + startPoint.toCNCString( "X", "Y"));
        program.add("G29");
        program.add("T94");
        program.add("T84");
        addForwardSection("C001","H001", secondPoint);
        program.add("T85");
        program.add("G149 G249");
        addBackwardSection("C002", "H002", nextToLastPoint);
        addForwardSection("C900","H003", secondPoint);
        addBackwardSection("C901", "H004", nextToLastPoint);
        addForwardSection("C902","H005", secondPoint);
        addBackwardSection("C903", "H006", nextToLastPoint);
        program.add("M199");
    }

    @Override
    public void addSubs() throws IllegalArgumentException {
        
        addSubSection( chain, "N0001");
        addSubSection( chain.getReversedChain(), "N0002");
    }

    private void addBackwardSection( String condition, String offset, Point nextToLastPoint) {
        program.add(condition);
        program.add("G42 H000 G01 " + nextToLastPoint.toCNCString("X", "Y"));
        program.add(offset);
        program.add("M98 P0002" ) ;
    }
}
