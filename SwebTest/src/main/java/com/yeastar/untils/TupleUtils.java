package com.yeastar.untils;

import org.javatuples.*;

/**
 * ClassName: TupleUtils
 * Function:
 * <p>
 * Tuple helper to create numerous items of tuple. the maximum is 10.
 * if you want to create tuple which elements count  more than 10, a new class would be a better choice.
 * if you don't want to new a class, just extends the class {@link org.javatuples.Tuple} and do your own implemention.
 * </p>
 * date: 2019/9/2 16:16
 *
 * Unit<A> (1 element)
 * Pair<A,B> (2 elements)
 * Triplet<A,B,C> (3 elements)
 * Quartet<A,B,C,D> (4 elements)
 * Quintet<A,B,C,D,E> (5 elements)
 * Sextet<A,B,C,D,E,F> (6 elements)
 * Septet<A,B,C,D,E,F,G> (7 elements)
 * Octet<A,B,C,D,E,F,G,H> (8 elements)
 * Ennead<A,B,C,D,E,F,G,H,I> (9 elements)
 * Decade<A,B,C,D,E,F,G,H,I,J> (10 elements)
 *
 * @version 1.0.0
 * @author Chavaer
 * @since JDK 1.8
 */
public class TupleUtils {

    /**
     * <p>Create a tuple of one element.</p>
     *
     * @param value0
     * @param <A>
     *
     * @return a tuple of one element
     */
    public static <A> Unit<A> with(final A value0) {

        return Unit.with(value0);
    }

    /**
     * <p>Create a tuple of two elements.</p>
     *
     * @param value0
     * @param value1
     * @param <A>
     * @param <B>
     *
     * @return a tuple of two elements
     */
    public static <A, B> Pair<A, B> with(final A value0, final B value1) {

        return Pair.with(value0, value1);
    }

    /**
     * <p>Create a tuple of three elements.</p>
     *
     * @param value0
     * @param value1
     * @param value2
     * @param <A>
     * @param <B>
     * @param <C>
     *
     * @return a tuple of three elements
     */
    public static <A, B, C> Triplet<A, B, C> with(final A value0, final B value1, final C value2) {

        return Triplet.with(value0, value1, value2);
    }

    /**
     * <p>Create a tuple of four elements.</p>
     *
     * @param <A>
     * @param <B>
     * @param <C>
     *
     * @param value0
     * @param value1
     * @param value2
     * @return a tuple of three elements
     */
    public static <A, B, C, D> Quartet<A, B, C, D> with(final A value0, final B value1, final C value2, final D value3) {

        return Quartet.with(value0, value1, value2, value3);
    }

    /**
     * <p>Create a tuple of five elements.</p>
     *
     * @param <A>
     * @param <B>
     * @param <C>
     *
     * @param value0
     * @param value1
     * @param value2
     * @return a tuple of three elements
     */
    public static <A, B, C, D, E> Quintet<A, B, C, D, E> with(final A value0, final B value1, final C value2, final D value3, final E value4) {

        return Quintet.with(value0, value1, value2, value3, value4);
    }



}
