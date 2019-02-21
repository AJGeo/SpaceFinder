/*
 * SpaceManager.java        
 * 
 * Created on 04 Nov 2018  23:49:18
 */
package spacefinder;

import static java.lang.Math.abs;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * SpaceManager finds the best placement of a rectangle with out overlap to any
 * existing rectangles closest to desired location
 *
 * @author AJ
 */
public class SpaceManager
{
//    private final String outputName = "SpaceManager";
//    private final boolean showOutput = true;
    private final int[] OPPOSITE = new int[]
    {
        2, 3, 0, 1
    };
    private final DimensionRec minSizeDim = new DimensionRec();
    private Integer[] spaceRect = new Integer[3];
    private final List<Integer[]> freeRectList = new ArrayList<>();

    public boolean addRectAngle(Integer[] inRect)
    {
        boolean reduce, add, del;
        int counter1, counter2;
        HashSet<Integer[]> new_free = new HashSet<>();
        HashSet<Integer[]> new_set = new HashSet<>();
        HashSet<Integer[]> mod_new_set = new HashSet<>();
        HashSet<Integer[]> reduced_set = new HashSet<>();

        inRect = normalized(inRect);
        int iLeft = inRect[0];
        int iTop = inRect[1];
        int iRight = inRect[2];
        int iBottom = inRect[3];

        if ((inRect[0] > spaceRect[OPPOSITE[0]])
                || (inRect[0 + 2] < spaceRect[OPPOSITE[0 + 2]]))
        {
            return false;
        }
        if ((inRect[1] > spaceRect[OPPOSITE[1]])
                || (inRect[1 + 2] < spaceRect[OPPOSITE[1 + 2]]))
        {
            return false;
        }

        //check to see which current free-space rectangles are intersected by new one
        for (Integer[] aFreeRect : freeRectList)
        {
            reduce = false;

            //see if rectangles intersect
            if ((iLeft >= aFreeRect[2]) || (iRight <= aFreeRect[0])
                    || (iTop >= aFreeRect[3]) || (iBottom <= aFreeRect[1]))
            {
            }
            else
            {
                //new rectangle intersects a free-space rectangle, which must be reduced
                //determine which edges of the current free-space rectangle are
                //intersected by the new one and form a new free-space rectangle
                //by reducing the old one to the part that is not intersected.
                //see if new rectangle completly surrounds free rectangle
                if ((iLeft <= aFreeRect[0]) && (iTop <= aFreeRect[1])
                        && (iRight >= aFreeRect[2]) && (iBottom >= aFreeRect[3]))
                {
                    //new rectangle covers this free rectangle completely -- remove it
                    reduce = true;
                }
                else
                {
                    //left-reduced?
                    if (iRight < aFreeRect[2])
                    {
                        Integer[] aRectNew = new Integer[]
                        {
                            iRight, aFreeRect[1], aFreeRect[2], aFreeRect[3]
                        };
                        new_set.add(aRectNew);
                        reduce = true;
                    }

                    //top-reduced?
                    if (iTop > aFreeRect[1])
                    {
                        Integer[] aRectNew = new Integer[]
                        {
                            aFreeRect[0], aFreeRect[1], aFreeRect[2], iTop
                        };
                        new_set.add(aRectNew);
                        reduce = true;
                    }

                    //right-reduced?
                    if (iLeft > aFreeRect[0])
                    {
                        Integer[] aRectNew = new Integer[]
                        {
                            aFreeRect[0], aFreeRect[1], iLeft, aFreeRect[3]
                        };
                        new_set.add(aRectNew);
                        reduce = true;
                    }

                    //bottom-reduced?
                    if (iBottom < aFreeRect[3])
                    {
                        Integer[] aRectNew = new Integer[]
                        {
                            aFreeRect[0], iBottom, aFreeRect[2], aFreeRect[3]
                        };
                        new_set.add(aRectNew);
                        reduce = true;
                    }
                }
            }

            //put the existing rectangle on a list of rectangles to be removed
            //if it was reduced or covered by the new one
            if (reduce)
            {
                reduced_set.add(aFreeRect);
            }
        }

        //determine which of the new free-space rectangles to keep:
        //1. only keep the reduced free-space rectangles if they are greater
        //   than the minimum size (which can be zero)
        //2. don't keep a rectangle if it is identical to one already in the list
        //   (i.e., has a lower index)
        //3. don't keep a rectangle if it is entirely within another
        //ADD:
        counter1 = -1;
        for (Integer[] aRectT1 : new_set)
        {
            counter1++;
            add = true;
            //skip if less than minimum size
            if (((aRectT1[2] - aRectT1[0]) < minSizeDim.getWidth())
                    || ((aRectT1[3] - aRectT1[1]) < minSizeDim.getHeight()))
            {
                add = false;
            }
            else
            {
                //compare to other candidate rectangles
                counter2 = -1;
                for (Integer[] aRectT2 : new_set)
                {
                    counter2++;
                    //don't compare with itself
                    if (aRectT1 != aRectT2)
                    {
                        //see if identical to another one
                        if ((Objects.equals(aRectT1[0], aRectT2[0]))
                                && (Objects.equals(aRectT1[1], aRectT2[1]))
                                && (Objects.equals(aRectT1[2], aRectT2[2]))
                                && (Objects.equals(aRectT1[3], aRectT2[3])))
                        {
                            //keep the last one if they are identical
                            if (counter1 < counter2)
                            {
                                add = false;
                                break;
                            }
                        }
                        else
                        {
                            //skip this one if it is entirely within another one
                            if ((aRectT1[0] >= aRectT2[0]) && (aRectT1[1] >= aRectT2[1])
                                    && (aRectT1[2] <= aRectT2[2]) && (aRectT1[3] <= aRectT2[3]))
                            {
                                add = false;
                                break;
                            }
                        }
                    }
                }
            }

            if (add)
            {
                mod_new_set.add(aRectT1);
            }
        }

        //form the new set of free-space rectangles
        //delete rectangles that have been reduces
        for (Integer[] freeRect : freeRectList)
        {
            del = true;
            for (Integer[] aReducedRec : reduced_set)
            {
                if ((Objects.equals(freeRect[0], aReducedRec[0]))
                        && (Objects.equals(freeRect[1], aReducedRec[1]))
                        && (Objects.equals(freeRect[2], aReducedRec[2]))
                        && (Objects.equals(freeRect[3], aReducedRec[3])))
                {
                    del = false;
                    break;
                }
            }

            if (del)
            {
                new_free.add(freeRect);
            }
        }

        //add reduced parts of old rectangles that have made it through
        //the selection process
        for (Integer[] aNewRec : mod_new_set)
        {
            new_free.add(aNewRec);
        }

        //save the new set
        freeRectList.clear();
        freeRectList.addAll(new_free);

        return true;
    }

    /**
     * Find the best location for the source rectangle. The best location is
     * returned in rntRec. The rntDist is a factor value of the distance moved
     * to new location. The rntDist is use full when in instances like boundary
     * packing is required
     *
     * @param sourceRect
     * @param rntRect
     * @param rntDist
     * @return
     */
    public boolean findLocation(Integer[] sourceRect,
            Integer[] rntRect, Integer[] rntDist)
    {
        // Declare vars ones for performance 
        int difY, difX, distance;
        int bestDist = 0;
        int counter;
        Integer[] difRect = new Integer[]
        {
            0, 0, 0, 0
        };
        Integer[] absDifRect = new Integer[]
        {
            0, 0, 0, 0
        };
        Integer[] bestRec = new Integer[]
        {
            0, 0, 0, 0
        };

//        Output.println(outputName, "Find nearest free location to contain ".
//                concat(aRect[0].toString()).concat(" ").
//                concat(aRect[1].toString()).concat(" ").
//                concat(aRect[2].toString()).concat(" ").
//                concat(aRect[3].toString()),
//                 showOutput);
        normalize(sourceRect);
        int width = sourceRect[2] - sourceRect[0];
        int height = sourceRect[3] - sourceRect[1];
//        Output.println(outputName, String.valueOf(iWidth).concat(" ").
//                concat(String.valueOf(iHeight)),
//                showOutput);

        //search every available free-space rectangle to find best fit
        for (Integer[] freeRect : freeRectList)
        {
            if ((sourceRect[0] >= freeRect[0]) && (sourceRect[1] >= freeRect[1])
                    && (sourceRect[2] <= freeRect[2]) && (sourceRect[3] <= freeRect[3]))
            {
                rntRect[0] = sourceRect[0];
                rntRect[1] = sourceRect[1];
                rntRect[2] = sourceRect[2];
                rntRect[3] = sourceRect[3];
                return true;
            }

            //see if rectangle would fit
//            Output.println(outputName, "  Check size against ".
//                    concat(String.valueOf(rntRect[2] - rntRect[0])).
//                    concat(", ").
//                    concat(String.valueOf(rntRect[3] - rntRect[1])),
//                    showOutput);
            if (width <= (freeRect[2] - freeRect[0])
                    && height <= (freeRect[3] - freeRect[1]))
            {
                //see how far rectangle would have to be moved to be placed inside
                for (counter = 0; counter <= 3; counter++)
                {
                    difRect[counter] = sourceRect[counter] - freeRect[counter];
                }
                for (counter = 0; counter <= 3; counter++)
                {
                    absDifRect[counter] = abs(difRect[counter]);
                }
                difY = 0;
                if ((difRect[1] * difRect[3]) > 0)
                {
                    if (absDifRect[1] > absDifRect[3])
                    {
                        difY = absDifRect[3];
                    }
                    else
                    {
                        difY = absDifRect[1];
                    }
                }

                difX = 0;
                if ((difRect[0] * difRect[2]) > 0)
                {
                    if (absDifRect[0] > absDifRect[2])
                    {
                        difX = absDifRect[2];
                    }
                    else
                    {
                        difX = absDifRect[0];
                    }
                }

                distance = difX * difX + difY * difY;
                if (((bestRec[0] == 0) && (bestRec[1] == 0) && (bestRec[2] == 0)
                        && (bestRec[3] == 0)) || (distance < bestDist))
                {
                    bestRec = freeRect;
                    bestDist = distance;
                }
            }
        }

        if ((bestRec[0] == 0) && (bestRec[1] == 0) && (bestRec[2] == 0)
                && (bestRec[3] == 0))
        {
            return false;
        }

        rntDist[0] = bestDist;

        //translate rectangle to nearest edge of nearest rectangle
        rntRect[0] = sourceRect[0];
        rntRect[1] = sourceRect[1];
        rntRect[2] = sourceRect[2];
        rntRect[3] = sourceRect[3];

        if (rntRect[0] < bestRec[0])
        {
            rntRect[0] = bestRec[0];
            rntRect[2] = rntRect[0] + width;
        }
        else if (rntRect[2] > bestRec[2])
        {
            rntRect[2] = bestRec[2];
            rntRect[0] = rntRect[2] - width;
        }

        if (rntRect[1] < bestRec[1])
        {
            rntRect[1] = bestRec[1];
            rntRect[3] = rntRect[1] + height;
        }
        else if (rntRect[3] > bestRec[3])
        {
            rntRect[3] = bestRec[3];
            rntRect[1] = rntRect[3] - height;
        }

        return true;
    }

    /**
     * Normalize rectangle to avoid negative values in calculations
     *
     * @param rect
     */
    private void normalize(Integer[] rect)
    {
        int i;
        Integer[] rectTemp;

        rectTemp = rect;
        for (i = 0; i <= 1; i++)
        {
            if (rect[i] > rect[OPPOSITE[i]])
            {
                rect[i] = rectTemp[OPPOSITE[i]];
                rect[OPPOSITE[i]] = rectTemp[i];
            }
        }
    }

    /**
     * Produce a new normalized rectangle from input rectangle. Use temp
     * variable to avoid zero value issues.
     *
     * @param rect
     * @return
     */
    private Integer[] normalized(Integer[] rect)
    {
        int iTemp;
        int iLeft = rect[0];
        int iTop = rect[1];
        int iRight = rect[2];
        int iBottom = rect[3];

        if (iLeft > iRight)
        {
            iTemp = iLeft;
            iLeft = iRight;
            iRight = iTemp;
        }
        if (iTop > iBottom)
        {
            iTemp = iBottom;
            iBottom = iTop;
            iTop = iTemp;
        }

        Integer[] rntRect = new Integer[]
        {
            iLeft, iTop, iRight, iBottom
        };

        return rntRect;
    }

    /**
     * Set the minimum size of a input rectangle
     *
     * @param iHeight
     * @param iWidth
     */
    public void setMinimumSize(int iHeight, int iWidth)
    {
        minSizeDim.setHeight(iHeight);
        minSizeDim.setWidth(iWidth);
    }

    /**
     * Set the area of interest
     *
     * @param spRect
     */
    public void setSpaceRect(Integer[] spRect)
    {
        spaceRect = spRect;
        freeRectList.add(spRect);
    }

    /**
     * Sort the free space on size to speed up finding space
     */
    public void sortBeforeFind()
    {
        sort(freeRectList, new Comparator()
        {
            @Override
            public int compare(Object o1, Object o2)
            {
                long iComp;
                iComp = ((Integer[]) o1)[0].compareTo(((Integer[]) o2)[0]);
                if (iComp == 0)
                {
                    iComp = ((Integer[]) o1)[1].compareTo(((Integer[]) o2)[1]);
                }
                return (int) iComp;
            }
        });
    }

    /**
     * Record of size/dimensions
     */
    private class DimensionRec
    {
        private Integer height, width;

        public Integer getHeight()
        {
            return height;
        }

        public void setHeight(Integer height)
        {
            this.height = height;
        }

        public Integer getWidth()
        {
            return width;
        }

        public void setWidth(Integer width)
        {
            this.width = width;
        }
    }
}
