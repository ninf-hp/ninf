package bricks.util;

public class RingBuffer {
    protected int currentIndex = -1;
    protected Object[] buffer;
    protected boolean isFull = false;

    public RingBuffer() {
	buffer = new Object[128];
    }

    public RingBuffer(int size) {
	buffer = new Object[size];
    }

    public int size() {
	return buffer.length;
    }

    public void put(Object obj) {
	currentIndex++;
	if (currentIndex == buffer.length) {
	    isFull = true;
	    currentIndex = 0;
	}
	buffer[currentIndex] = obj;
    }

    public Object get(int index) {
	Object buf = null;
	if ((isFull == true) ||
	    ((currentIndex >= 0) && (currentIndex+1 > index))) {
	    int trackIndex = currentIndex - index;
	    if (trackIndex < 0) {
		trackIndex = buffer.length + trackIndex;
	    }
	    buf = buffer[trackIndex];
	}
	return buf;
    }

    public Object get() {
	//return buffer[currentIndex];
	Object object = null;
	if (currentIndex >= 0) {
	    object = buffer[currentIndex];
	}
	return object;
    }

    // debug
    public void printRingBuffer() {
	System.out.println("currentIndex = " + currentIndex + "\n");
	for (int i = 0 ; i < buffer.length ; i++) {
	    //System.out.println(currentIndex + " : " + 
	    //Format.format(buffer[i].time, 3) + " " +
	    //buffer[i].idle + " " + buffer[i].queueLength);
	    System.out.println(currentIndex + " : " + buffer[i]);
	}
    }
}
