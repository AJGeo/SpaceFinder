/*
 * SpaceFinder.java
 * 
 * Created on 04 Nov 2018  23:41:27
 */
package spacefinder;

/**
 * Demonstrate basic usage of SpaceManager class
 * 
 * @author AJ
 * 
 * OUTPUT
 * Clutter Rect x: 80, y: 107, width: 55, hight: 45
 * Find space for x: 85, y: 115, width: 35, hight: 35
 * 0 Rect x: 85, y: 152, width: 35, hight: 35, distance factor: 1369
 * 1 Rect x: 45, y: 115, width: 35, hight: 35, distance factor: 1600
 * 2 Rect x: 85, y: 72, width: 35, hight: 35, distance factor: 1849
 * 3 Rect x: 135, y: 115, width: 35, hight: 35, distance factor: 2500
 * 4 Rect x: 120, y: 152, width: 35, hight: 35, distance factor: 2594
 * 5 Rect x: 50, y: 152, width: 35, hight: 35, distance factor: 2594
 * 6 Rect x: 45, y: 80, width: 35, hight: 35, distance factor: 2825
 * 7 Rect x: 120, y: 72, width: 35, hight: 35, distance factor: 3074
 * 8 Rect x: 85, y: 187, width: 35, hight: 35, distance factor: 5184
 * 9 Rect x: 10, y: 115, width: 35, hight: 35, distance factor: 5625

 */
public class SpaceFinder
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        SpaceManager spaceManager = new SpaceManager();

        //Set Min Width & Height
        spaceManager.setMinimumSize(30, 30);

        //Set Aria of intrest
        Integer[] spaceRec = new Integer[]
        {
            // For test set arbitrary
            0, 0, 575, 476
        };
        spaceManager.setSpaceRect(spaceRec);

        //Add obstical(s) / Clutter Rect(s)
        // x, y, x+width, y+height
        Integer[] ClutRec = new Integer[]
        {
            80,
            107,
            90 + 45,
            107 + 45
        };
        spaceManager.addRectAngle(ClutRec);
        System.out.println(" Clutter Rect x: ".
                concat(String.valueOf(ClutRec[0])).
                concat(", y: ").
                concat(String.valueOf(ClutRec[1])).
                concat(", width: ").
                concat(String.valueOf(ClutRec[2] - ClutRec[0])).
                concat(", hight: ").
                concat(String.valueOf(ClutRec[3] - ClutRec[1])));

        // Sort the free space rectangles
        spaceManager.sortBeforeFind();

        Integer[] DeClutDimRec = new Integer[]
        {
            85,
            115,
            85 + 35,
            115 + 35
        };
        System.out.println(" Find space for x: ".
                concat(String.valueOf(DeClutDimRec[0])).
                concat(", y: ").
                concat(String.valueOf(DeClutDimRec[1])).
                concat(", width: ").
                concat(String.valueOf(DeClutDimRec[2] - DeClutDimRec[0])).
                concat(", hight: ").
                concat(String.valueOf(DeClutDimRec[3] - DeClutDimRec[1])));

        // Find location for the same rectangle N time
        for (int i = 0; i < 10; i++)
        {
            Integer[] DeClutRectRec = zeroRect();
            Integer[] distance = new Integer[]
            {
                0
            };
            if (spaceManager.findLocation(DeClutDimRec, DeClutRectRec, distance))
            {
                // Add found location of rectangle to occupied space
                spaceManager.addRectAngle(DeClutRectRec);
                System.out.println(String.valueOf(i).concat(" Rect x: ").
                        concat(String.valueOf(DeClutRectRec[0])).
                        concat(", y: ").
                        concat(String.valueOf(DeClutRectRec[1])).
                        concat(", width: ").
                        concat(String.valueOf(DeClutRectRec[2] - DeClutRectRec[0])).
                        concat(", hight: ").
                        concat(String.valueOf(DeClutRectRec[3] - DeClutRectRec[1])).
                        concat(", distance factor: ").
                        concat(String.valueOf(distance[0]))
                );
            }
        }
    }

    private static Integer[] zeroRect()
    {
        return new Integer[]
        {
            0, 0, 0, 0
        };
    }
}
