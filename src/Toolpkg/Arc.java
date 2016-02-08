package Toolpkg;

import SodickCNCProgram.CNCCodeLine;

/**
 *
 * @author Mats
 */
public class Arc extends Geometry {
    
    private final double centerX, centerY, radius, startAngle, endAngle;
    private final Util.ArcDirection direction;

    public Arc(double centerX, double centerY, double radius, double startAngle, double endAngle, Util.ArcDirection direction ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.direction = direction;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getEndAngle() {
        return endAngle;
    }

    @Override
    public Point getStartPoint() {
        return new Point(
                centerX + radius * Math.cos(Math.toRadians(startAngle)), 
                centerY + radius * Math.sin( Math.toRadians(startAngle)));
    }
    
    @Override
    public Point getEndPoint() {
        return new Point(
                centerX + radius * Math.cos(Math.toRadians(endAngle)), 
                centerY + radius * Math.sin( Math.toRadians(endAngle)));
    }

    public Util.ArcDirection getDirection() {
        return direction;
    }
    
    public Arc getReversedArc() {
        Util.ArcDirection newDirection = Util.ArcDirection.CCW;
        if ( direction == Util.ArcDirection.CCW ) newDirection = Util.ArcDirection.CW;
        return new Arc( centerX, centerY, radius, startAngle, endAngle, newDirection );
    }

    public static Arc getFillet( Line l1, Line l2, double filletRadius) {
        // The end of l1 must be the start of l2
        if ( l1.getEndPoint().pointDistance( l2.getStartPoint() ) > Constants.SAME_POINT_MAX_DISTANCE ) {
            return null;
        }
        double biSectorAngle = (l1.getRevAngle() + l2.getAngle()) / 2;
        double diffAngle = biSectorAngle - l2.getAngle();
        
        double lengthOfBiSector = filletRadius / Math.cos(diffAngle);
        
        Line l3 = Line.getLineAtAngle( l1.getEndPoint(), biSectorAngle, lengthOfBiSector );
        
        Util.ArcDirection dir = Util.ArcDirection.CCW;
        
        if (diffAngle < Math.PI ) dir = Util.ArcDirection.CW;
        
        Arc fillet = new Arc(l3.getxEnd(), l3.getyEnd(), filletRadius, l1.getAngle() - Math.PI/2, l2.getAngle() - Math.PI/2, dir );
                return fillet;
    }
    @Override
    public CNCCodeLine geoToCNCCode(Point lastPoint, Util.GCode lastGCode) {
        Util.GCode gCode;
        Point startPoint;
        Point endPoint;
        if ( direction == Util.ArcDirection.CCW ) {
            gCode = Util.GCode.G03;
            startPoint = getStartPoint();
            endPoint = getEndPoint();
        } else {
            gCode = Util.GCode.G02;
            startPoint = getEndPoint();
            endPoint = getStartPoint();
        }
        double iValue = centerX - startPoint.getxPoint();
        double jValue = centerY - startPoint.getyPoint();
        
        String line = "";
        if ( gCode != lastGCode ) {
            line += Util.gCodeToString( gCode) + " ";
        }
        if ( Math.abs( endPoint.getxPoint() - lastPoint.getxPoint() ) > 0.00005 ) line += "X" + Util.cncRound( endPoint.getxPoint(),4) + " ";
        if ( Math.abs( endPoint.getyPoint() - lastPoint.getyPoint() ) > 0.00005 ) line += "Y" + Util.cncRound( endPoint.getyPoint(),4) + " ";
        
        line += "I" + Util.cncRound(iValue,4) + " ";
        line += "J" + Util.cncRound(jValue,4);
        
        return new CNCCodeLine(line, gCode, endPoint);
       
    }
    
    
    
}
