package cz.green.util;

/**
 * This class supports only some constants and methods for working with intervals.
 * Can't be instantiate, because it has no sense to make this class instance. Has
 * no instance mothods and no atributes.
 */
public abstract class IntervalMethods {
    /**
     * "Position" constants - bit informatting about "left" property.
     * Value is <code>1</code>.
     */
    public final static int LEFT = 1;
    /**
     * "Position" constants - bit informatting about "in" property.
     * Value is <code>2</code>.
     */
    public final static int IN = 2;
    /**
     * "Position" constants same as <code>IN</code> - bit informatting
     * about "in" property.
     *
     * @see IntervalMethods.IN
     */
    public final static int MIDDLE = 2;
    /**
     * "Position" constants same as <code>IN</code> - bit informatting
     * about "in" property.
     *
     * @see IntervalMethods.IN
     */
    public final static int BEHIND = 2;
    /**
     * "Position" constants - bit informatting about "right" property.
     * Value is <code>4</code>.
     */
    public final static int RIGHT = 4;

    /**
     * Tests if the <code>interval1</code> has penetration with
     * the <code>interval2</code>.
     *
     * @param <code>interval1</code> one dimensional array with 2 items, where
     *                               item <code>interval1[0]</code> gives the beggining and item
     *                               <code>interval1[1]</code> the end of the interval1
     * @param <code>interval2</code> determines 2nd interval, with same
     *                               meaning as the 1st interval
     * @return <code>true</code> if the <code>interval1</code> has
     *         intersection with the <code>interval2</code> and false, if it doesn't
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval1</code> or <code>interval2</code>
     *          has less than 2 items, on the other hand if the array has more
     *          items -> it doesn't matter.
     */
    public static boolean hasPenetration(int[] interval1, int[] interval2)
            throws BadDimensionException {
        try {
            return (interval1[0] <= interval2[1]) && (interval1[1] >= interval2[0]);
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(
                    (interval1.length < interval2.length) ? interval1.length : interval2.length, 2);
        }
    }

    /**
     * Tests if the <code>point</code> is in the <code>interval</code>.
     *
     * @param <code>interval</code> one dimensional array with 2 items, where
     *                              item <code>interval[0]</code> gives the begining and item
     *                              <code>interval[1]</code> the end of the interval
     * @param <code>point</code>    this point we test, if it is in the interval
     * @return <code>boolean</code>
     *         return <code>true</code> if the <code>point</code> is in the
     *         <code>interval</code> and false, if it doesn't
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     */
    public static boolean isIn(int[] interval, int point) throws BadDimensionException {
        try {
            return (interval[0] <= point) && (interval[1] >= point);
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
    }

    /**
     * Moves <code>interval</code> by <code>difference</code>.
     * To <code>interval[0]</code> and <code>interval[1]</code>
     * adds <code>difference</code>
     *
     * @param <code>interval</code>   one dimensional array with 2 items, where
     *                                item <code>interval[0]</code> gives the begining and item
     *                                <code>interval[1]</code> the end of the interval
     * @param <code>difference</code> number, by what have to move
     *                                <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     */
    public static void moveBy(int[] interval, int difference) throws BadDimensionException {
        try {
            interval[0] += difference;
            interval[1] += difference;
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
    }

    /**
     * Moves end of the <code>interval</code> to <code>point</code>.
     * The size of the <code>interval</code> stays the same.
     *
     * @param <code>interval</code> one dimensional array with 2 items, where
     *                              item <code>interval[0]</code> gives the begining and item
     *                              <code>interval[1]</code> the end of the interval
     * @param <code>point</code>    point, where will be the end
     *                              of the <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     */
    public static void moveEndTo(int[] interval, int point) throws BadDimensionException {
        try {
            point -= interval[1];
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
        moveBy(interval, point);
    }

    /**
     * Moves beginning of the <code>interval</code> to <code>point</code>.
     * The size of the <code>interval</code> stays the same.
     *
     * @param <code>interval</code> one dimensional array with 2 items, where
     *                              item <code>interval[0]</code> gives the begining and item
     *                              <code>interval[1]</code> the end of the interval
     * @param <code>point</code>    point, where will be the beginning
     *                              of the <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     */
    public static void moveStartTo(int[] interval, int point) throws BadDimensionException {
        try {
            point -= interval[0];
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
        moveBy(interval, point);
    }

    /**
     * Change size of the <code>interval</code> by <code>difference</code>.
     * To <code>interval[1]</code> adds <code>difference</code>. Same as
     * function <code>resizeEndBy(interval, difference)</code>.
     *
     * @param <code>interval</code>   one dimensional array with 2 items, where
     *                                item <code>interval[0]</code> gives the beggining and item
     *                                <code>interval[1]</code> the end of the interval
     * @param <code>difference</code> number, by what have to resize
     *                                <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.util.WrongIntervalException</code>
     *          if after resizing the <code>interval</code> will arise wrong interval.
     *          The start point will be greater than end point.
     */
    public static void resizeBy(int[] interval, int difference) throws BadDimensionException, WrongIntervalException {
        resizeEndBy(interval, difference);
    }

    /**
     * Change size of the <code>interval</code> by <code>difference</code>.
     * To <code>interval[1]</code> adds <code>difference</code>.
     *
     * @param <code>interval</code>   one dimensional array with 2 items, where
     *                                item <code>interval[0]</code> gives the begining and item
     *                                <code>interval[1]</code> the end of the interval
     * @param <code>difference</code> number, by what have to resize
     *                                <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.util.WrongIntervalException</code>
     *          if after resizing the <code>interval</code> will arise wrong interval.
     *          The start point will be greater than end point.
     */
    public static void resizeEndBy(int[] interval, int difference) throws BadDimensionException, WrongIntervalException {
        try {
            difference += interval[1];
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
        resizeEndTo(interval, difference);
    }

    /**
     * Change size of the <code>interval</code> by moving end point to
     * <code>point</code>.
     *
     * @param <code>interval</code> one dimensional array with 2 items, where
     *                              item <code>interval[0]</code> gives the begining and item
     *                              <code>interval[1]</code> the end of the interval
     * @param <code>point</code>    point, where to move end point of the
     *                              <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.util.WrongIntervalException</code>
     *          if after resizing the <code>interval</code> will arise wrong interval.
     *          The start point will be greater than end point.
     */
    public static void resizeEndTo(int[] interval, int point) throws BadDimensionException, WrongIntervalException {
        try {
            if (interval[0] > point)
                throw new WrongIntervalException();
            interval[1] = point;
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
    }

    /**
     * Change size of the <code>interval</code> by <code>difference</code>.
     * To <code>interval[0]</code> adds <code>difference</code>.
     *
     * @param <code>interval</code>   one dimensional array with 2 items, where
     *                                item <code>interval[0]</code> gives the begining and item
     *                                <code>interval[1]</code> the end of the interval
     * @param <code>difference</code> number, by what have to resize
     *                                <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.util.WrongIntervalException</code>
     *          if after resizing the <code>interval</code> will arise wrong interval.
     *          The start point will be greater than end point.
     */
    public static void resizeStartBy(int[] interval, int difference) throws BadDimensionException, WrongIntervalException {
        try {
            difference += interval[0];
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
        resizeStartTo(interval, difference);
    }

    /**
     * Change size of the <code>interval</code>. Moves start point of the
     * <code>interval</code> to <code>point</code>.
     *
     * @param <code>interval</code> one dimensional array with 2 items, where
     *                              item <code>interval[0]</code> gives the begining and item
     *                              <code>interval[1]</code> the end of the interval
     * @param <code>point</code>    point, where to move start point of the
     *                              <code>interval</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.util.WrongIntervalException</code>
     *          if after resizing the <code>interval</code> will arise wrong interval.
     *          The start point will be greater than end point.
     */
    public static void resizeStartTo(int[] interval, int point) throws BadDimensionException, WrongIntervalException {
        try {
            if (point > interval[1])
                throw new WrongIntervalException();
            interval[0] = point;
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
    }

    /**
     * Return the size of the <code>interval</code>.
     *
     * @param <code>interval</code> one dimensional array with 2 items, where
     *                              item <code>interval[0]</code> gives the begining and item
     *                              <code>interval[1]</code> the end of the interval
     * @return <code>int</code>
     *         the size
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     * @throws <code>cz.green.util.WrongIntervalException</code>
     *          if the <code>interval</code> has start point greater than end point.
     */
    public static int size(int[] interval) throws BadDimensionException, WrongIntervalException {
        int size = 0;
        try {
            if ((size = interval[1] - interval[0]) < 0)
                throw new WrongIntervalException();
            return size;
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(interval.length, 2);
        }
    }

    /**
     * Gives information where lies the <code>interval2</code> according
     * to the <code>interval1</code>.
     *
     * @param <code>interval1</code> one dimensional array with 2 items, where
     *                               item <code>interval1[0]</code> gives the begining and item
     *                               <code>interval1[1]</code> the end of the interval1
     * @param <code>interval2</code> determines 2nd interval, with same
     *                               meaning as the 1st interval
     * @return <code>int</code>
     *         return is composition of caonstants <code>LEFT</code>, <code>IN</code>
     *         or <code>RIGHT</code>. If it return <code>IN</code> it means,
     *         that interval2 is less or equal to the <code>interval1</code>
     *         and all point from <code>interval2</code> lies also in
     *         <code>interval1</code>.
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval1</code> or <code>interval2</code>
     *          has less than 2 items, on the other hand if the array has more
     *          items -> it doesn't matter.
     */
    public static int whereIs(int[] interval1, int[] interval2) throws BadDimensionException {
        int result = 0;
        try {
            if ((interval1[0] <= interval2[1]) && (interval1[1] >= interval2[0]))
                result |= IN;
            if (interval2[0] < interval1[0])
                result |= LEFT;
            if (interval2[1] > interval1[1])
                result |= RIGHT;
            return result;
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException(
                    (interval1.length < interval2.length) ? interval1.length : interval2.length, 2);
        }
    }

    /**
     * Gives information where lies the <code>point</code> according
     * to the <code>interval</code>. Int the <code>interval</code>
     * or on the right (left) side from it.
     *
     * @param <code>interval</code> one dimensional array with 2 items, where
     *                              item <code>interval[0]</code> gives the begining and item
     *                              <code>interval[1]</code> the end of the interval1
     * @param <code>point</code>    this point, that position we have to know
     * @return <code>int</code>
     *         return defined constant <code>LEFT</code>, <code>IN</code>
     *         or <code>RIGHT</code> according to the position of the
     *         <code>point</code>
     * @throws <code>cz.green.util.BadDimensionException</code>
     *          if array <code>interval</code> has less than 2 items,
     *          on the other hand if the array <code>interval</code> has more
     *          items -> it doesn't matter.
     */
    public static int whereIs(int[] interval, int point) throws BadDimensionException {
        try {
            if (interval[0] > point)
                return LEFT;
            if (interval[1] < point)
                return RIGHT;
            return IN;
        } catch (IndexOutOfBoundsException e) {
            throw new BadDimensionException( interval.length, 2 );
	}
}
}
