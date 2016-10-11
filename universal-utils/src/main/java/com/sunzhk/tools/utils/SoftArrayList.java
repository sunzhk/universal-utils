package com.sunzhk.tools.utils;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * 随便写的软引用ArrayList，每个child都被保存在一个软引用中以免内存泄漏
 * @author sunzhk
 *
 * @param <E> 保存的对象类型
 */
public class SoftArrayList<E> extends AbstractList<E> implements Cloneable, Serializable, RandomAccess {

	private static final long serialVersionUID = 524139671117719159L;

	public static final Object[] OBJECT = new Object[0];
	
    private static final int MIN_CAPACITY_INCREMENT = 12;

    int size;

    transient Object[] array;

	public SoftArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        }
        array = (capacity == 0 ? OBJECT : new Object[capacity]);
    }

	public SoftArrayList() {
        array = OBJECT;
    }

	public SoftArrayList(Collection<? extends SoftReference<E>> collection) {
        if (collection == null) {
            throw new NullPointerException("collection == null");
        }

        Object[] a = collection.toArray();
        if (a.getClass() != Object[].class) {
            Object[] newArray = new Object[a.length];
            System.arraycopy(a, 0, newArray, 0, a.length);
            a = newArray;
        }
        array = a;
        size = a.length;
    }

	@Override
	public boolean add(E object) {
        Object[] a = array;
        int s = size;
        if (s == a.length) {
            Object[] newArray = new Object[s +
                    (s < (MIN_CAPACITY_INCREMENT / 2) ?
                     MIN_CAPACITY_INCREMENT : s >> 1)];
            System.arraycopy(a, 0, newArray, 0, s);
            array = a = newArray;
        }
        a[s] = new SoftReference<E>(object);
        size = s + 1;
        modCount++;
        return true;
    }

	@Override
	public void add(int index, E object) {
        Object[] a = array;
        int s = size;
        if (index > s || index < 0) {
            throwIndexOutOfBoundsException(index, s);
        }

        if (s < a.length) {
            System.arraycopy(a, index, a, index + 1, s - index);
        } else {
            // assert s == a.length;
            Object[] newArray = new Object[newCapacity(s)];
            System.arraycopy(a, 0, newArray, 0, index);
            System.arraycopy(a, index, newArray, index + 1, s - index);
            array = a = newArray;
        }
        a[index] = new SoftReference<E>(object);
        size = s + 1;
        modCount++;
    }

    private static int newCapacity(int currentCapacity) {
        int increment = (currentCapacity < (MIN_CAPACITY_INCREMENT / 2) ?
                MIN_CAPACITY_INCREMENT : currentCapacity >> 1);
        return currentCapacity + increment;
    }

    @Deprecated
	@Override
	public boolean addAll(Collection<? extends E> collection) {
//        Object[] newPart = collection.toArray();
//        int newPartSize = newPart.length;
//        if (newPartSize == 0) {
//            return false;
//        }
//        Object[] a = array;
//        int s = size;
//        int newSize = s + newPartSize; // If add overflows, arraycopy will fail
//        if (newSize > a.length) {
//            int newCapacity = newCapacity(newSize - 1);  // ~33% growth room
//            Object[] newArray = new Object[newCapacity];
//            System.arraycopy(a, 0, newArray, 0, s);
//            array = a = newArray;
//        }
//        System.arraycopy(newPart, 0, a, s, newPartSize);
//        size = newSize;
//        modCount++;
//        return true;
		return false;
	}

	@Deprecated
	@Override
    public boolean addAll(int index, Collection<? extends E> collection) {
//        int s = size;
//        if (index > s || index < 0) {
//            throwIndexOutOfBoundsException(index, s);
//        }
//        Object[] newPart = collection.toArray();
//        int newPartSize = newPart.length;
//        if (newPartSize == 0) {
//            return false;
//        }
//        Object[] a = array;
//        int newSize = s + newPartSize; // If add overflows, arraycopy will fail
//        if (newSize <= a.length) {
//             System.arraycopy(a, index, a, index + newPartSize, s - index);
//        } else {
//            int newCapacity = newCapacity(newSize - 1);  // ~33% growth room
//            Object[] newArray = new Object[newCapacity];
//            System.arraycopy(a, 0, newArray, 0, index);
//            System.arraycopy(a, index, newArray, index + newPartSize, s-index);
//            array = a = newArray;
//        }
//        System.arraycopy(newPart, 0, a, index, newPartSize);
//        size = newSize;
//        modCount++;
//        return true;
		return false;
	}

    static IndexOutOfBoundsException throwIndexOutOfBoundsException(int index, int size) {
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
    }

	@Override
	public void clear() {
		if (size != 0) {
			Arrays.fill(array, 0, size, null);
			size = 0;
			modCount++;
		}
	}

	@Override
	public Object clone() {
		try {
			SoftArrayList<?> result = (SoftArrayList<?>) super.clone();
			result.array = array.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public void ensureCapacity(int minimumCapacity) {
		Object[] a = array;
		if (a.length < minimumCapacity) {
			Object[] newArray = new Object[minimumCapacity];
			System.arraycopy(a, 0, newArray, 0, size);
			array = newArray;
			modCount++;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(int index) {
        if (index >= size) {
            throwIndexOutOfBoundsException(index, size);
        }
        return ((SoftReference<E>)array[index]).get();
    }

    /**
     * Returns the number of elements in this {@code ArrayList}.
     *
     * @return the number of elements in this {@code ArrayList}.
     */
    @Override public int size() {
        return size;
    }

    @Override public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Searches this {@code ArrayList} for the specified object.
     *
     * @param object
     *            the object to search for.
     * @return {@code true} if {@code object} is an element of this
     *         {@code ArrayList}, {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object object) {
        Object[] a = array;
        int s = size;
        if (object != null) {
            for (int i = 0; i < s; i++) {
                if (object.equals(((SoftReference<E>) a[i]).get())) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < s; i++) {
                if (a[i] == null || ((SoftReference<E>) a[i]).get() == null) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 单例包含，存在同种类的实例时返回true
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
	public boolean singleContains(Object object){
    	Object[] a = array;
        int s = size;
        if (object != null) {
            for (int i = 0; i < s; i++) {
                if (((SoftReference<E>) a[i]).get().getClass() == object.getClass()) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < s; i++) {
                if (a[i] == null || ((SoftReference<E>) a[i]).get() == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(Object object) {
        Object[] a = array;
        int s = size;
        if (object != null) {
            for (int i = 0; i < s; i++) {
                if (object.equals(((SoftReference<E>) a[i]).get())) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < s; i++) {
                if (a[i] == null || ((SoftReference<E>) a[i]).get() == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override public int lastIndexOf(Object object) {
        Object[] a = array;
        if (object != null) {
            for (int i = size - 1; i >= 0; i--) {
                if (object.equals(((SoftReference<E>) a[i]).get())) {
                    return i;
                }
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (a[i] == null || ((SoftReference<E>) a[i]).get() == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Removes the object at the specified location from this list.
     *
     * @param index
     *            the index of the object to remove.
     * @return the removed object.
     * @throws IndexOutOfBoundsException
     *             when {@code location < 0 || location >= size()}
     */
    @Override
    public E remove(int index) {
        Object[] a = array;
        int s = size;
        if (index >= s) {
            throwIndexOutOfBoundsException(index, s);
        }
        @SuppressWarnings("unchecked") E result = (E) a[index];
        System.arraycopy(a, index + 1, a, index, --s - index);
        a[s] = null;  // Prevent memory leak
        size = s;
        modCount++;
        return result;
    }

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object object) {
		Object[] a = array;
		int s = size;
		if (object != null) {
			for (int i = 0; i < s; i++) {
				if (object.equals(((SoftReference<E>) a[i]).get())) {
					System.arraycopy(a, i + 1, a, i, --s - i);
					a[s] = null;  // Prevent memory leak
					size = s;
					modCount++;
					return true;
				}
			}
		} else {
			for (int i = 0; i < s; i++) {
				if (a[i] == null || ((SoftReference<E>) a[i]).get() == null) {
					System.arraycopy(a, i + 1, a, i, --s - i);
					a[s] = null;  // Prevent memory leak
					size = s;
					modCount++;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		if (fromIndex == toIndex) {
			return;
		}
		Object[] a = array;
		int s = size;
		if (fromIndex >= s) {
			throw new IndexOutOfBoundsException("fromIndex " + fromIndex
					+ " >= size " + size);
		}
		if (toIndex > s) {
			throw new IndexOutOfBoundsException("toIndex " + toIndex
					+ " > size " + size);
		}
		if (fromIndex > toIndex) {
			throw new IndexOutOfBoundsException("fromIndex " + fromIndex
					+ " > toIndex " + toIndex);
		}

		System.arraycopy(a, toIndex, a, fromIndex, s - toIndex);
		int rangeSize = toIndex - fromIndex;
		Arrays.fill(a, s - rangeSize, s, null);
		size = s - rangeSize;
		modCount++;
	}

	@Override
	public E set(int index, E object) {
		Object[] a = array;
		if (index >= size) {
			throwIndexOutOfBoundsException(index, size);
		}
		@SuppressWarnings("unchecked")
		E result = ((SoftReference<E>) a[index]).get();
		a[index] = new SoftReference<E>(object);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray() {
		int s = size;
		Object[] result = new Object[s];
		
		for(int i = 0;i<s;i++){
			result[i] = ((SoftReference<E>) array[i]).get();
		}
		
//		System.arraycopy(array, 0, result, 0, s);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] contents) {
		int s = size;
		if (contents.length < s) {
			T[] newArray = (T[]) Array.newInstance(contents.getClass().getComponentType(), s);
			contents = newArray;
		}
		for(int i = 0;i<s;i++){
			contents[i] = ((SoftReference<T>) array[i]).get();
		}
		System.arraycopy(this.array, 0, contents, 0, s);
		if (contents.length > s) {
			contents[s] = null;
		}
		return contents;
	}

    /**
     * Sets the capacity of this {@code ArrayList} to be the same as the current
     * size.
     *
     * @see #size
     */
    public void trimToSize() {
        int s = size;
        if (s == array.length) {
            return;
        }
        if (s == 0) {
            array = OBJECT;
        } else {
            Object[] newArray = new Object[s];
            System.arraycopy(array, 0, newArray, 0, s);
            array = newArray;
        }
        modCount++;
    }

    @Override
    public Iterator<E> iterator() {
        return new SoftArrayListIterator();
    }

    private class SoftArrayListIterator implements Iterator<E> {
    	/** Number of elements remaining in this iteration */
    	private int remaining = size;

    	/** Index of element that remove() would remove, or -1 if no such elt */
    	private int removalIndex = -1;

    	/** The expected modCount value */
    	private int expectedModCount = modCount;

    	public boolean hasNext() {
    		return remaining != 0;
    	}

    	@SuppressWarnings("unchecked")
    	public E next() {
    		SoftArrayList<E> ourList = SoftArrayList.this;
    		int rem = remaining;
    		if (ourList.modCount != expectedModCount) {
    			throw new ConcurrentModificationException();
    		}
    		if (rem == 0) {
            	throw new NoSuchElementException();
    		}
    		remaining = rem - 1;
    		return ((SoftReference<E>) ourList.array[removalIndex = ourList.size - rem]).get();
    	}

        public void remove() {
            Object[] a = array;
            int removalIdx = removalIndex;
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (removalIdx < 0) {
                throw new IllegalStateException();
            }
            System.arraycopy(a, removalIdx + 1, a, removalIdx, remaining);
            a[--size] = null;  // Prevent memory leak
            removalIndex = -1;
            expectedModCount = ++modCount;
        }
    }

    @Override
    public int hashCode() {
        Object[] a = array;
        int hashCode = 1;
        for (int i = 0, s = size; i < s; i++) {
            @SuppressWarnings("unchecked")
			Object e = ((SoftReference<E>) a[i]).get();
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

	@Override
	public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SoftArrayList)) {
            return false;
        }
        SoftArrayList<?> that = (SoftArrayList<?>) o;
        int s = size;
        if (that.size() != s) {
            return false;
        }
        Object[] a = array;
        if (that instanceof RandomAccess) {
            for (int i = 0; i < s; i++) {
                @SuppressWarnings("unchecked")
				Object eThis = ((SoftReference<E>) a[i]).get();
				Object ethat = ((SoftReference<?>) that.get(i)).get();
                if (eThis == null ? ethat != null : !eThis.equals(ethat)) {
                    return false;
                }
            }
        } else {  // Argument list is not random access; use its iterator
            Iterator<?> it = that.iterator();
            for (int i = 0; i < s; i++) {
                @SuppressWarnings("unchecked")
				Object eThis = ((SoftReference<E>)a[i]).get();
                Object eThat = ((SoftReference<?>)it.next()).get();
                if (eThis == null ? eThat != null : !eThis.equals(eThat)) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
	private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(array.length);
        for (int i = 0; i < size; i++) {
            stream.writeObject(((SoftReference<E>)array[i]).get());
        }
    }

    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int cap = stream.readInt();
        if (cap < size) {
            throw new InvalidObjectException("Capacity: " + cap + " < size: " + size);
        }
        array = (cap == 0 ? OBJECT : new Object[cap]);
        for (int i = 0; i < size; i++) {
            array[i] = new SoftReference<E>((E) stream.readObject());
        }
    }
}
