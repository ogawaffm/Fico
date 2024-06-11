package com.ogawa.fico.function;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;

public class Suppliers {

    /**
     * Constantly returns null.
     *
     * @param <T> Return type of the supplier
     * @return I1 Supplier that constantly returns {@code null}.
     */
    public static <T> Supplier<T> ofNull() {
        return () -> (T) null;
    }

    /**
     * Creates a {@link java.util.function.Supplier} of a constant value.
     *
     * @param <T> the type
     * @param t   the constant value to return
     * @return I1 supplier that constantly returns t.
     */
    public static <T> Supplier<T> of(@NonNull final T t) {
        return () -> t;
    }

    public static Supplier<Boolean> of(final boolean b) {
        return b ? () -> true : () -> false;
    }

    public static Supplier<String> of(@NonNull final String s) {
        return s.isEmpty() ? () -> "" : () -> s;
    }

    public static <A, R> Supplier<R> fromFunction(final Function<? super A, ? extends R> f, final A a) {
        return () -> f.apply(a);
    }

    public static <A1, A2, R> Supplier<R> fromFunction(final BiFunction<? super A1, A2, ? extends R> f,
        final A1 a1, final A2 a2) {
        return () -> f.apply(a1, a2);
    }

    public static <A1, A2, A3, R> Supplier<R> fromFunction(final Function3<? super A1, A2, A3, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3) {
        return () -> f.apply(a1, a2, a3);
    }

    public static <A1, A2, A3, A4, R> Supplier<R> fromFunction(final Function4<? super A1, A2, A3, A4, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3, final A4 a4) {
        return () -> f.apply(a1, a2, a3, a4);
    }

    public static <A1, A2, A3, A4, A5, R> Supplier<R> fromFunction(
        final Function5<? super A1, A2, A3, A4, A5, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5) {
        return () -> f.apply(a1, a2, a3, a4, a5);
    }

    public static <A1, A2, A3, A4, A5, A6, R> Supplier<R> fromFunction(
        final Function6<? super A1, A2, A3, A4, A5, A6, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5, final A6 a6) {
        return () -> f.apply(a1, a2, a3, a4, a5, a6);
    }

    public static <A1, A2, A3, A4, A5, A6, A7, R> Supplier<R> fromFunction(
        final Function7<? super A1, A2, A3, A4, A5, A6, A7, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5, final A6 a6, final A7 a7) {
        return () -> f.apply(a1, a2, a3, a4, a5, a6, a7);
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, R> Supplier<R> fromFunction(
        final Function8<? super A1, A2, A3, A4, A5, A6, A7, A8, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5, final A6 a6, final A7 a7, final A8 a8) {
        return () -> f.apply(a1, a2, a3, a4, a5, a6, a7, a8);
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, R> Supplier<R> fromFunction(
        final Function9<? super A1, A2, A3, A4, A5, A6, A7, A8, A9, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5, final A6 a6, final A7 a7, final A8 a8,
        final A9 a9) {
        return () -> f.apply(a1, a2, a3, a4, a5, a6, a7, a8, a9);
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R> Supplier<R> fromFunction(
        final Function10<? super A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, ? extends R> f,
        final A1 a1, final A2 a2, final A3 a3, final A4 a4, final A5 a5, final A6 a6, final A7 a7, final A8 a8,
        final A9 a9, final A10 a10) {
        return () -> f.apply(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
    }


    public static <S extends Supplier<A>, A, R> Supplier<R> fromSuppliedFunction(
        final Function<? super A, ? extends R> f, final S a) {
        return () -> f.apply(a.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, R> Supplier<R> fromSuppliedFunction(
        final BiFunction<? super A1, A2, ? extends R> f,
        final S1 s1, final S2 s2) {
        return () -> f.apply(s1.get(), s2.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, R> Supplier<R> fromSuppliedFunction(
        final Function3<? super A1, A2, A3, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3) {
        return () -> f.apply(s1.get(), s2.get(), s3.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, S4 extends Supplier<A4>, A4, R> Supplier<R> fromSuppliedFunction(
        final Function4<? super A1, A2, A3, A4, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3, final S4 s4) {
        return () -> f.apply(s1.get(), s2.get(), s3.get(), s4.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, S4 extends Supplier<A4>, A4, S5 extends Supplier<A5>, A5, R> Supplier<R> fromSuppliedFunction(
        final Function5<? super A1, A2, A3, A4, A5, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3, final S4 s4, final S5 s5) {
        return () -> f.apply(s1.get(), s2.get(), s3.get(), s4.get(), s5.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, S4 extends Supplier<A4>, A4, S5 extends Supplier<A5>, A5, S6 extends Supplier<A6>, A6, R> Supplier<R> fromSuppliedFunction(
        final Function6<? super A1, A2, A3, A4, A5, A6, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3, final S4 s4, final S5 s5, final S6 s6) {
        return () -> f.apply(s1.get(), s2.get(), s3.get(), s4.get(), s5.get(), s6.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, S4 extends Supplier<A4>, A4, S5 extends Supplier<A5>, A5, S6 extends Supplier<A6>, A6, S7 extends Supplier<A7>, A7, R> Supplier<R> fromSuppliedFunction(
        final Function7<? super A1, A2, A3, A4, A5, A6, A7, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3, final S4 s4, final S5 s5, final S6 s6, final S7 s7) {
        return () -> f.apply(s1.get(), s2.get(), s3.get(), s4.get(), s5.get(), s6.get(), s7.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, S4 extends Supplier<A4>, A4, S5 extends Supplier<A5>, A5, S6 extends Supplier<A6>, A6, S7 extends Supplier<A7>, A7, S8 extends Supplier<A8>, A8, R> Supplier<R> fromSuppliedFunction(
        final Function8<? super A1, A2, A3, A4, A5, A6, A7, A8, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3, final S4 s4, final S5 s5, final S6 s6, final S7 s7, final S8 s8) {
        return () -> f.apply(s1.get(), s2.get(), s3.get(), s4.get(), s5.get(), s6.get(), s7.get(), s8.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, S4 extends Supplier<A4>, A4, S5 extends Supplier<A5>, A5, S6 extends Supplier<A6>, A6, S7 extends Supplier<A7>, A7, S8 extends Supplier<A8>, A8, S9 extends Supplier<A9>, A9, R> Supplier<R> fromSuppliedFunction(
        final Function9<? super A1, A2, A3, A4, A5, A6, A7, A8, A9, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3, final S4 s4, final S5 s5, final S6 s6, final S7 s7, final S8 s8,
        final S9 s9) {
        return () -> f.apply(s1.get(), s2.get(), s3.get(), s4.get(), s5.get(), s6.get(), s7.get(), s8.get(), s9.get());
    }

    public static <S1 extends Supplier<A1>, A1, S2 extends Supplier<A2>, A2, S3 extends Supplier<A3>, A3, S4 extends Supplier<A4>, A4, S5 extends Supplier<A5>, A5, S6 extends Supplier<A6>, A6, S7 extends Supplier<A7>, A7, S8 extends Supplier<A8>, A8, S9 extends Supplier<A9>, A9, S10 extends Supplier<A10>, A10, R> Supplier<R> fromSuppliedFunction(
        final Function10<? super A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, ? extends R> f,
        final S1 s1, final S2 s2, final S3 s3, final S4 s4, final S5 s5, final S6 s6, final S7 s7, final S8 s8,
        final S9 s9, final S10 s10) {
        return () -> f.apply(s1.get(), s2.get(), s3.get(), s4.get(), s5.get(), s6.get(), s7.get(), s8.get(), s9.get(),
            s10.get());
    }


    /* ***************************************************************************************************************** */

    /**
     * Create a new {@link java.util.function.Supplier} by transforming the result calling the originalSupplier
     * {@link java.util.function.Supplier}
     *
     * @param tranformationFunction function to tranformationFunction the result of a
     *                              {@link java.util.function.Supplier} of I1's to B's
     * @param originalSupplier      a {@link java.util.function.Supplier} of I1's
     * @param <T>                   return type of the {@link java.util.function.Supplier} to tranformationFunction
     * @param <R>                   return type of the new {@link java.util.function.Supplier}
     * @return a new {@link java.util.function.Supplier} returning B's
     */
    public static <T, R> Supplier<R> compose(final Supplier<T> originalSupplier,
        final Function<? super T, R> tranformationFunction) {
        return () -> tranformationFunction.apply(originalSupplier.get());
    }

    /**
     * Performs function application within a supplier (applicative functor pattern).
     *
     * @param functionArgumentSupplier supplier
     * @param functionSupplier         The Supplier function to apply.
     * @return I1 new Supplier logAfter applying the given Supplier function to the first argument.
     */
    public static <T, R> Supplier<R> ap(final Supplier<T> functionArgumentSupplier,
        final Supplier<Function<T, R>> functionSupplier) {
        return () -> functionSupplier.get().apply(functionArgumentSupplier.get());
    }

    /**
     * Returns a supplier that constantly supplies the value of the optional if present, otherwise null.
     *
     * @param <T>      Return type of the supplier
     * @param optional The optional to turn into a supplier
     * @return a {@link java.util.function.Supplier} that constantly supplies the value of the optional if present,
     * otherwise null.
     */
    public static <T> Supplier<T> ofUnwrapped(final Optional<T> optional) {
        return optional.isPresent() ? of(optional.get()) : ofNull();
    }

    /**
     * I1 supplier that memoize the value return by another {@link java.util.function.Supplier}, whose
     * {@link java.util.function.Supplier#get()} method is guaranteed to be call at most once. The returned
     * {@link java.util.function.Supplier} is thread-safe
     *
     * @param <A>      the type
     * @param supplier the supplier to memoize
     * @return the memoizing supplier
     */
    public static <A> Supplier<A> memoize(final Supplier<A> supplier) {
        return supplier instanceof MemoizingSupplier ? supplier
            : new MemoizingSupplier<>(Objects.requireNonNull(supplier));
    }

    /**
     * I1 supplier that weakly memoize the value return by another {@link java.util.function.Supplier} , The returned
     * {@link java.util.function.Supplier} is thread-safe
     *
     * @param <A>      the type
     * @param supplier the supplier to memoize
     * @return the weakly memoizing supplier
     */
    public static <A> Supplier<A> weakMemoize(final Supplier<A> supplier) {
        return supplier instanceof WeakMemoizingSupplier || supplier instanceof MemoizingSupplier ? supplier
            : new WeakMemoizingSupplier<>(
                Objects.requireNonNull(supplier));
    }

    private static final class MemoizingSupplier<T> implements Supplier<T> {

        private volatile Supplier<T> delegate;

        private T t;

        MemoizingSupplier(final Supplier<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            // double Checked Locking
            if (delegate != null) {
                synchronized (this) {
                    if (delegate != null) {
                        final T res;
                        this.t = res = delegate.get();
                        delegate = null;
                        return res;
                    }
                }
            }
            return t;
        }
    }

    private static final class WeakMemoizingSupplier<T> implements Supplier<T> {

        private final Supplier<T> delegate;

        // Contains the value from delegate.
        private volatile WeakReference<T> value;

        WeakMemoizingSupplier(final Supplier<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            T t = value == null ? null : value.get();
            // double Checked Locking
            if (t == null) {
                synchronized (this) {
                    t = value == null ? null : value.get();
                    if (t == null) {
                        t = delegate.get();
                        value = new WeakReference<T>(t);
                    }
                }
            }
            return t;
        }
    }
}
