import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {
    static Comparator<Process> QueueComparator = new Process.SJFwComparator();
    static PriorityQueue<Process> processQueue = new PriorityQueue<>(QueueComparator);
    static ArrayList<Process> completedProcesses = new ArrayList<>();
    static int iterations = 1_000_000;
    static int initialProcesses = 50;
    static Random r = new Random();

    public static void generateProcesses(int t) {
        double gaussianSample = r.nextGaussian() * (iterations / 6.0) + (iterations / 2.0);

        if(gaussianSample < iterations && gaussianSample > 0 && t > gaussianSample) {
            for(int i = 0; i < r.nextInt(100); i++) {
                processQueue.add(Process.generateRandomProcess(t));
            }
        }
    }

    public static void main(String[] args) {
        for(int i = 0; i < initialProcesses; i++) {
            Process randomProcess = Process.generateRandomProcess(0);

            processQueue.add(randomProcess);
        }

        for(int t = 0; t < iterations; t++) {
            generateProcesses(t);

            Process current = processQueue.peek();

            if(current == null) {
                continue;
            }

            if(current.advanceProcess(1, t)) {
                processQueue.remove(current);
                completedProcesses.add(current);
            }
        }

        int aggregateSumProcessLength = completedProcesses.stream()
                .map(Process::getProcessLength)
                .mapToInt(Integer::intValue)
                .sum();

        double avgCompletion = completedProcesses.stream()
                .map(process -> process.getCompletionTime() - process.getStartTime())
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);

        double avgWaitTime = completedProcesses.stream()
                .map(process -> process.getStartTime() - process.getProcessInitializationTime())
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);

        System.out.printf("Average completion time for a process: %f%nAverage process wait time since initialization: %f%nTotal sum of all process lengths: %d%nTotal processes served: %d", avgCompletion, avgWaitTime, aggregateSumProcessLength, completedProcesses.size());
    }
}