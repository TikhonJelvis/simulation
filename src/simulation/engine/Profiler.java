package simulation.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A profiler that will take performance measurements from a certain thread.
 * 
 * @author Jacob Taylor
 * 
 */
public class Profiler implements Runnable {
	/**
	 * Get a string representing a StackTraceElement.
	 * 
	 * @param elem
	 *            the stack trace element to get a string from
	 * @return a string based on elem
	 */
	private String getMethodString(StackTraceElement elem) {
		return elem.getClassName() + "." + elem.getMethodName();
	}

	/**
	 * An entry for a method that tracks its sub-methods and keeps a count of
	 * the number of times it has been seen.
	 * 
	 * @author Jacob Taylor
	 * 
	 */
	private class MethodEntry implements Comparable<MethodEntry> {
		private String methodName;
		private int count;
		private int depth;
		// sub-methods
		private Map<String, MethodEntry> calls;

		public MethodEntry(String methodName, int depth) {
			this.methodName = methodName;
			this.depth = depth;
			calls = new HashMap<String, MethodEntry>();
		}

		public int compareTo(MethodEntry other) {
			// compare by count
			if (count < other.count)
				return 1;
			else if (count == other.count)
				return 0;
			return -1;
		}

		public void addCount(StackTraceElement[] trace, int i) {
			// i is the index of the NEXT method, not this one.
			++count;
			if (i < trace.length) { // has sub-methods
				String method = getMethodString(trace[i]);
				MethodEntry sub;
				if (calls.containsKey(method)) { // already have the given
													// sub-method
					sub = calls.get(method);
				} else {
					// create a new one with higher depth
					sub = new MethodEntry(method, depth + 1);
					calls.put(method, sub);
				}
				// now increment the sub-method
				sub.addCount(trace, i + 1);
			}
		}

		public void writeTo(StringBuilder sb, double totalCalls) {
			if (methodName != null) {
				// indentation
				for (int i = 0; i < depth; ++i) {
					sb.append(' ');
				}
				// have the method name first, then the count ratio
				sb.append(String.format("%-" + (50 - depth) + "s %.5f\n",
						methodName, count / totalCalls));
			}
			// now write submethods
			Object[] subs = calls.values().toArray();
			Arrays.sort(subs);
			for (Object sub : subs) {
				((MethodEntry) sub).writeTo(sb, totalCalls);
			}
		}
	}

	// thread to monitor
	private Thread monitoredThread;
	// all method entries
	private MethodEntry counts;
	// number of traces
	private int traces;
	// is it running?
	private boolean running;

	/**
	 * Create a profiler that will monitor a thread. Does not start the
	 * profiler.
	 * 
	 * @param toMonitor
	 *            the thread to monitor
	 */
	public Profiler(Thread toMonitor) {
		monitoredThread = toMonitor;
		counts = new MethodEntry(null, -1);
	}

	private void trace() {
		StackTraceElement[] trace = monitoredThread.getStackTrace();
		// reverse, so that the ones at the bottom of the call stack are in
		// front
		int mid = trace.length / 2;
		for (int i = 0; i < mid; ++i) {
			StackTraceElement temp = trace[i];
			trace[i] = trace[trace.length - 1 - i];
			trace[trace.length - 1 - i] = temp;
		}
		counts.addCount(trace, 0);
	}

	public void run() {
		while (running) {
			trace();
			++traces;
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				return;
			}
		}
	}

	/**
	 * Start the profiler.
	 */
	public void start() {
		running = true;
		new Thread(this).start();
	}

	/**
	 * Stop the profiler.
	 */
	public void stop() {
		running = false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		counts.writeTo(sb, traces);
		return sb.toString();
	}
}
