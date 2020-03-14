package com.ahzak.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/11/20 9:58
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class CollectionUtil extends cn.hutool.core.collection.CollectionUtil {

    /**
     * 根据集合中对象的某个方法的返回值是否和给定的值相等, 来判断集合中是否存在符合要求的对象
     *
     * @param collection 集合
     * @param function   方法
     * @param value      值
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/11/20 10:04
     */
    public static <T> boolean contains(final Collection<T> collection, final Function<T, Object> function, final Object value) {
        return contains(collection, ele -> Objects.equals(function.apply(ele), value));
    }

    public static <T> boolean contains(final Collection<T> collection, final Predicate<T> predicate) {
        for (T ele : collection) {
            if (ele != null && predicate.test(ele)) {
                return true;
            }
        }
        return false;
    }


    public static <T> T get(final Collection<T> collection, final Function<T, Object> function, final Object value) {
        return get(collection, ele -> Objects.equals(function.apply(ele), value));
    }

    public static <T> T get(final Collection<T> collection, final Predicate<T> predicate) {
        for (T ele : collection) {
            if (ele != null && predicate.test(ele)) {
                return ele;
            }
        }
        return null;
    }

    public static <T> Integer getIndex(final List<T> list, final Predicate<T> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && predicate.test(list.get(i))) {
                return i;
            }
        }
        return null;
    }


    public static <T> List<T> sub(final Collection<T> collection, final Function<T, Object> function, final Object value) {
        return sub(collection, ele -> Objects.equals(function.apply(ele), value));
    }

    public static <T> List<T> sub(final Collection<T> collection, final Predicate<T> predicate) {
        List<T> list = new ArrayList<>();

        for (T ele : collection) {
            if (ele != null && predicate.test(ele)) {
                list.add(ele);
            }
        }

        return list;
    }

    public static <T> T computeIfAbsent(Collection<T> collection, Function<T, Object> function, Object value, Supplier<T> supplier) {
        T ret = get(collection, function, value);
        if (ret == null) {
            ret = supplier.get();
            collection.add(ret);
        }
        return ret;
    }

    /**
     * 笛卡尔积
     *
     * @param lists
     * @return void
     * @author Zhu Kaixiao
     * @date 2019/11/29 18:03
     */
    public static <T> List<List<T>> cartesianProduct(final List<List<T>> lists) {
        T[][] arrays = (T[][]) new Object[lists.size()][];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = (T[]) lists.get(i).toArray();
        }

        return cartesianProduct(arrays);
    }

    public static <T> List<List<T>> cartesianProduct(final T[][] arrays) {
        int[][] indexAndLength = new int[2][arrays.length];

        for (int i = 0; i < arrays.length; i++) {
            indexAndLength[0][i] = 0;
            indexAndLength[1][i] = arrays[i].length;
        }

        List<List<T>> cartesianProductList = new ArrayList<>();
        getOptions(arrays, indexAndLength, cartesianProductList);
        return cartesianProductList;
    }


    private static <T> void getOptions(final T[][] arrays, int[][] indexAndLength, List<List<T>> cartesianProductList) {
        List<T> ret = new ArrayList<>(arrays.length);
        cartesianProductList.add(ret);
        for (int i = 0; i < arrays.length; i++) {
            ret.add(arrays[i][indexAndLength[0][i]]);
        }

        if (addIndex(indexAndLength, arrays.length)) {
            getOptions(arrays, indexAndLength, cartesianProductList);
        }
    }

    private static boolean addIndex(int[][] indexAndLength, int index) {
        if (index <= 0) {
            return false;
        }

        if ((indexAndLength[0][index - 1] += 1) < indexAndLength[1][index - 1]) {
            return true;
        }
        indexAndLength[0][index - 1] = 0;
        return addIndex(indexAndLength, index - 1);
    }


//    public static void main(String[] args) {
//        List<List<Integer>> lists = new ArrayList<>();
//        List<Integer> list1 = Arrays.asList(1, 2, 3);
//        List<Integer> list2 = Arrays.asList(4, 5, 6);
//        lists.add(list1);
//        lists.add(list2);
//        List<List<Integer>> lists1 = cartesianProduct(lists);
//
//        Integer[][] aa = new Integer[2][];
//        aa[0] = new Integer[]{1, 2, 3};
//        aa[1] = new Integer[]{4, 5, 6};
//        List<List<Integer>> lists2 = cartesianProduct(aa);
//        System.out.println(1);
//    }

    /**
     * 判断集合是否全为null
     *
     * @param collection Collection
     * @return
     */
    public static boolean isAllNull(final Collection<?> collection) {
        for (Object o : collection) {
            if (o != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在两个不同的泛型的集合中根据指定的比较器比较两个集合中的元素是否相同
     *
     * @param collection1 集合1
     * @param collection2 集合2
     * @param comparator  比较器
     * @param sameSize    是否规定两个集合的大小相同  如果不规定,则以较小的集合为主进行比较
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/12/26 17:43
     */
    public static <E1, E2> boolean crossClassMatch(Collection<E1> collection1, Collection<E2> collection2, CrossClassComparator<E1, E2> comparator, boolean sameSize) {

        if (sameSize && collection1.size() != collection2.size()) {
            return false;
        }

        final Iterator<E1> iterator1 = collection1.iterator();
        final Iterator<E2> iterator2 = collection2.iterator();
        Iterator mainIterator = collection1.size() < collection2.size()
                ? iterator1
                : iterator2;
        while (mainIterator.hasNext()) {
            if (comparator.compare(iterator1.next(), iterator2.next()) != 0) {
                return false;
            }
        }

        return true;

    }

    /**
     * 在两个不同的泛型的集合中根据指定的比较器比较两个集合中的元素是否相同
     *
     * @param collection1
     * @param collection2
     * @param comparator
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/12/26 17:55
     */
    public static <E1, E2> boolean crossClassMatch(Collection<E1> collection1, Collection<E2> collection2, CrossClassComparator<E1, E2> comparator) {
        return crossClassMatch(collection1, collection2, comparator, true);
    }

    /**
     * 在两个不同的泛型的集合中根据指定的比较器比较两个集合中的元素是否相同
     * 但是不要求相同的对象所在的下标也一样
     *
     * @param collection1
     * @param collection2
     * @param comparator
     * @param sameSize
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/12/26 18:07
     */
    public static <E1, E2> boolean crossClassBroadMatch(Collection<E1> collection1, Collection<E2> collection2,
                                                        CrossClassComparator<E1, E2> comparator, boolean sameSize) {
        if (sameSize && collection1.size() != collection2.size()) {
            return false;
        }


        boolean flg = collection1.size() < collection2.size()
                ? true
                : false;

        List list = new ArrayList();
        if (flg) {
            for (E1 e1 : collection1) {
                for (E2 e2 : collection2) {
                    boolean eq = false;
                    if (comparator.compare(e1, e2) == 0) {
                        if (contains(list, o -> o == e2)) {
                            continue;
                        }
                        list.add(e2);
                        eq = true;
                    }
                    if (eq == false) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        } else {
            for (E2 e2 : collection2) {
                for (E1 e1 : collection1) {
                    boolean eq = false;
                    if (comparator.compare(e1, e2) == 0) {
                        if (contains(list, o -> o == e1)) {
                            continue;
                        }
                        list.add(e1);
                        eq = true;
                    }
                    if (eq == false) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        }


        return true;
    }


    public static <E1, E2> boolean crossClassBroadMatch(Collection<E1> collection1, Collection<E2> collection2,
                                                        CrossClassComparator<E1, E2> comparator) {
        return crossClassBroadMatch(collection1, collection2, comparator, true);
    }


    public static <E> Collection<E> removeAny(Collection<E> collection, Predicate<E> predicate) {
        final List<E> sub = sub(collection, predicate);
        collection.removeAll(sub);
        return collection;
    }

    /**
     * 将targetList中的元素安装sourceList中的元素的顺序进行排序
     *
     * @param sourceList 源list
     * @param targetList 目标list
     * @param function1  源list中元素顺序的参考字段
     * @param function2  目标list中元素顺序的参考字段
     * @return java.util.List<E2>
     * @author Zhu Kaixiao
     * @date 2019/12/27 16:07
     */
    public static <E1, E2> List<E2> shadowSort(
            final List<E1> sourceList, final Function<E1, Object> function1,
            final List<E2> targetList, final Function<E2, Object> function2
    ) {
        for (int i = 0, sortIndex = 0; i < sourceList.size() && sortIndex < targetList.size(); i++) {
            final E1 e1 = sourceList.get(i);
            Integer index = getIndex(targetList, e2 -> Objects.equals(function1.apply(e1), function2.apply(e2)));
            if (index == null) {
                continue;
            }

            if (index != sortIndex) {
                final E2 temp1 = targetList.get(sortIndex);
                final E2 temp2 = targetList.get(index);
                targetList.set(sortIndex, temp2);
                targetList.set(index, temp1);
            }

            sortIndex++;
        }

        return targetList;
    }


    /**
     * 两个不同的类的对象进行比较
     */
    @FunctionalInterface
    public interface CrossClassComparator<T1, T2> {
        /**
         * 相同返回0 o1比o2小返回负数, o1比o2大返回正数
         *
         * @param o1
         * @param o2
         * @return int
         * @author Zhu Kaixiao
         * @date 2019/12/26 17:46
         */
        int compare(T1 o1, T2 o2);
    }
}

