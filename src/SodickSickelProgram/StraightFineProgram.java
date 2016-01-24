/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SodickSickelProgram;

import Toolpkg.Chain;
import Toolpkg.Geometry;
import Toolpkg.Point;
import Toolpkg.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 *
 * @author Mats
 */
class StraightFineProgram extends GeoProgram {
    
    private Util.GCode lastGCode;
    
    private final static String HEADER_FILE_NAME = "SodickSickelProgram/straight6.txt";
    
    void addHeader() {
        
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(HEADER_FILE_NAME);
        BufferedReader br = new BufferedReader( new InputStreamReader(is));
        String line;
        try {
            while ( ( line = br.readLine() ) != null ) {
                program.add(line);
            }
        } catch (IOException ex) {
            System.err.println("Fel vid läsning av resource : " + HEADER_FILE_NAME);
        }
    }
    
    void addMainProgram( ) {
        Point startPoint = chain.getStartPoint();
        Point secondPoint = chain.getSecondPoint();
        Point lastPoint = chain.getLastPoint();
        Point nextToLastPoint = chain.getNextToLastPoint();
        
        buildMain( startPoint, secondPoint, lastPoint, nextToLastPoint );
    }
    
    void addSubs() throws Exception {
        
        addSubSection( chain, "N0001");
        addSubSection( chain.getReversedChain(), "N0002");
    }

    private void buildMain(Point startPoint, Point secondPoint, Point lastPoint, Point nextToLastPoint) {
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

    private void addBackwardSection( String condition, String offset, Point nextToLastPoint) {
        program.add(condition);
        program.add("G42 H000 G01 " + nextToLastPoint.toCNCString("X", "Y"));
        program.add(offset);
        program.add("M98 P0002" ) ;
    }

    private void addForwardSection( String condition, String offset, Point secondPoint) {
        program.add(condition);
        program.add("G41 H000 G01 " + secondPoint.toCNCString("X", "Y"));
        program.add(offset);
        program.add("M98 P0001");
    }

    private void addSubSection(Chain chainToCode, String subNumber) throws Exception {
        // Början på underprogrammet
        program.add("");
        program.add(subNumber);
        lastGCode = Util.GCode.G01;
        
        // Spara nuvarande position vilket är den andra punkten eftersom huvudprogrammet
        // redan har gått dit.
        Point lastPoint = chainToCode.getSecondPoint();  
        
        // Gå igenom hela länken men hoppa över första punkten som är använd
        // i huvudprogrammet.
        Iterator<Geometry> geoIter = chainToCode.getIterator();
        
        // Skippa första linjen
        if ( geoIter.hasNext() ) geoIter.next();
        else throw new Exception("Tom länk vid addSubSection");  // Något är fel om det inte finns någon länk här.
        
        while ( geoIter.hasNext() ) {
            Geometry geo = geoIter.next();
            CNCCodeLine line = geo.geoToCNCCode(lastPoint, lastGCode);
            program.add( line.getLine() );
            lastGCode = line.getgCode();
            lastPoint = line.getLastPoint();
        }
        program.add("M99");
        
    }
}
