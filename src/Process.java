import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

public class Process {
    private final UUID PID;
    private final int processLength;
    private final int processInitializationTime;
    private int startTime;
    private int completionTime;
    private String processName;
    private int completed;

    public Process(String processName, int processLength, int processInitializationTime) {
        this.PID = UUID.randomUUID();
        this.processName = processName;
        this.processLength = processLength;
        this.processInitializationTime = processInitializationTime;
        this.completed = 0;
        this.startTime = -1;
        this.completionTime = -1;
    }

    public static Process generateRandomProcess(int processInitializationTime) {
        Random r = new Random();
        return new Process("RandomProcess" + r.nextInt(), 1 + r.nextInt(9999), processInitializationTime);
    }

    public UUID getPID() {
        return PID;
    }

    public String getProcessName() {
        return processName;
    }

    public int getProcessLength() {
        return processLength;
    }

    public int getCompleted() {
        return completed;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getProcessInitializationTime() {
        return processInitializationTime;
    }

    public int getRemaining() {
        return this.processLength - this.completed;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public boolean advanceProcess(int units, int time) {
        this.startTime = (this.startTime < 0) ? time : this.startTime;
        this.completed += units;

        if(this.completed >= this.processLength) {
            this.completionTime = time;
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("PID:%s, initialized: %d, started: %d, completed: %d/%d", this.PID, this.processInitializationTime, this.startTime, this.completed, this.processLength);
    }

    public static class FCFSComparator implements Comparator<Process> {
        // Comparator which seeks to determine which process was initialized first, aka First Come, First Served.
        @Override
        public int compare(Process o1, Process o2) {
            return Integer.compare(o1.processInitializationTime, o2.processInitializationTime);
        }
    }

    public static class SJFwComparator implements Comparator<Process> {
        // Comparator which seeks to determine the process which will take the least amount of time to complete, aka Shortest Job First.
        @Override
        public int compare(Process o1, Process o2) {
            return Integer.compare(o1.getRemaining(), o2.getRemaining());
        }
    }

    public static class SJFComparator implements Comparator<Process> {
        // Comparator which seeks to determine the shorter process, aka Shortest Job First.
        @Override
        public int compare(Process o1, Process o2) {
            return Integer.compare(o1.getProcessLength(), o2.getProcessLength());
        }
    }
}