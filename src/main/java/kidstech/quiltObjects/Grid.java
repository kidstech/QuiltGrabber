package kidstech.quiltObjects;

import java.awt.geom.Line2D;
import java.util.ArrayList;

// Code by deragonmr, in DigiQuilt

// referenced from DigiQuilt
@SuppressWarnings("serial")
public class Grid extends ArrayList<Line2D.Double>{


    /**
     * Construct a new set of Grid. Comes with 4 lines, one on each
     * edge, by default. 
     */
    public Grid(){
        this.add(new Line2D.Double(0.0, 0.0, 0.0, 1.0));
        this.add(new Line2D.Double(0.0, 0.0, 1.0, 0.0));
        this.add(new Line2D.Double(0.0, 1.0, 1.0, 1.0));
        this.add(new Line2D.Double(1.0, 0.0, 1.0, 1.0));

    }
    
    /**
     * Construct a new Grid, with lines set up as specified to evenly divide the
     * grid horizontally, vertically, or diagonally. So, for example, specifying
     * hDivs as 4 will result in a grid with horizontal lines dividing the Grid
     * into 4 evenly spaced sections. (Note that the diagonal lines will be evenly
     * spaced, but will not create sections with equal areas.)
     * 
     * @param hDivs number of horizontal divisions
     * @param vDivs number of vertical divisions
     * @param dUpDivs number of diagonal (going up from left to right) divisions
     * @param dDownDivs number of diagonal (going down from left to right) divisions
     */
    public Grid(int hDivs, int vDivs, int dUpDivs, int dDownDivs){
        this();
        addHorizontalLines(hDivs);
        addVerticalLines(vDivs);
    }
    
    /**
     * Construct a new Grid, with lines set up as specified to evenly divide the
     * grid horizontally, vertically, or diagonally. So, for example, specifying
     * hDivs as 4 will result in a grid with horizontal lines dividing the Grid
     * into 4 evenly spaced sections. (Note that the diagonal lines will be evenly
     * spaced, but will not create sections with equal areas.)
     * 
     * The given array of GridLines will also be added to the Grid automatically,
     * allowing for any possible grid to be created without having to use lots of
     * addLine() calls.
     * 
     * @param hDivs number of horizontal divisions
     * @param vDivs number of vertical divisions
     * @param dUpDivs number of diagonal (going up from left to right) divisions
     * @param dDownDivs number of diagonal (going down from left to right) divisions
     * @param lines an array of GridLines to be added in addition to the divisions
     * requested above (if any).
     */
    public Grid(int hDivs, int vDivs, int dUpDivs, int dDownDivs, 
            Line2D.Double... lines){
        this(hDivs, vDivs, dUpDivs, dDownDivs);
        for (Line2D.Double line : lines){
            this.add(line);
        }
    }

    /**
     * Adds vertical lines, evenly spaced, to create n divisions.
     * @param divisions the number of desired divisions
     */
    private final void addVerticalLines(int divisions){
        if (divisions <= 1){
            return;
        }
        int divider = divisions;
        double spacer = 1.0 / divider;

        for(int i = 1; i <= divisions; i++){
            this.add(new Line2D.Double(spacer*i, 0.0, spacer*i, 1.0)) ;
        }
    }

    /**
     * Adds horizontal lines, evenly spaced, to create n divisions
     * @param divisions the number of desired divisions
     */
    private final void addHorizontalLines(int divisions){
        if (divisions <= 1){
            return;
        }
        int divider = divisions;
        double spacer = 1.0 / divider;

        for(int i = 1; i <= divisions; i++){
            this.add(new Line2D.Double(0.0, spacer*i, 1.0, spacer*i)) ;
        }
    }
}